package com.suhininalex.clones.core.postprocessing

import com.intellij.openapi.vfs.VirtualFile
import com.suhininalex.clones.core.CloneIndexer
import com.suhininalex.clones.core.structures.CloneClass
import com.suhininalex.clones.core.structures.TreeCloneClass
import com.suhininalex.clones.core.utils.file
import com.suhininalex.clones.core.utils.textRange

/**
 * Unites nearby clone classes in file
 */
fun List<TreeCloneClass>.uniteNearbyClones(baseFile: VirtualFile){
    println("=========================")
    val sortedClasses = sortedBy { it.clones.filter { it.file == baseFile }.map { it.firstElement.offset }.min()!! }
    sortedClasses.forEach{
        println(it.asString())
    }
    println("----------------------------------")
    sortedClasses.dropLast(1).forEachIndexed { i, it ->
        val other = sortedClasses[i+1]
        println("${it.asString()}  ${canUnite(it, other)}")
    }
}

fun CloneClass.asString(): String =
    clones.map { "(${it.file.name}, ${it.textRange.startOffset}, ${it.textRange.endOffset})" }.toList().toString()


fun canUnite(first: CloneClass, second: CloneClass, distance: Int = 10): Boolean {
    val cloneEndPositions = first.clones.groupBy { it.file }.map { (file, clones) -> file to clones.map { it.textRange.endOffset } }.toMap()
    return second.clones.all { clone ->
        cloneEndPositions[clone.file].orEmpty().any { cloneEnd ->
            clone.textRange.startOffset - cloneEnd in 0..distance
        }
    }
}

fun List<CloneClass>.tryToUnite(i: Int){
    if (i==lastIndex) return
    val first = this[i]
    var lastSucceed: Int? = null
    for (j in i+1..lastIndex){
        val second = this[j]
        if (canUnite(first, second)){
            lastSucceed = j
        }
    }
}

fun inspectFile(virtualFile: VirtualFile){
    CloneIndexer.getFileCloneClassesGroupedBySequence(virtualFile).forEach{ cloneClasses ->
        cloneClasses.filterSubClassClones().uniteNearbyClones(virtualFile)
    }
}
