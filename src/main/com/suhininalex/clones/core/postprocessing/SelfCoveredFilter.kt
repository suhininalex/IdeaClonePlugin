package com.suhininalex.clones.core.postprocessing

import com.suhininalex.clones.core.*
import com.suhininalex.clones.core.structures.Clone
import com.suhininalex.clones.core.structures.CloneClass
import com.suhininalex.clones.core.structures.SourceToken
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
        CloneScore(scoreSelfCoverage(), scoreSameMethod(), clones.first().textLength)

fun filterPredicate(cloneClass: CloneClass): Boolean =
        Application.readAction {
            with(cloneClass.getScore()) {
                selfCoverage <= PluginSettings.coverageSkipFilter / 100f || selfCoverage <= 0.85 && sameMethodCount <= 0.7
            }
        }

private fun CloneClass.scoreSelfCoverage(): Double =
        clones.first().scoreSelfCoverage()

private fun Clone.scoreSelfCoverage(): Double {

     val sequence = tokenSequence().toList()
     val indexMap = sequence.mapIndexed { i, psiElement ->  psiElement to i}.toMap()
     val tree = suffixTree(sequence.map(::SourceToken).toList())

     if (tree.haveTooMuchClones(sequence.size)) return 1.0

     val length = tree
             .getAllCloneClasses(10).toList()
             .filterSubClassClones()
             .flatMap { it.clones.toList() }
             .map{ IntRange(indexMap[it.firstPsi]!!, indexMap[it.lastPsi]!!) }
             .uniteRanges()
             .sumBy { it.length }
     return length.toDouble()/sequence.size
}

private fun SuffixTree<SourceToken>.haveTooMuchClones(sourceLength: Int) =
    getAllCloneClasses(10).drop(sourceLength*PluginSettings.coverageSkipFilter/100).firstOrNull() != null


private fun CloneClass.scoreSameMethod(): Double {
    val mostPopularMethodNumber = clones.map{ it.firstPsi.method }.groupBy { it }.map { it.value.size }.max()
    return (mostPopularMethodNumber!!-1)/(size-1).toDouble()
}