package com.suhininalex.clones

import com.suhininalex.clones.core.postprocessing.*
import com.suhininalex.clones.core.structures.CloneClass
import com.suhininalex.clones.core.utils.childrenMethods
import com.suhininalex.clones.core.utils.printText
import com.suhininalex.clones.core.utils.stringId
import com.suhininalex.clones.core.utils.tokenSequence

class ExtractSiblingClonesTest : FolderProjectTest("testdata/siblingClones/") {

    val clones
        get() = cloneManager.getAllCloneClasses().toList().filterSubClassClones()

    fun testNotAloneDuplicate() {
        val problems = clones.splitSiblingClones().mergeCloneClasses().filter { ! checkCountInvariant(it) }
        problems.forEach {
            println("========================")
            println("Problem class:")
            it.printInfo()
        }
        assertTrue(problems.isEmpty())
    }

    fun testSameTokenLengthSequence(){
        val problems = clones
//            .filter {
//                it.splitToSiblingClones().any { ! checkTokenLengthInvariant(it) }
//            }
        baseDirectoryPsi.childrenMethods.forEach {
            println("METHOD: ${it.stringId}")
            cloneManager.getMethodFilteredClones(it).forEach {
                println("========================")
                it.clones.forEach {
                    it.printText()
                    println("----------------------")
                }
            }
        }
//        problems.splitSiblingClones().mergeCloneClasses().forEach {
//                println("========================")
//                it.clones.forEach {
//                    it.printText()
//                    println("----------------------")
//                }
//
////                it.clones.map{it.normalizePsiHierarchy()}.forEach {
////                    println(it.tokenSequence().map{it.node.elementType}.toList())
////                }
//
//        }
        assertTrue(problems.isEmpty())
    }
}

fun CloneClass.printInfo(){
    clones.forEach {
        println(it.tokenSequence().toList())
        it.printText()
        println("------------------------")
    }
}