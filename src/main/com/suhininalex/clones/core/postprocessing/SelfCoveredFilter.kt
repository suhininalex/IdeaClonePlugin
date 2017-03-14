package com.suhininalex.clones.core.postprocessing

import com.suhininalex.clones.core.*
import com.suhininalex.clones.core.structures.Clone
import com.suhininalex.clones.core.structures.CloneClass
import com.suhininalex.clones.core.structures.Token
import com.suhininalex.clones.core.utils.*
import com.suhininalex.clones.ide.configuration.PluginSettings
import com.suhininalex.suffixtree.SuffixTree
import nl.komponents.kovenant.Promise
import java.lang.Exception

fun List<CloneClass>.filterSelfCoveredClasses(): List<CloneClass> =
        filter(::filterPredicate)

fun ListWithProgressBar<CloneClass>.filterSelfCoveredClasses(): Promise<List<CloneClass>, Exception> {
    return filter(::filterPredicate)
}

data class CloneScore(val selfCoverage: Double, val sameMethodCount: Double, val length: Int)

fun CloneClass.getScore(): CloneScore =
        CloneScore(scoreSelfCoverage()/100.0, scoreSameMethod()/100.0, clones.first().textLength)

fun filterPredicate(cloneClass: CloneClass): Boolean =
        with(cloneClass.getScore()) {
            selfCoverage <= 0.7 || selfCoverage <= 0.85 && sameMethodCount <= 0.7
        }

private fun CloneClass.scoreSelfCoverage(): Int =
        clones.first().scoreSelfCoverage()

private fun Clone.scoreSelfCoverage(): Int {

     val sequence = tokenSequence().toList()
     val indexMap = sequence.mapIndexed { i, psiElement ->  psiElement to i}.toMap()
     val tree = suffixTree(sequence.map(::Token).toList())

     if (tree.haveTooMuchClones(sequence.size)) return 100

     val length = tree
             .getAllCloneClasses(10).toList()
             .filterSubClassClones()
             .flatMap { it.clones.toList() }
             .map{ IntRange(indexMap[it.firstPsi]!!, indexMap[it.lastPsi]!!) }
             .uniteRanges()
             .sumBy { it.length }
     return length*100/sequence.size
}

private fun SuffixTree<Token>.haveTooMuchClones(sourceLength: Int) =
    getAllCloneClasses(10).drop(sourceLength*PluginSettings.coverageSkipFilter/100).firstOrNull() != null


private fun CloneClass.scoreSameMethod(): Int {
    assert(size > 1)
    val mostPopularMethodNumber = clones.map{ it.firstPsi.method }.groupBy { it }.map { it.value.size }.max()
    return (mostPopularMethodNumber!!-1)*100/(size-1)
}