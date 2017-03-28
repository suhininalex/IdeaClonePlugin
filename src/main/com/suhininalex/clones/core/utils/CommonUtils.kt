package com.suhininalex.clones.core.utils

import java.awt.EventQueue
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.MutableTreeNode
import javax.swing.tree.TreeNode


fun String.abbreviate(length: Int) =
        "${take(length)}..."

fun <T> Sequence<Sequence<T>>.zipped(): List<List<T>>{
    val result = ArrayList<ArrayList<T>>()
    forEach {
        it.forEachIndexed { i, it ->
            if (i >= result.size) result.add(ArrayList<T>())
            result[i].add(it)
        }
    }
    return result
}

fun <T> T.depthFirstTraverse(children: (T) -> Sequence<T>): Sequence<T> =
        sequenceOf(this) + children(this).flatMap { it.depthFirstTraverse(children) }

fun <T> T.depthFirstTraverse(recursionFilter: (T)-> Boolean, children: (T) -> Sequence<T>) =
        this.depthFirstTraverse { if (recursionFilter(it)) children(it) else emptySequence() }

fun <T> T.leafTraverse(isLeaf: (T)-> Boolean, children: (T) -> Sequence<T>) =
        this.depthFirstTraverse ({ ! isLeaf(it) }, children).filter { isLeaf(it) }

fun <T> times(times: Int, provider: ()-> Sequence<T>): Sequence<T> =
        (1..times).asSequence().flatMap { provider() }

infix fun <T> Sequence<T>.equalContent(another: Sequence<T>) =
        zip(another).all { (a,b) -> a == b }

fun <T> Sequence<T>.isEmpty() =
        iterator().hasNext()

inline fun <E> MutableList<E>.addIf(element: E, condition:(element: E)->Boolean){
    if (condition(element)) add(element)
}

fun <T> iterate(f:()->T?) = object : Iterator<T>{
    var next :T? = f()
    override fun hasNext() = next!=null
    override fun next():T {
        val result = next ?: throw NoSuchElementException()
        next = f()
        return result
    }
}

fun <T> Sequence<T>.areEqual(): Boolean {
    val first = first()
    return all { it == first }
}

/**
 * Enshure ranges have no intersections. Unite them if they do.
 */
fun List<IntRange>.uniteRanges(): List<IntRange> {
    if (size < 2) return this
    val sorted = sortedBy { it.start }.asSequence()
    val result = ArrayList<IntRange>()
    val first = sorted.first()
    var lastLeft = first.start
    var lastRight = first.endInclusive
    sorted.forEach {
        if (it.endInclusive <= lastRight ) {
            // skip
        } else if (it.start <= lastRight)  {
            lastRight = it.endInclusive
        } else {
            result.add(lastLeft..lastRight)
            lastLeft = it.start
            lastRight = it.endInclusive
        }
    }
    result.add(lastLeft..lastRight)
    return result
}

val IntRange.length: Int
    get() = endInclusive - start + 1

fun <T> callInEventQueue(body: ()->T): T {
    if (EventQueue.isDispatchThread()) return body()
    var result: T? = null
    EventQueue.invokeAndWait { result = body() }
    return result!!
}

fun SwingTreeNode(userObject: Any, children: List<MutableTreeNode>): DefaultMutableTreeNode {
    return DefaultMutableTreeNode(userObject).apply {
        children.forEach {
            this.add(it)
        }
    }
}

fun doubleClickListener(handler: (MouseEvent) -> Unit): MouseAdapter =
     object : MouseAdapter() {
        override fun mouseClicked(e: MouseEvent) {
            if (e.clickCount == 2) handler(e)
        }
    }

fun DefaultMutableTreeNode.allNodes(): Sequence<DefaultMutableTreeNode> =
        depthFirstTraverse { it.children }

/**
 * Use if you can enshure there are no TreeNodes except DefaultMutableTreeNode
 */
@Suppress("UNCHECKED_CAST")
val DefaultMutableTreeNode.children: Sequence<DefaultMutableTreeNode>
    get() = children().asSequence() as Sequence<DefaultMutableTreeNode>

operator fun Boolean.plus(other: Boolean): Boolean =
        this || other