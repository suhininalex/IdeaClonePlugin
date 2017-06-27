package com.suhininalex.clones.core.utils

import com.suhininalex.suffixtree.*

fun <T: Comparable<T>> suffixTree(sequence: List<T>): SuffixTree<T> =
        SuffixTree<T>().apply {
            addSequence(sequence)
        }

fun Node.riseTraverser() = object: Iterable<Node> {
    var node: Node? = this@riseTraverser
    override fun iterator() = iterate {
        val result = node
        node = node?.parentEdge?.parent
        result
    }
}

fun Node.descTraverser() = riseTraverser().reversed()

fun Node.lengthToRoot() =
        riseTraverser().sumBy { it.parentEdge?.length ?: 0 }

val Edge.length: Int
    get() = end - begin + 1

val Edge.isTerminal: Boolean
    get() = this.terminal == null