package com.suhininalex.clones.core.postprocessing

import com.suhininalex.clones.core.structures.CloneClass
import com.suhininalex.clones.core.structures.RangeCloneClass
import com.suhininalex.clones.core.structures.TreeCloneClass

fun List<TreeCloneClass>.validClonesFilter(): List<CloneClass> =
        mapNotNull { it.remainValidClones() }

fun TreeCloneClass.remainValidClones(): CloneClass? {
    val clones = clones.filter { it.firstElement.isValid }.toList()
    if (clones.size > 1) {
        return RangeCloneClass(clones)
    } else {
        return null
    }
}