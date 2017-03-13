package com.suhininalex.clones

import com.suhininalex.clones.core.postprocessing.filterSubClassClones

class PluginTest : FolderProjectTest("testdata/siblingClones/") {

    val clones
        get() = cloneManager.getAllCloneClasses().toList().filterSubClassClones()


    fun testSameTokenLengthSequence(){
        val problems = clones//.splitSiblingClones()

//        cloneClasses.forEach {
//            it.printInfo()
//        }
//        assertTrue(problems.isEmpty())
    }
}
