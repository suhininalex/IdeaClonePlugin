package com.suhininalex.clones.core.clonefilter

import com.intellij.psi.impl.source.tree.ElementType
import com.intellij.psi.tree.TokenSet
import com.suhininalex.clones.core.*
import com.suhininalex.suffixtree.Edge
import com.suhininalex.suffixtree.Node

object SubSequenceFilter : CloneClassFilter {
    override fun isAllowed(cloneClass: CloneClass?): Boolean {
        cloneClass ?: return false



        val nodes = cloneClass.treeNode.descTraverser()
        return nodes.asSequence()
            .map{ CloneClass(it) }
            .filter{ it.length > 1 }
            .filter { cloneClass.length != it.length }
            .none {
                val first = times(cloneClass.length / it.length + 1) { it.tokenSequence() }.filter { it.source !in xFilter }
                val second = nodes.tokenSequence().filter { it.source !in xFilter }
                first equalContent second
            }
    }

    val xFilter = TokenSet.create(ElementType.ELSE_KEYWORD, ElementType.OROR, ElementType.ANDAND)

    fun Iterable<Node>.tokenSequence() = asSequence().map { it.parentEdge }.filterNotNull().flatMap(Edge::asSequence)


}

infix fun <T> Iterable<T>.isRepeatableBy(another: ()->Iterable<T>): Boolean {
    val source = this.iterator()

    while (source.hasNext()) {
        if ( ! source.equalStart(another().iterator())) return false
    }

    return true
}

fun <T> Iterator<T>.equalStart(other: Iterator<T>, maximumGap: Int = 2): Boolean {

    val otherBegin = other.next()

    for (gap in 0..maximumGap) {
        if (this.hasNext() && this.next() == otherBegin) break
    }


    while (this.hasNext() && other.hasNext()) {
        if ( this.next() != other.next()) return false
    }

    return true
}