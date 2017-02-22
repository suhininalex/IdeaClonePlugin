package com.suhininalex.clones

import com.suhininalex.clones.core.*
import com.suhininalex.clones.core.clonefilter.filterClones
import com.suhininalex.clones.core.interfaces.CloneClass
import com.suhininalex.clones.ide.method

class ExtractSiblingClonesTest : FolderProjectTest("testdata/sphinx4/src") {

    val clones
        get() = cloneManager.getAllCloneClasses().filterClones().toList()

//    fun testNotAloneDuplicate() {
//        val problems = clones.splitSiblingClones().filter { ! checkCountInvariant(it) }
//        problems.forEach {
//            println("========================")
//            println("Problem class:")
//            it.printInfo()
//        }
//        assertTrue(problems.isEmpty())
//    }

    fun testSameTokenLengthSequence(){
        val problems = clones
//            .filter {
//                it.splitToSiblingClones().any { ! checkTokenLengthInvariant(it) }
//            }

        problems.forEach {
            assert(it.clones.map { it.tokenSequence().count() }.areEqual())
            try {it.splitToSiblingClones()}
            catch (e: Throwable) {
//                assert(it.clones.map{it.normalize()}.map { it.tokenSequence().count() }.areEqual())
                println("========================")
                println("Problem source:")
                println(it.clones.first().firstPsi.method?.text)
                println("--------------------------")
                it.clones.first().printText()
                println("----------------------")
                it.clones.map{it.normalize()}.forEach {
                    println(it.tokenSequence().map{it.node.elementType}.toList())
                }
            }

        }
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