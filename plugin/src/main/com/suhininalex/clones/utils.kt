package com.suhininalex.clones

import com.intellij.psi.PsiMethod
import com.suhininalex.suffixtree.Node
import java.util.*
import java.util.concurrent.locks.Lock
import java.util.stream.Stream

//TODO обеспечить уникальность (все еще нет!)
fun PsiMethod.getStringId() =
        containingFile.containingDirectory.name + "." +
        containingClass!!.name + "." +
        name + ":"+
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

fun Node.riseIterator() = object : Iterator<Node> {
    var node = this@riseIterator
    override fun hasNext() = node.parentEdge!=null
    override fun next(): Node {
        node=node.parentEdge.parent
        return node
    }
}