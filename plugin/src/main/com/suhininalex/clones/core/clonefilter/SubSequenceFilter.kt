package com.suhininalex.clones.core.clonefilter

import com.intellij.psi.impl.source.tree.ElementType
import com.intellij.psi.tree.TokenSet
import com.suhininalex.clones.core.*
import com.suhininalex.suffixtree.Node
import stream

object SubSequenceFilter : CloneClassFilter {
    override fun isAllowed(cloneClass: CloneClass?): Boolean {
        cloneClass ?: return false
        val nodes = cloneClass.treeNode.descTraverser()
        return nodes.stream()
            .map{ CloneClass(it) }
            .filter{ it.length > 1 }
            .filter { cloneClass.length != it.length }
            .noneMatch {
                val first = times(cloneClass.length / it.length + 1) { it.tokenStream() }.filter { it.source !in xFilter }
                val second = nodes.tokenStream().filter { it.source !in xFilter }
                first equalContent second
            }
    }

    val xFilter = TokenSet.create(ElementType.ELSE_KEYWORD, ElementType.OROR, ElementType.ANDAND)

    fun Iterable<Node>.tokenStream() = stream().map { it.parentEdge }.filter { it != null }.flatMap { it.asSequence().stream() }
}