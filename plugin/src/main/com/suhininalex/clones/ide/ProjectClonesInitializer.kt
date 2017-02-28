package com.suhininalex.clones.ide

import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.ElementType.METHOD
import com.intellij.psi.tree.TokenSet
import com.suhininalex.clones.core.*
import com.suhininalex.clones.core.utils.*
import java.util.concurrent.ConcurrentHashMap

object ProjectClonesInitializer {

    private val map = ConcurrentHashMap<Project, CloneManager>()

    fun getInstance(project: Project) =
        map.computeIfAbsent(project) { initializeCloneManager(project) }

    fun initializeCloneManager(project: Project): CloneManager {
        val files: List<PsiJavaFile> = Application.runReadAction ( Computable {
            project.getAllPsiJavaFiles().toList()
        })
        val cloneManager = CloneManager()

        val progressManager = ProgressManager.getInstance()
        val task = {
            files.forEachIndexed { i, psiJavaFile ->
                if (progressManager.progressIndicator.isCanceled) throw InterruptedException()
                progressManager.progressIndicator.fraction = i.toDouble()/files.size
                Application.runReadAction { cloneManager.processPsiFile(psiJavaFile) }
            }
        }

        val succeed = callInEventQueue {
            progressManager.runProcessWithProgressSynchronously(task, "Building suffix trie...", true, project)
        }

        if (! succeed) throw InterruptedException()

        return cloneManager
    }

    private fun CloneManager.processPsiFile(file: PsiFile) =
        file.findTokens(TokenSet.create(METHOD)).forEach {
            addMethod(it as PsiMethod)
        }
}