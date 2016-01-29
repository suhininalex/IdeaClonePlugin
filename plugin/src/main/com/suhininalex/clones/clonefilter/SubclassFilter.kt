package com.suhininalex.clones.clonefilter

import com.suhininalex.clones.CloneClass
import com.suhininalex.suffixtree.Node
import stream

import java.util.IdentityHashMap

class SubclassFilter(cloneClassesToFilter: Iterable<CloneClass>) : CloneClassFilter {

    /**
     * link from node to cloneClass with suffix link to this node
     */
    private val reverseSuffixLink = IdentityHashMap<Node, CloneClass>()
        .apply {
            cloneClassesToFilter.stream().filter { it.treeNode.suffixLink!=null }
                .forEach { put(it.treeNode.suffixLink, it) }
        }

    override fun isAllowed(cloneClass: CloneClass): Boolean {
        val greaterClass = reverseSuffixLink[cloneClass.treeNode] ?: return true
        return greaterClass.size != cloneClass.size
    }

}
