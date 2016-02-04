package com.suhininalex.clones.clonefilter

import com.suhininalex.clones.*
import com.suhininalex.suffixtree.Node
import stream

object SubSequenceFilter : CloneClassFilter {
    override fun isAllowed(cloneClass: CloneClass?): Boolean {
        cloneClass ?: return false
        val nodes = cloneClass.treeNode.descTraverser()
        return nodes.stream()
            .map{CloneClass(it)}
            .filter{ it.length > 1 }
            .filter { cloneClass.length != it.length }
            .noneMatch {
                times(cloneClass.length / it.length + 1) {it.tokenStream()} equalContent nodes.tokenStream()
            }
    }

    fun Iterable<Node>.tokenStream() = stream().map { it.parentEdge }.filter { it != null }.flatMap { it.asSequence().stream() }
}