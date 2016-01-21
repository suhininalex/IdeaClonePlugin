package com.suhininalex.clones

import clones.ProjectClonesInitializer
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiMethod
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

infix fun <T> Collection<T>.equal(other: Collection<T>): Boolean {
    if (this.size!=other.size) return false
    val otherIterator = other.iterator()
    forEach {
        if (it != otherIterator.next()) return false
    }
    return true
}
fun Node.descTraverser() = riseTraverser().reversed()

fun <T> Iterator<T>?.hasNext() = if (this!=null) hasNext() else false

fun <T> Iterable<T>.isEmpty() = !iterator().hasNext()

fun <T> Stream<T>.isEmpty() = iterator().hasNext()

fun <T> Stream<T>.toList(): List<T> = collect(Collectors.toList()!!)

fun <T> Iterator<T>.nextOrNull() = if (hasNext()) next() else null