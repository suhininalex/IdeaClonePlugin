package com.suhininalex.clones.clonefilter

import com.suhininalex.clones.CloneClass
import com.suhininalex.clones.descTraverser
import com.suhininalex.clones.isEmpty
import com.suhininalex.clones.riseTraverser
import com.suhininalex.suffixtree.Node
import stream
import java.util.stream.Stream

object SubSequenceFilter : CloneClassFilter {
    override fun isAllowed(cloneClass: CloneClass?): Boolean {
        val nodesToCheck = cloneClass?.treeNode?.riseTraverser() ?: return false
        val fullStream = nodesToCheck.tokenStream()
        return nodesToCheck.stream()
                .map{CloneClass(it)}
                .filter { it.length > 1 && cloneClass!!.length % it.length == 0 }
                .noneMatch { fullStream.isRepeatableBy{it.treeNode.descTraverser().tokenStream()} }
    }

    fun Iterable<Node>.tokenStream() = stream().map { it.parentEdge }.filter { it != null }.flatMap { it.sequence.stream() }

    fun Stream<Any?>.isRepeatableBy(subStream: ()->Stream<Any?>): Boolean {
        if (subStream().isEmpty()) return true
        val allIterator = iterator()
        var subIterator = subStream().iterator()
        while (allIterator.hasNext()){
            if ( ! subIterator.hasNext()) subIterator = subStream().iterator()
            if (allIterator.next() != subIterator.next()) return false
        }
        return true
    }
}