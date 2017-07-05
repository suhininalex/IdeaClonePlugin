package com.suhininalex.clones.core.utils

import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.roots.TestSourcesFilter
import com.intellij.openapi.util.Computable
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.ElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import com.suhininalex.clones.ide.FileListener
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.deferred
import java.awt.EventQueue
import java.lang.Exception

val Application: Application
    get() = ApplicationManager.getApplication()

fun <T> Application.readAction(action: () -> T): T =
    Application.runReadAction(Computable(action))

val Project.sourceFiles: Sequence<PsiFile>
    get() = Application.readAction {
        PsiManager.getInstance(this).findDirectory(baseDir)!!
            .depthFirstTraverse { Application.readAction { it.subdirectories.asSequence() } }
            .flatMap { Application.readAction { it.files.asSequence() } }
            .filter { Application.readAction { it.isSourceFile() } }
    }

fun Project.addBulkFileListener(bulkFileListener: BulkFileListener){
    messageBus.connect().subscribe(VirtualFileManager.VFS_CHANGES, bulkFileListener)
}

val Project.fileIndex: ProjectFileIndex
        get() {
            return ProjectFileIndex.getInstance(this)
        }

fun PsiFile.isSourceFile(): Boolean =
        project.fileIndex.isContentSourceFile(virtualFile)

fun PsiFile.isTestFile(): Boolean =
        TestSourcesFilter.isTestSources(virtualFile, project)


fun PsiElement.findTokens(filter: TokenSet): Sequence<PsiElement> =
        this.leafTraverse({it in filter}) {it.children.asSequence()}

operator fun TokenSet.contains(element: PsiElement?): Boolean = this.contains(element?.node?.elementType)

fun PsiElement.asSequence(): Sequence<PsiElement> =
        this.depthFirstTraverse { it.children.asSequence() }.filter { it.children.isEmpty() }

private val javaTokenFilter = TokenSet.create(
        ElementType.WHITE_SPACE, ElementType.DOC_COMMENT, ElementType.C_STYLE_COMMENT, ElementType.END_OF_LINE_COMMENT, ElementType.REFERENCE_PARAMETER_LIST, ElementType.MODIFIER_LIST
)

fun isNoiseElement(psiElement: PsiElement): Boolean =
    psiElement in javaTokenFilter || psiElement.textLength == 0

class BackgroundTask<T>(val name: String, cancelAble: Boolean, private val task: (ProgressIndicator) -> T) : Task.Modal(null, name, cancelAble){
    private val deferred = deferred<T, Exception>()

    val promise = deferred.promise

    override fun run(progressIndicator: ProgressIndicator) {
        try {
            val result = task(progressIndicator)
            deferred.resolve(result)
        } catch (e: Exception){
            deferred.reject(e)
        }
    }
}

fun <T> ProgressManager.backgroundTask(name: String, cancelAble: Boolean = true, task: (ProgressIndicator) -> T): Promise<T, Exception> {
    val task = BackgroundTask(name, cancelAble, task)
    EventQueue.invokeLater { run(task) }
    return task.promise
}

class ListWithProgressBar<out T>(val name: String, val cancelAble: Boolean, val list: List<T>){
    fun filter(predicate: (T) -> Boolean): Promise<List<T>, Exception> {
        return ProgressManager.getInstance().backgroundTask(name, cancelAble){ progressIndicator ->
            list.filterIndexed { i, it ->
                if (progressIndicator.isCanceled) throw InterruptedException()
                progressIndicator.fraction = i.toDouble()/ list.size
                predicate(it)
            }
        }
    }

    fun foreach(task: (T) -> Unit): Promise<Unit, Exception> {
        return ProgressManager.getInstance().backgroundTask(name, cancelAble){ progressIndicator ->
            list.forEachIndexed { i, it ->
                if (progressIndicator.isCanceled) throw InterruptedException()
                progressIndicator.fraction = i.toDouble()/ list.size
                task(it)
            }
        }
    }

    fun <R> flatMap(f: (T) -> List<R>): Promise<List<R>, Exception> {
        return ProgressManager.getInstance().backgroundTask(name, cancelAble){ progressIndicator ->
            var i = 0
            list.flatMap {
                if (progressIndicator.isCanceled) throw InterruptedException()
                progressIndicator.fraction = i++.toDouble()/ list.size
                f(it)
            }
        }
    }

}

fun <T> List<T>.withProgressBar(name: String, cancelAble: Boolean = true): ListWithProgressBar<T> =
        ListWithProgressBar(name, cancelAble, this)

fun PsiElement.nextLeafElement(): PsiElement? {
    var current = this
    while (current.nextSibling == null){
        if (current.parent is PsiFile) return null
        current = current.parent
    }

    current = current.nextSibling
    while (current.firstChild != null)
        current = current.firstChild
    return current
}

fun PsiElement.prevLeafElement(): PsiElement {
    var current = this
    while (current.prevSibling == null)
        current = current.parent
    current = current.prevSibling
    while (current.lastChild != null)
        current = current.lastChild
    return current
}

fun PsiElement.firstEndChild(): PsiElement {
    var current = this
    while (current.firstChild != null) current = current.firstChild
    return current
}

fun PsiElement.lastEndChild(): PsiElement {
    var current = this
    while (current.lastChild != null) current = current.lastChild
    return current
}

val PsiElement?.method: PsiMethod?
    get() =
        if (this is PsiMethod) {
            this
        } else {
            PsiTreeUtil.getParentOfType(this, PsiMethod::class.java)
        }

val CurrentProject: Project?
    get() =
        ProjectManager.getInstance().openProjects.firstOrNull()?.run {
            if (isDisposed) null
            else this
        }

val Project.toolWindowManager: ToolWindowManager
    get() = ToolWindowManager.getInstance(this)