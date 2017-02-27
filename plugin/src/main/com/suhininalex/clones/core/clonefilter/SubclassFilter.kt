package com.suhininalex.clones.core.clonefilter

import com.suhininalex.clones.core.structures.TreeCloneClass
import com.suhininalex.suffixtree.Node
import java.util.*

class SubclassFilter(treeCloneClassesToFilter: Iterable<TreeCloneClass>) {

    /**
     * link from node to suffixTreeCloneClass with suffix link to this node
     */
    private val reverseSuffixLink = IdentityHashMap<Node, TreeCloneClass>()
        .apply {
            treeCloneClassesToFilter.asSequence().filter { it.treeNode.suffixLink!=null }
                .forEach { put(it.treeNode.suffixLink, it) }
        }

    fun isAllowed(treeCloneClass: TreeCloneClass): Boolean {
        val greaterClass = reverseSuffixLink[treeCloneClass.treeNode] ?: return true
        return greaterClass.size != treeCloneClass.size
    }

}
