package com.suhininalex.clones

import com.intellij.psi.PsiElement
import com.suhininalex.clones.core.*
import com.suhininalex.clones.core.clonefilter.filterClones
import com.suhininalex.clones.core.interfaces.Clone
import com.suhininalex.clones.core.interfaces.CloneClass

class TreeClonesTest : FolderProjectTest("testdata/sphinx4/") {

    val clones
        get() = cloneManager.getAllCloneClasses().filterClones().toList()

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

