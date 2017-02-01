package com.suhininalex.clones.core

import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.ElementType.*
import com.intellij.psi.tree.TokenSet
import com.suhininalex.clones.ide.ProjectClonesInitializer
import com.suhininalex.suffixtree.Edge
import com.suhininalex.suffixtree.Node
import iterate
import java.awt.EventQueue
import java.lang.IllegalArgumentException

fun PsiMethod.getStringId() =
        containingFile.containingDirectory.name + "." +
        containingClass!!.name + "." +
        name + "."+
        parameterList;

val Edge.length: Int
    get() = end - begin + 1

fun Clone.getTextRangeInMethod(offset: Int) = TextRange(firstElement.getTextRange().startOffset - offset, lastElement.getTextRange().endOffset-offset)

fun Project.getCloneManager() = ProjectClonesInitializer.getInstance(this)

fun Token.getTextRange() = source.textRange

fun Node.lengthToRoot() =
    riseTraverser().sumBy { it.parentEdge?.length ?: 0 }

fun <T> callInEventQueue(body: ()->T): T {
    if (EventQueue.isDispatchThread()) return body()
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
        node = node?.parentEdge?.parent
        result
    }
}

fun Node.descTraverser() = riseTraverser().reversed()

fun <T> T.depthFirstTraverse(children: (T) -> Sequence<T>): Sequence<T> =
       sequenceOf(this) + children(this).flatMap { it.depthFirstTraverse(children) }

fun <T> T.depthFirstTraverse(recursionFilter: (T)-> Boolean, children: (T) -> Sequence<T>) =
        this.depthFirstTraverse { if (recursionFilter(it)) children(it) else sequenceOf() }

fun <T> T.leafTraverse(isLeaf: (T)-> Boolean, children: (T) -> Sequence<T>) =
    this.depthFirstTraverse ({ ! isLeaf(it) }, children).filter { isLeaf(it) }

fun Project.getAllPsiJavaFiles() =
    PsiManager.getInstance(this).findDirectory(baseDir)!!.getPsiJavaFiles()

fun PsiDirectory.getPsiJavaFiles(): Sequence<PsiJavaFile> =
    this.depthFirstTraverse { it.subdirectories.asSequence() }.flatMap { it.files.asSequence() }.filter { it is PsiJavaFile }.map { it as PsiJavaFile }

fun PsiElement.findTokens(filter: TokenSet): Sequence<PsiElement> =
    this.leafTraverse({it in filter}) {it.children.asSequence()}

operator fun TokenSet.contains(element: PsiElement?): Boolean = this.contains(element?.node?.elementType)

fun PsiElement.asSequence(): Sequence<PsiElement> =
    this.depthFirstTraverse { it.children.asSequence() }

fun <T> times(times: Int, provider: ()-> Sequence<T>): Sequence<T> =
    (1..times).asSequence().flatMap { provider() }

infix fun <T> Sequence<T>.equalContent(another: Sequence<T>) =
    zip(another).all { (a,b) -> a == b }

fun CloneClass.tokenSequence(): Sequence<Token> =
    treeNode.descTraverser().asSequence().map { it.parentEdge }.filter { it != null }.flatMap(Edge::asSequence)

fun Edge.asSequence(): Sequence<Token> {
    if (isTerminal) {
        throw IllegalArgumentException("You should never call this method for terminating edge.")
    } else {
        return (sequence.subList(begin, end + 1) as MutableList<Token>).asSequence()
    }
}

val javaTokenFilter = TokenSet.create(
        WHITE_SPACE, SEMICOLON, DOC_COMMENT, C_STYLE_COMMENT, END_OF_LINE_COMMENT, RPARENTH, LPARENTH, RBRACE, LBRACE, CODE_BLOCK, EXPRESSION_LIST
    )

val Edge.isTerminal: Boolean
    get() = this.terminal == null

fun <T> Sequence<T>.isEmpty() =
        iterator().hasNext()

fun <T> Sequence<T>.peek(operation: (T) -> Unit): Sequence<T> =
    filter {
        operation(it)
        true
    }

fun <T> Sequence<T>.peekIndexed(operation: (Int, T) -> Unit): Sequence<T> =
        filterIndexed { i, value ->
            operation(i, value)
            true
        }