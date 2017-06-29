package com.suhininalex.clones.core.postprocessing

import com.suhininalex.clones.core.structures.TreeCloneClass

fun List<TreeCloneClass>.notLongestSequenceFilter(): List<TreeCloneClass> =
    filter { it.treeNode.edges.all { it.terminal == null } }
