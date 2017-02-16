package com.suhininalex.clones.core

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.suhininalex.clones.core.clonefilter.LengthFilter
import com.suhininalex.clones.core.clonefilter.filterClones
import com.suhininalex.clones.core.interfaces.Clone
import com.suhininalex.clones.core.interfaces.CloneClass
import com.suhininalex.clones.ide.endLine
import com.suhininalex.clones.ide.method
import com.suhininalex.clones.ide.startLine
import com.suhininalex.suffixtree.SuffixTree
import java.util.*

class CloneID(val clone: Clone){

    val file = clone.firstPsi.containingFile
    val startLine = clone.firstPsi.startLine
    val endLine = clone.lastPsi.endLine

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as CloneID

        if (file != other.file) return false
        if (startLine != other.startLine) return false
        if (endLine != other.endLine) return false

        return true
    }

    override fun hashCode(): Int {
        var result = file?.hashCode() ?: 0
        result = 31 * result + startLine
        result = 31 * result + endLine
        return result
    }
}

fun filterSameCloneRangeClasses(clones: List<CloneClass>): List<RangeCloneClass> {
    val map = HashMap<CloneID, Int>()
    var groupId = 0
    clones.forEach { cloneRangeClass ->
        val cloneWithAnotherParent = cloneRangeClass.clones.find { map[CloneID(it)] != null }
        val groupId: Int =
                if (cloneWithAnotherParent == null) {
                    groupId++
                } else {
                    map[CloneID(cloneWithAnotherParent)]!!
                }

        cloneRangeClass.clones.map(::CloneID).forEach {
            map.put(it, groupId)
        }
    }
    return map.entries.groupBy { it.value }.values.map { RangeCloneClass(it.map { it.key.clone }) }
}

fun CloneClass.scoreSelfCoverage(): Int =
        clones.first().scoreSelfCoverage()

fun Clone.scoreSelfCoverage(): Int{
    val sequence = sequenceFromRange(firstPsi, lastPsi).toList()

    val tree = SuffixTree<Token>()
    tree.addSequence(sequence.map(::Token))
    val clones = tree.getAllCloneClasses().filterClones();
    val raw = clones.map { RangeCloneClass(it.clones.map { RangeClone(it.firstPsi, it.lastPsi) }.toList()) }
    val length = raw.flatMap { it.cloneRanges }
            .map{ TextRange(it.firstPsi.startLine, it.lastPsi.endLine+1) }
            .uniteRanges()
            .sumBy { it.length }
    val bigLength = TextRange(firstPsi.startLine, lastPsi.endLine).length + 1
    return length*100/bigLength
}

fun PsiElement.nextElement(): PsiElement{
    var current = this
    while (current.nextSibling == null)
        current = current.parent
    current = current.nextSibling
    while (current.firstChild != null)
        current = current.firstChild
    return current
}

fun sequenceFromRange(firstPsi: PsiElement, lastPsi: PsiElement): Sequence<PsiElement> {
    var first = firstPsi
    while (first.firstChild != null)
        first = first.firstChild
    return generateSequence (firstPsi) { it.nextElement() }.takeWhile { it.textRange.endOffset <= lastPsi.textRange.endOffset }.filter { it !in javaTokenFilter }
}

val lengthClassFilter = LengthFilter(10)

fun SuffixTree<Token>.getAllCloneClasses(): Sequence<TreeCloneClass>  =
        root.depthFirstTraverse { it.edges.asSequence().map { it.terminal }.filter { it != null } }
                .map(::TreeCloneClass)
                .filter { lengthClassFilter.isAllowed(it) }

fun List<TextRange>.uniteRanges(): List<TextRange> {
    if (size < 2) return this
    val sorted = sortedBy { it.startOffset }.asSequence()
    val result = ArrayList<TextRange>()
    val first = sorted.first()
    var lastLeft = first.startOffset
    var lastRight = first.endOffset
    sorted.forEach {
        if (it.endOffset <= lastRight ) {
            // skip
        } else if (it.startOffset <= lastRight)  {
            lastRight = it.endOffset
        } else {
            result.add(TextRange(lastLeft, lastRight))
            lastLeft = it.startOffset
            lastRight = it.endOffset
        }
    }
    result.add(TextRange(lastLeft, lastRight))
    return result
}

data class CloneScore(val selfCoverage: Double, val sameMethodCount: Double, val length: Int)

fun CloneScore.score(): Double =
        (1-selfCoverage*sameMethodCount)*length

fun CloneClass.getScore() =
    CloneScore(scoreSelfCoverage()/100.0, scoreSameMethod()/100.0, clones.first().getLength())

fun CloneClass.scoreSameMethod(): Int =
    if (clones.count() < 2) 100
    else (clones.map{ it.firstPsi.method }.groupBy { it }.map { it.value.size }.max()!!-1)*100/(clones.count()-1)
