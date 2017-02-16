package com.suhininalex.clones

import com.intellij.psi.PsiElement
import com.suhininalex.clones.core.*
import com.suhininalex.clones.core.clonefilter.filterClones
import com.suhininalex.clones.core.interfaces.Clone
import com.suhininalex.clones.core.interfaces.CloneClass

class ExtractSiblingClonesTest : FolderProjectTest("testdata/sphinx4/src") {

    fun testSimpleTest() {
        val clones = cloneManager.getAllCloneClasses().filterClones().toList()


        clones.extractSiblingClones().filter { ! checkTokenLengthInvariant(it) }.forEach {
            println("===================")
            it.clones.first().printText()
        }
        clones.filter { ! checkCountInvariant(it) }.forEach {
            println("===================")
            it.clones.first().printText()
        }
    }
}

fun checkCountInvariant(cloneRangeClass: CloneClass): Boolean =
    cloneRangeClass.clones.count() > 1

fun checkTokenLengthInvariant(cloneRangeClass: CloneClass): Boolean =
    cloneRangeClass.clones.map { it.tokenSequence().count() }.areEqual()

fun Clone.tokenSequence(): Sequence<PsiElement> =
    sequenceFromRange(firstPsi, lastPsi)
