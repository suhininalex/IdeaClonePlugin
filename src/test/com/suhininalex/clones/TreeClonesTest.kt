package com.suhininalex.clones

import com.suhininalex.clones.core.CloneIndexer
import com.suhininalex.clones.core.postprocessing.filterSubClassClones
import com.suhininalex.clones.core.structures.CloneClass
import com.suhininalex.clones.core.utils.areEqual
import com.suhininalex.clones.core.utils.tokenSequence

class TreeClonesTest : FolderProjectTest("testdata/sphinx4-java/") {

    val clones
        get() = CloneIndexer.getAllCloneClasses().filterSubClassClones().toList()

    fun testNotAloneDuplicate() {
        assertTrue(clones.all(::checkCountInvariant))
    }

    fun testSameTokenLengthSequence(){
        assertTrue(clones.all(::checkTokenLengthInvariant))
    }
}

fun checkCountInvariant(cloneClass: CloneClass): Boolean =
    cloneClass.clones.count() > 1

fun checkTokenLengthInvariant(cloneClass: CloneClass): Boolean =
    cloneClass.clones.map { it.tokenSequence().count() }.areEqual()

