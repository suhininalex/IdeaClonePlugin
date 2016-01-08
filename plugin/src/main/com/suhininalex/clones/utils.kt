package com.suhininalex.clones

import com.intellij.psi.PsiMethod
import com.suhininalex.suffixtree.Edge
import com.suhininalex.suffixtree.Node
import java.util.*
import java.util.concurrent.locks.Lock

fun PsiMethod.getStringId() =
        containingFile.containingDirectory.name + "." +
        containingClass!!.name + "." +
        name + "."+
        parameterList;

fun <E> buildLinkedList(init: LinkedList<E>.() -> Unit): List<E> {
    val list = LinkedList<E>()
    list.init()
    return list
}

infix inline fun <T> Lock.use(body:()->T):T =
    try {
        this.lock()
        body()
    } finally {
        this.unlock()
    }

inline fun <E> MutableList<E>.addIf(element: E, condition:(element: E)->Boolean){
    if (condition(element)) add(element)
}

fun Node.riseTraverser() = object : Iterable<Node> {
    override fun iterator() = object : Iterator<Node> {

        var node: Node? = this@riseTraverser

        override fun hasNext() = node!=null

        override fun next(): Node {
            val result = node!!
            node=node?.parentEdge?.parent
            return result
        }
    }
}

fun Edge.getLength() = end - begin + 1