package clones

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.impl.source.tree.ElementType.METHOD
import com.intellij.psi.tree.TokenSet
import com.suhininalex.clones.*
import nl.komponents.kovenant.CancelablePromise
import nl.komponents.kovenant.task
import java.awt.EventQueue
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object ProjectClonesInitializer {

    private val map = ConcurrentHashMap<Project, CloneManager>()

    fun getInstance(project: Project) =
        map.computeIfAbsent(project) {initializeCloneManager(project)}

    fun initializeCloneManager(project: Project): CloneManager {
        val cloneManager = CloneManager(50)
        val files = project.getAllPsiJavaFiles().toList()
        val progressView = ProgressView(project, files.size)

        val initialize = task {
            files.forEach {
                Application.runReadAction {
                    cloneManager.processPsiFile(it)
                    progressView.next(it.name)
                }
            }
        } success {
            progressView.done()
        }

        EventQueue.invokeLater {
            if (! progressView.showAndGet())
                (initialize as CancelablePromise).cancel(InterruptedException("cancel"))
        }

        initialize.get()
        return cloneManager
    }

    private fun CloneManager.processPsiFile(file: PsiFile) =
        file.findTokens(TokenSet.create(METHOD)).forEach {
            addMethod(it as PsiMethod)
        }
}