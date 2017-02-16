package com.suhininalex.clones

import com.intellij.JavaTestUtil
import com.intellij.openapi.application.PluginPathManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import com.siyeh.ig.psiutils.TestUtils
import com.suhininalex.clones.core.*
import com.suhininalex.clones.core.clonefilter.LengthFilter
import com.suhininalex.clones.core.clonefilter.filterClones
import com.suhininalex.clones.ide.ClonesViewProvider
import com.suhininalex.clones.ide.childrenMethods
import com.suhininalex.suffixtree.SuffixTree
import java.awt.EventQueue
import java.util.*
import java.util.function.ToLongBiFunction
import kotlin.concurrent.read

class PluginTest : LightPlatformCodeInsightFixtureTestCase() {

    override fun getTestDataPath() = "testdata/"

    override fun setUp() {
        super.setUp()
    }

    fun testSimpleTest() {
        val directory = myFixture.copyDirectoryToProject("sphinx4", "")
        myFixture.psiManager.findDirectory(directory)!!.getPsiJavaFiles().forEach {
            println(it.name)
        }

        val cloneManager = CloneManager()
        myFixture.file.childrenMethods.forEach {
            cloneManager.addMethod(it)
        }

        val clones = cloneManager.getAllCloneClasses().filterClones().toList()

        val raw = clones.map { RangeCloneClass(it.clones.map { RangeClone(it.firstPsi, it.lastPsi) }.toList()) }

        val result = filterSameCloneRangeClasses(clones.extractSiblingClones())
        var classN = 1
        result.forEach {
            val clone = it.cloneRanges[0]
            println("Class ${classN++}")
            println("==========================================================")
            clone.printText()
            println("SCORE: ${clone.scoreSelfCoverage()}")
        }
    }
}

