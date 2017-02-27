package com.suhininalex.clones.core

import com.intellij.psi.PsiElement
import com.mromanak.unionfind.UnionFindSet
import com.suhininalex.clones.core.clonefilter.filterClones
import com.suhininalex.clones.core.structures.Clone
import com.suhininalex.clones.core.structures.CloneClass
import com.suhininalex.clones.core.utils.*
import com.suhininalex.clones.ide.method

fun List<CloneClass>.filterSelfCoveredClasses(): List<CloneClass> =
    filter {
        with(it.getScore()) {
            selfCoverage <= 0.7 || selfCoverage <= 0.85 && sameMethodCount <= 0.7
        }
    }

class CloneID(val clone: Clone){

    val left: PsiElement
        get() = clone.firstPsi

    val right: PsiElement
        get() = clone.lastPsi

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as CloneID

        if (left != other.left) return false
        if (right != other.right) return false

        return true
    }

    override fun hashCode(): Int {
        var result = left.hashCode()
        result = 31 * result + right.hashCode()
        return result
    }
}

fun List<CloneClass>.filterSameCloneRangeClasses(): List<RangeCloneClass> {
    val unionSet = UnionFindSet(this.flatMap { it.clones.toList() }.map(::CloneID))
    this.forEach {
        if (it.size>0) {
            val first = CloneID(it.clones.first())
            it.clones.forEach { unionSet.join(first, CloneID(it)) }
        }
    }
    return unionSet.equivalenceClasses.map { RangeCloneClass(it.map { it.clone }) }
}

fun CloneClass.scoreSelfCoverage(): Int =
        clones.first().scoreSelfCoverage()

fun Clone.scoreSelfCoverage(): Int {
    val sequence = tokenSequence().toList()
    val indexMap = sequence.mapIndexed { i, psiElement ->  psiElement to i}.toMap()
    val length = suffixTree(sequence.map(::Token).toList())
            .getAllCloneClasses(10)
            .filterClones()
            .flatMap { it.clones.toList() }
            .map{ IntRange(indexMap[it.firstPsi]!!, indexMap[it.lastPsi]!!) }
            .uniteRanges()
            .sumBy { it.length }
    val bigLength = sequence.size
    return length*100/bigLength
}

fun PsiElement.nextLeafElement(): PsiElement {
    var current = this
    while (current.nextSibling == null)
        current = current.parent
    current = current.nextSibling
    while (current.firstChild != null)
        current = current.firstChild
    return current
}

fun PsiElement.prevLeafElement(): PsiElement {
    var current = this
    while (current.prevSibling == null)
        current = current.parent
    current = current.prevSibling
    while (current.lastChild != null)
        current = current.lastChild
    return current
}


data class CloneScore(val selfCoverage: Double, val sameMethodCount: Double, val length: Int)

fun CloneScore.score(): Double =
        (1-selfCoverage*sameMethodCount)*length

fun CloneClass.getScore() =
    CloneScore(scoreSelfCoverage()/100.0, scoreSameMethod()/100.0, clones.first().getLength())

fun CloneClass.scoreSameMethod(): Int {
    assert(size > 1)
    val mostPopularMethodNumber = clones.map{ it.firstPsi.method }.groupBy { it }.map { it.value.size }.max()
    return (mostPopularMethodNumber!!-1)*100/(size-1)
}

fun List<CloneClass>.splitSiblingClones() : List<CloneClass> =
        flatMap ( CloneClass::splitToSiblingClones )

fun CloneClass.splitToSiblingClones(): List<CloneClass> {
    val randomClone = clones.first().normalizePsiHierarchy()
    val siblingRanges = randomClone.extractSiblingSequences().toList().mapToTokenIndexes(randomClone.tokenSequence())
    return clones.map{it.normalizePsiHierarchy()}.map { it.extractSubClones(siblingRanges).asSequence() }.zipped().map(::RangeCloneClass)
}

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

fun Clone.extractSubClones(intervals: List<Pair<Int, Int>>): List<Clone> {
    val sequence = tokenSequence().toList()
    return intervals.map { (left, right) -> RangeClone(sequence[left], sequence[right]) }
}

fun List<Clone>.mapToTokenIndexes(container: Sequence<PsiElement>): List<Pair<Int, Int>> {
    val map = container.mapIndexed { i, psiElement -> psiElement to i }.toMap()
    return this.map { map[it.firstPsi.firstEndChild().getNextGoodElement()]!! to map[it.lastPsi.lastEndChild().getPrevGoodElement()]!! }
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
    generateSequence (firstPsi.firstEndChild()) { it.nextLeafElement() }
            .takeWhile { it.textRange.endOffset <= lastPsi.textRange.endOffset }
            .filter { it !in javaTokenFilter }

/**
 * Finds the biggest parent for firstPsi which points at the same place
 */
fun Clone.normalizePsiHierarchy(): Clone {
    var current = firstPsi
    while (current.textRange.startOffset == current.parent.textRange.startOffset
            && current.parent.textRange.endOffset <= lastPsi.textRange.endOffset)
        current = current.parent
    return RangeClone(current, lastPsi)
}