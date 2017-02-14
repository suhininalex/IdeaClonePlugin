package com.suhininalex.clones

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import com.suhininalex.clones.ide.childrenMethods

class PluginTest : LightCodeInsightFixtureTestCase() {

    override fun getTestDataPath() = "${super.getTestDataPath()}/plugin/src/testData/"

    fun testSimpleTest() {
        myFixture.configureByFile("SimpleClass.java")
        myFixture.file.childrenMethods.forEach {
            println("=====================")
            println(it.text )
        }
    }
}
