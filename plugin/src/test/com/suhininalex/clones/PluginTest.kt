package com.suhininalex.clones

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
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

class PluginTest : LightCodeInsightFixtureTestCase() {

    override fun getTestDataPath() = "C:\\projects\\work\\IdeaClonePlugin\\plugin\\src\\testData"

    fun testSimpleTest() {
        myFixture.configureByFile("SimpleClass.java")
        val cloneManager = CloneManager()
        myFixture.file.childrenMethods.forEach {
            cloneManager.addMethod(it)
        }
        val clones = cloneManager.getAllCloneClasses().filterClones().toList()

        val raw = clones.map { CloneRangeClass(it.clones.map { CloneRange(it.firstPsi, it.lastPsi) }.toList()) }

        val c = extractSiblingClones(clones)
        val result = filterSameCloneRangeClasses(c)
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

