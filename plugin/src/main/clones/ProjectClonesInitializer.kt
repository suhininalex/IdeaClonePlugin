package clones

import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.ElementType
import com.intellij.psi.tree.TokenSet
import com.suhininalex.clones.CloneManager
import com.suhininalex.clones.stream
import com.suhininalex.clones.wrapAsReadTask
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
        val executor = Executors.newSingleThreadExecutor()
        val semaphore = Semaphore(0)

        val files = getAllPsiJavaFiles(project)
        val progressView = ProgressView(project, files.size)
        val task = {
            try {
                processFiles(cloneManager, files, progressView)
                progressView.setAsProcessing()
                progressView.done()
            } catch (e: InterruptedException) {
                /* Canceled! */
            } finally {
                semaphore.release()
            }
        }
        executor.execute(wrapAsReadTask(task))
        EventQueue.invokeLater { progressView.showAndGet() }
        try {
            semaphore.acquire()
        } catch (e: InterruptedException) {
            throw IllegalStateException("Illegal state of clones due interruption.")
        }

        return cloneManager
    }

    @Throws(InterruptedException::class)
    private fun processFiles(cloneManager: CloneManager, files: List<PsiFile>, progressView: ProgressView) =
        files.forEach {
            if (progressView.status === ProgressView.Status.Canceled) throw InterruptedException("Task was canceled.")
            it.processPsiFile(cloneManager)
            progressView.next(it.name)
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
