package com.suhininalex.clones

import com.suhininalex.suffixtree.Edge
import com.suhininalex.suffixtree.Node
import stream
import java.util.stream.Stream

class CloneClass(val treeNode: Node) {

//    val clones: Stream<Clone> get() = treeNode.edges.stream().peek { println("flatmap: ${it.end}") }.flatMap { it.getTerminalsWithOffset() }.map {
//        val (edge, offset) = it
//        val lastElementIndex = edge.end - offset - edge.length
//        val firstElementIndex = lastElementIndex - treeNode.lengthToRoot() + 1
//        println("edge: ${edge.end} ${edge.length} length: $length index:  $firstElementIndex $lastElementIndex $offset")
//        return@map Clone(edge.getFromSequence(firstElementIndex), edge.getFromSequence(lastElementIndex))
//    }

    val clones: Stream<Clone> get() = CloneIterator(this).stream()

    val size by lazy {
        clones.count()
    }

    val length = treeNode.lengthToRoot()

    val isEmpty = clones.isEmpty()

    companion object {
        val lengthComparator = comparator { first: CloneClass, second: CloneClass -> first.length - second.length }
    }

    fun Edge.getTerminalsWithOffset() =
            Pair(this, 0).leafTraverse ({it.first.terminal==null}) {
                val offset = it.first.length + it.second
                println("$offset")
                it.first.terminal.edges.stream().map { Pair(it, offset) }
            }

    private fun Edge.getFromSequence(pos: Int) = sequence[pos] as Token
}