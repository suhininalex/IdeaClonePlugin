package com.suhininalex.clones

import clones.ProjectClonesInitializer
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiMethod
import com.suhininalex.suffixtree.Edge
import com.suhininalex.suffixtree.Node
import java.awt.EventQueue
import java.util.*
import java.util.stream.StreamSupport

fun PsiMethod.getStringId() =
        containingFile.containingDirectory.name + "." +
        containingClass!!.name + "." +
        name + "."+
        parameterList;

fun Edge.getLength() = end - begin + 1

fun String.abbrevate(size: Int) =
    if (length > size) substring(0,size)+"..." else this

fun Clone.getTextRange(offset: Int = 0) =
        TextRange(firstElement.getTextRange().startOffset - offset, lastElement.getTextRange().endOffset - offset)

fun Clone.getTextRangeInMethod() = getTextRange(firstElement.method.textRange.startOffset)

fun Project.getCloneManager() = ProjectClonesInitializer.getInstance(this)

fun Token.getTextRange() = source.textRange

fun <T> Iterable<T>.stream() =
    StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED), false)

fun <T> Array<T>.stream() = Arrays.stream(this)

fun Node.lengthToRoot() =
        riseTraverser().sumBy { it.parentEdge?.getLength() ?: 0  }

class ret<T>(val result:T) {
    inline infix public fun thenDo(body: T.()->Unit): T{
        result.body()
        return result
    }
}

class InterruptableCallable(val callable:(Boolean)->Unit) {
    @Volatile private  var interrupt = false
    fun interrupt() {
        interrupt = true
    }
    operator fun invoke(): Status {
        callable(interrupt)
        return if (interrupt) Status.Interrupted else Status.Done
    }
}

enum class Status{
    Done, Interrupted
}

fun <T> Iterable<T>.interruptableForeach(body: (T)->Unit) =
    InterruptableCallable{ interrupted ->
        this.forEach {
            body(it)
            if (interrupted) return@forEach
        }
        if (interrupted) Status.Interrupted else Status.Done
}

fun <T> callInEventQueue(body: ()->T): T {
    var result: T? = null
    EventQueue.invokeAndWait { result = body() }
    return result!!
}

val Application: Application
    get() = ApplicationManager.getApplication()

fun <E> Stack<E>.popEach(body: Stack<E>.(E) -> Unit){
    while(!empty()) body(pop())
}

fun <E> stack(initial: E) = ret(Stack<E>()) thenDo { push(initial) }

fun <T> iterate(f:()->T?) = object : Iterator<T>{
    var next :T? = f()
    override fun hasNext() = next!=null
    override fun next():T {
        val result = next ?: throw NoSuchElementException()
        next = f()
        return result
    }
}

fun Node.riseTraverser() = object: Iterable<Node> {
    var node: Node? = this@riseTraverser
    override fun iterator() = iterate { ret(node) thenDo { node=node?.parentEdge?.parent } }
}

inline fun <E> MutableList<E>.addIf(element: E, condition:(element: E)->Boolean){
    if (condition(element)) add(element)
}