package com.suhininalex.clones

import com.suhininalex.clones.core.CloneIndexer
import com.suhininalex.clones.core.postprocessing.*
import com.suhininalex.clones.core.structures.CloneClass
import com.suhininalex.clones.core.utils.printText
import com.suhininalex.clones.core.utils.tokenSequence

class ExtractSiblingClonesTest : FolderProjectTest("testdata/sphinx4-java/") {

    val clones
        get() = CloneIndexer.getAllCloneClasses().filterSubClassClones()

    fun testNotAloneDuplicate() {
        val problems = clones.splitSiblingClones().mergeCloneClasses().filter { ! checkCountInvariant(it) }
        problems.forEach(CloneClass::printInfo)
        assertTrue(problems.isEmpty())
    }

    fun testSameTokenLengthSequence(){
        val problems = clones.splitSiblingClones().mergeCloneClasses().filter { ! checkTokenLengthInvariant(it) }
        problems.forEach{
            it.printInfo()
            it.clones.forEach {
                println(it.tokenSequence().toList())
            }
        }
        assertTrue(problems.isEmpty())
    }
}

fun CloneClass.printInfo(){
    clones.forEach {
        println("========================")
        println("Problem class:")
        println(it.tokenSequence().toList())
        it.printText()
        println("------------------------")
    }
}