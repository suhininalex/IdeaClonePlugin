package com.suhininalex.clones.core

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.mromanak.unionfind.UnionFindSet
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
    val unionSet = UnionFindSet(clones.flatMap { it.clones.toList() }.map(::CloneID))
    clones.forEach {
        if (it.clones.count()>0) {
            val first = CloneID(it.clones.first())
            it.clones.forEach { unionSet.join(first, CloneID(it)) }
        }
    }
    return unionSet.equivalenceClasses.map { RangeCloneClass(it.map { it.clone }.toList()) }
}

fun CloneClass.scoreSelfCoverage(): Int =
        clones.first().scoreSelfCoverage()

fun Clone.scoreSelfCoverage(): Int{
    val tree = SuffixTree<Token>()
    val sequence = tokenSequence().toList()
    val map = sequence.mapIndexed { i, psiElement ->  psiElement to i}.toMap()
    tree.addSequence(sequence.map(::Token).toList())
    val clones = tree.getAllCloneClasses().filterClones()
    val length = clones.flatMap { it.clones.toList() }
            .map{ map[it.firstPsi]!! to map[it.lastPsi]!! }
            .uniteRanges()
            .sumBy { it.second - it.first + 1 }
    val bigLength = sequence.size
    return length*100/bigLength
}

fun PsiElement.nextLeafElement(): PsiElement{
    var current = this
    while (current.nextSibling == null)
        current = current.parent
    current = current.nextSibling
    while (current.firstChild != null)
        current = current.firstChild
    return current
}

fun PsiElement.prevLeafElement(): PsiElement{
    var current = this
    while (current.prevSibling == null)
        current = current.parent
    current = current.prevSibling
    while (current.lastChild != null)
        current = current.lastChild
    return current
}

fun sequenceFromRange(firstPsi: PsiElement, lastPsi: PsiElement): Sequence<PsiElement> =
        generateSequence (firstPsi.firstEndChild()) { it.nextLeafElement() }.takeWhile { it.textRange.endOffset <= lastPsi.textRange.endOffset }.filter { it !in javaTokenFilter }

val lengthClassFilter = LengthFilter(10)

fun SuffixTree<Token>.getAllCloneClasses(): Sequence<TreeCloneClass>  =
        root.depthFirstTraverse { it.edges.asSequence().map { it.terminal }.filter { it != null } }
                .map(::TreeCloneClass)
                .filter { lengthClassFilter.isAllowed(it) }

fun List<Pair<Int,Int>>.uniteRanges(): List<Pair<Int, Int>> {
    if (size < 2) return this
    val sorted = sortedBy { it.first }.asSequence()
    val result = ArrayList<Pair<Int,Int>>()
    val first = sorted.first()
    var lastLeft = first.first
    var lastRight = first.second
    sorted.forEach {
        if (it.second <= lastRight ) {
            // skip
        } else if (it.first <= lastRight)  {
            lastRight = it.second
        } else {
            result.add(lastLeft to lastRight)
            lastLeft = it.first
            lastRight = it.second
        }
    }
    result.add(lastLeft to lastRight)
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




fun List<CloneClass>.splitSiblingClones() : List<CloneClass> =
        flatMap ( CloneClass::splitToSiblingClones )

fun CloneClass.splitToSiblingClones(): List<RangeCloneClass> {
    val randomClone = clones.first().normalize()
    val siblingRanges = randomClone.extractSiblingSequences().toList().toIndexes(randomClone.tokenSequence())
    return clones.map{it.normalize()}.map { it.extractSubClones(siblingRanges).asSequence() }.zipped().map(::RangeCloneClass)
}

fun PsiElement.str() = "$node (${textRange.startOffset} <- ${parent.textRange.startOffset})"


fun PsiElement.getNextGoodElement(): PsiElement {
    var current = this
    while (current in javaTokenFilter) current = current.nextLeafElement()
    return current
}

fun PsiElement.getPrevGoodElement(): PsiElement {
    var current = this
    while (current in javaTokenFilter) current = current.prevLeafElement()
    return current
}

fun Clone.extractSubClones(intervals: List<Pair<Int, Int>>): List<RangeClone> {
    val sequence = sequenceFromRange(firstPsi, lastPsi).toList()
    return intervals.map { (left, right) -> RangeClone(sequence[left], sequence[right]) }
}

fun List<Clone>.toIndexes(container: Sequence<PsiElement>): List<Pair<Int, Int>> {
    val map = container.mapIndexed { i, psiElement -> psiElement to i }.toMap()
    return this
            .map { map[it.firstPsi.firstEndChild().getNextGoodElement()]!! to map[it.lastPsi.lastEndChild().getPrevGoodElement()]!! }
}

fun PsiElement.firstEndChild(): PsiElement {
    var current = this
    while (current.firstChild != null) current = current.firstChild
    return current
}

fun PsiElement.lastEndChild(): PsiElement {
    var current = this
    while (current.lastChild != null) current = current.lastChild
    return current
}

fun Clone.tokenSequence(): Sequence<PsiElement> =
        sequenceFromRange(firstPsi, lastPsi)

fun Clone.normalize(): RangeClone {
    var current = firstPsi
    while (current.textRange.startOffset == current.parent.textRange.startOffset && current.parent.textRange.endOffset <= lastPsi.textRange.endOffset) current = current.parent
    return RangeClone(current, lastPsi)
}