package com.suhininalex.clones

import clones.ProjectClonesInitializer
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.tree.TokenSet
import com.suhininalex.suffixtree.Edge
import com.suhininalex.suffixtree.Node
import iterate
import stream
import java.awt.EventQueue
import java.util.stream.Collectors
import java.util.stream.Stream

fun PsiMethod.getStringId() =
        containingFile.containingDirectory.name + "." +
        containingClass!!.name + "." +
        name + "."+
        parameterList;

fun Edge.getLength() = end - begin + 1

fun Clone.getTextRange(offset: Int = 0) =
        TextRange(firstElement.getTextRange().startOffset - offset, lastElement.getTextRange().endOffset - offset)

fun Clone.getTextRangeInMethod() = getTextRange(firstElement.method.textRange.startOffset)

fun Project.getCloneManager() = ProjectClonesInitializer.getInstance(this)

fun Token.getTextRange() = source.textRange

fun Node.lengthToRoot() =
        riseTraverser().sumBy { it.parentEdge?.getLength() ?: 0  }

fun <T> callInEventQueue(body: ()->T): T {
    var result: T? = null
    EventQueue.invokeAndWait { result = body() }
    return result!!
}

val Application: Application
    get() = ApplicationManager.getApplication()


fun Node.riseTraverser() = object: Iterable<Node> {
    var node: Node? = this@riseTraverser
    override fun iterator() = iterate {
        val result = node
        node=node?.parentEdge?.parent
        result
    }
}

fun Node.descTraverser() = riseTraverser().reversed()

fun <T> Iterator<T>?.hasNext() = if (this!=null) hasNext() else false

fun <T> Iterable<T>.isEmpty() = !iterator().hasNext()

fun <T> Stream<T>.isEmpty() = iterator().hasNext()

fun <T> Stream<T>.toList(): List<T> = collect(Collectors.toList()!!)

fun <T> Iterator<T>.nextOrNull() = if (hasNext()) next() else null

fun <T> Stream<out T>.concat(stream: Stream<out T>) = Stream.concat(this, stream)

fun <T> T.depthFirstTraverse(children: (T) -> Stream<T>): Stream<T> =
    Stream.of(this).concat( children(this).flatMap { it.depthFirstTraverse(children) } )

fun Project.getAllPsiJavaFiles() =
    PsiManager.getInstance(this).findDirectory(baseDir)!!.getPsiJavaFiles()

fun PsiDirectory.getPsiJavaFiles(): Stream<PsiJavaFile> =
    this.depthFirstTraverse { it.subdirectories.stream() }.flatMap { it.files.stream() }.filter { it is PsiJavaFile }.map { it as PsiJavaFile }

fun PsiElement.findTokens(filter: TokenSet): Stream<PsiElement> =
    this.depthFirstTraverse { if (it !in filter) it.children.stream() else Stream.empty() }.filter { it in filter }

operator fun TokenSet.contains(element: PsiElement?): Boolean = this.contains(element?.node?.elementType)

fun PsiElement.asStream(filter: TokenSet): Stream<PsiElement> =
    this.depthFirstTraverse { if (it !in filter) it.children.stream() else Stream.empty()  }