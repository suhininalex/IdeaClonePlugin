package clones

import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.ElementType
import com.intellij.psi.tree.TokenSet
import com.suhininalex.clones.*
import net.suhininalex.kotlin.concurrent.interruptableForeach
import stream
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

        val task = files.interruptableForeach {
            it.processPsiFile(cloneManager)
            progressView.next(it.name)
        }

        val windowResult = callInEventQueue {
            progressView.showAndGetOk()
        }

        Application.runReadAction {
            if (task()) progressView.done()
        }

        if (!windowResult.resultSync) task.interrupt()

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
