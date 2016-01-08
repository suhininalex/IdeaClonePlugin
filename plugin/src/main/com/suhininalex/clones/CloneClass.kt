package com.suhininalex.clones

import com.suhininalex.suffixtree.Node

class CloneClass(val treeNode: Node) {
    val size by lazy {
        clones.sumBy { 1 }
    }

    val length = computeLengthToRoot(treeNode)

    val clones = object: Iterable<Clone> {
        override fun iterator() = CloneIterator(treeNode, length).iterator()
    }

    val isEmpty = !clones.iterator().hasNext()

    private fun computeLengthToRoot(node: Node) =
        node.riseTraverser().sumBy { it.parentEdge?.getLength() ?: 0  }

    companion object {
        val lengthComparator = comparator { first: CloneClass, second: CloneClass -> first.length - second.length }
    }
}