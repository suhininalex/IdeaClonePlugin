package clones

import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.ElementType
import com.intellij.psi.tree.TokenSet
import com.suhininalex.clones.*
import nl.komponents.kovenant.CancelablePromise
import nl.komponents.kovenant.task
import nl.komponents.kovenant.then
import stream
import java.awt.EventQueue
import java.util.*

object ProjectClonesInitializer {

    private val map = HashMap<Project, CloneManager>()

    @Synchronized fun getInstance(project: Project) =
        map[project] ?:
            (initializeCloneManager(project)).apply {
                map.put(project, this)
            }

    @Synchronized fun initializeCloneManager(project: Project): CloneManager {
        val cloneManager = CloneManager(50)
        val files = getAllPsiJavaFiles(project)
        val progressView = ProgressView(project, files.size)

        val initialize = task {
            files.forEach {
                Application.runReadAction {
                    it.processPsiFile(cloneManager)
                    progressView.next(it.name)
                }
            }
        } success {
            progressView.done()
        }

        EventQueue.invokeAndWait {
            if (! progressView.showAndGet())
                (initialize as CancelablePromise).cancel(InterruptedException("cancel"))
        }

        if (initialize.isSuccess()) return cloneManager
        else throw initialize.getError()
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
