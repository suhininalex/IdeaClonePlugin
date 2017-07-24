package com.suhininalex.clones.core.postprocessing

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.suhininalex.clones.core.CloneIndexer
import com.suhininalex.clones.core.structures.*
import com.suhininalex.clones.core.utils.*
import com.suhininalex.clones.ide.configuration.PluginSettings

/**
 * Unites nearby clone classes in file
 */
fun List<CloneClass>.uniteNearbyClones(baseFile: VirtualFile): Pair<List<CloneClass>, List<CloneClass>> {
    val clonesInFile = sortedFileDuplicates(baseFile, this.filterEqualClasses())
    val unitedRanges = clonesInFile.unite()
    val unitedClasses = unitedRanges
            .filter { (start, end) -> start != end }
            .map{ unite(clonesInFile[it.first].cloneClass, clonesInFile[it.second].cloneClass) }
    val unUnitedClasses = unitedRanges
            .filter { (start, end) -> start == end }
            .map { (start, end) -> clonesInFile[start].cloneClass }
    return unitedClasses to unUnitedClasses
}

fun sortedFileDuplicates(baseFile: VirtualFile, clones: List<CloneClass>): List<FileDuplicate>{
    return clones
            .map {
                FileDuplicate(it.clones.filter { it.file == baseFile }.minBy { it.textRange.startOffset }!!, it)
            }
            .sortedBy { it.duplicate.textRange.startOffset }
}

data class FileDuplicate(val duplicate: Clone, val cloneClass: CloneClass)

fun canUnite(first: FileDuplicate, second: FileDuplicate): Boolean {
    if (first.cloneClass == second.cloneClass) return false
    return first.cloneClass.clones.all { clone ->
        second.cloneClass.clones.filter { it.file == clone.file }.any { canUnite(clone, it) }
    }
}

fun canUnite(first: Clone, second: Clone, distance: Int = PluginSettings.minFragment): Boolean {
    if (first.file != second.file) return false
    else return second.firstPsi.hasLeftPsi(first.lastPsi, distance) || second.firstPsi.hasRightPsi(first.lastPsi, distance)
}

fun PsiElement.hasRightPsi(psiElement: PsiElement, maxOffset: Int): Boolean =
    generateSequence(this) {it.nextLeafElement()}.filterNot(::isNoiseElement).take(maxOffset).any { it == psiElement }

fun PsiElement.hasLeftPsi(psiElement: PsiElement, maxOffset: Int): Boolean =
    generateSequence(this) {it.prevLeafElement()}.filterNot(::isNoiseElement).take(maxOffset).any { it == psiElement }

fun List<FileDuplicate>.findLastUnitable(i: Int): Int? {
    val first = this[i]
    var lastSucceed: Int? = null
    var lastEnd: Int = first.duplicate.textRange.endOffset
    for (j in i+1..lastIndex){
        val second = this[j]
        if (canUnite(first, second)){
            if (lastEnd < second.duplicate.textRange.endOffset){
                lastSucceed = j
                lastEnd = second.duplicate.textRange.endOffset
            }
        }
    }
    return lastSucceed
}

fun List<FileDuplicate>.unite(): List<Pair<Int, Int>>{
    if (isEmpty()) return emptyList()
    val list = ArrayList<Pair<Int, Int>>()
    var current = 0
    do {
        var lastUnitable = findLastUnitable(current)
        if (lastUnitable != null) {
            list.add(current to lastUnitable)
        } else {
            list.add(current to current)
        }
        current = (lastUnitable ?: current) + 1
    } while (current <= lastIndex)
    return list
}

fun unite(first: CloneClass, second: CloneClass): CloneClass{
    if (first == second) return first
    val clones = first.clones.map { firstClone ->
        val secondClone = second.clones.find { canUnite(firstClone, it)}!!
        unite(firstClone, secondClone)
    }
    return RangeCloneClass(clones.toList())
}

fun unite(first: Clone, second: Clone): Clone =
    if (first.textRange.startOffset < second.textRange.startOffset)
        RangeClone(first.firstPsi, second.lastPsi)
    else
        RangeClone(second.firstPsi, first.lastPsi)

fun List<CloneClass>.filterEqualClasses(): List<CloneClass> {
    val classes = map { ClassDescriptor(it.clones.map { CloneDescriptor(it.firstPsi, it.lastPsi) }.toSet()) }.toSet()
    return classes.map { RangeCloneClass(it.clones.map { RangeClone(it.first, it.last) }) }
}

data class ClassDescriptor(val clones: Set<CloneDescriptor>)

data class CloneDescriptor(val first: PsiElement, val last: PsiElement)
