package com.suhininalex.clones

import com.suhininalex.clones.core.*
import com.suhininalex.clones.core.clonefilter.filterClones
import com.suhininalex.clones.core.utils.printText

class PluginTest : FolderProjectTest("testdata/siblingClones/") {

    val clones
        get() = cloneManager.getAllCloneClasses().filterClones().toList()


    fun testSameTokenLengthSequence(){
        val problems = clones//.splitSiblingClones()

        clones.forEach {
            val clone = it.clones.first()
            println(clone.tokenSequence().map { it to it.haveSibling(clone.lastPsi.textRange.endOffset) }.toList())
            clone.printText()
        }
//        clones.forEach {
//            it.printInfo()
//        }
//        assertTrue(problems.isEmpty())
    }
}
