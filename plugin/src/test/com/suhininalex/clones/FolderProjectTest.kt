package com.suhininalex.clones

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import com.suhininalex.clones.core.*
import com.suhininalex.clones.ide.childrenMethods

open class FolderProjectTest(val testFolder: String) : LightCodeInsightFixtureTestCase() {

    val cloneManager = CloneManager()

    override fun getTestDataPath() = testFolder

    override fun setUp() {
        super.setUp()
        val directory = myFixture.copyDirectoryToProject("/", "")
        myFixture.psiManager.findDirectory(directory)!!.childrenMethods.forEach {
            cloneManager.addMethod(it)
        }
    }

}