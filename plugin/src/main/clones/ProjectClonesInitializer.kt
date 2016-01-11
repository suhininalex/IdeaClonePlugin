package clones

import com.intellij.openapi.application.Application
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.ElementType
import com.intellij.psi.tree.TokenSet
import com.suhininalex.clones.*
import java.awt.*
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore

object ProjectClonesInitializer {

    private val map = HashMap<Project, CloneManager>()

    @Synchronized fun getInstance(project: Project): CloneManager {
        var cloneManager: CloneManager? = map[project]
        if (cloneManager != null) return cloneManager
        cloneManager = initializeCloneManager(project)
        map.put(project, cloneManager)
        return cloneManager
    }

    fun initializeCloneManager(project: Project): CloneManager {
        val cloneManager = CloneManager(50)

        val files = getAllPsiJavaFiles(project)
        val progressView = ProgressView(project, files.size)
        val task = files.interruptableForeach {
            println("foreach!")
            it.processPsiFile(cloneManager)
            progressView.next(it.name)
        }

        invokeLater(wrapAsReadTask {
            task.call()
            progressView.done()
        })

        EventQueue.invokeLater {
            val r = progressView.showAndGet()
            if (!r) task.interrupt()
        }

        return cloneManager
    }

    private fun PsiFile.processPsiFile(cloneManager: CloneManager) =
            findTokens(TokenSet.create(ElementType.METHOD)).forEach {
            cloneManager.addMethod(it as PsiMethod)
        }

    private fun getAllPsiJavaFiles(project: Project)=
         PsiManager.getInstance(project).findDirectory(project.baseDir)!!.getPsiJavaFiles()

    private fun PsiDirectory.getPsiJavaFiles(accumulator: MutableList<PsiJavaFile> = LinkedList()): MutableList<PsiJavaFile> {
        files.stream().filter { it is PsiJavaFile }.forEach { accumulator.add(it as PsiJavaFile) }
        subdirectories.forEach { it.getPsiJavaFiles(accumulator) }
        return accumulator
    }

    private fun PsiElement.findTokens(tokenSet: TokenSet, accumulator: MutableList<PsiElement> = LinkedList()): List<PsiElement> {
        if (tokenSet.contains(node?.elementType)) {
            accumulator.add(this)
        } else children.forEach {
            it.findTokens(tokenSet, accumulator)
        }
        return accumulator
    }
}
