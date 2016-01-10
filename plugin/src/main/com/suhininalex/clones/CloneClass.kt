package com.suhininalex.clones

import com.suhininalex.suffixtree.Node

class CloneClass(val treeNode: Node) {
    val size by lazy {
        clones.sumBy { 1 }
    }

    val length = treeNode.lengthToRoot()

    val clones = object: Iterable<Clone> {
        override fun iterator() = CloneIterator(treeNode, length).iterator()
    }

    val isEmpty = !clones.iterator().hasNext()

    companion object {
        val lengthComparator = comparator { first: CloneClass, second: CloneClass -> first.length - second.length }
    }
}