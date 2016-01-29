package com.suhininalex.clones

import com.suhininalex.suffixtree.Edge
import com.suhininalex.suffixtree.Node
import java.util.*

class CloneIterator(val cloneClass: CloneClass) : Iterator<Clone> {

    private val stack = Stack<EdgeInfo>()

    private fun addEdgesToStack(node : Node, offset: Int) =
        node.edges.forEach {
            stack.push(EdgeInfo(it, offset))
        }

    init {
        addEdgesToStack(cloneClass.treeNode, 0)
    }

    override fun hasNext(): Boolean {
        return !stack.isEmpty()
    }

    override fun next(): Clone {
        val( edge, offset) = stack.pop()

        with (edge) {
            if (terminal != null) {
                addEdgesToStack(edge.terminal, offset+edge.length)
                return next()
            } else {
                val lastElementIndex = end-offset-length
                val firstElementIndex = lastElementIndex - cloneClass.length + 1
                return Clone(getFromSequence(firstElementIndex),getFromSequence(lastElementIndex))
            }
        }
    }

    private data class EdgeInfo(val edge: Edge, val offset: Int)

    private fun Edge.getFromSequence(pos: Int) = sequence[pos] as Token
}