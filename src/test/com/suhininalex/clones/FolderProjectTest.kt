package com.suhininalex.clones

import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiMethod
import com.intellij.psi.impl.source.tree.ElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import com.suhininalex.clones.core.*
import com.suhininalex.clones.core.languagescope.java.JavaIndexedSequence
import com.suhininalex.clones.core.utils.findTokens
import kotlin.properties.Delegates

open class FolderProjectTest(val testFolder: String) : LightCodeInsightFixtureTestCase() {

    var baseDirectoryPsi by Delegates.notNull<PsiDirectory>()

    override fun getTestDataPath() = testFolder

    override fun setUp() {
        super.setUp()
        val directory = myFixture.copyDirectoryToProject("/", "")
        baseDirectoryPsi = myFixture.psiManager.findDirectory(directory)!!
//        baseDirectoryPsi.findTokens(TokenSet.create(ElementType.METHOD)).forEach { method ->
//            if (method is PsiMethod)
//                CloneIndexer.addSequence(JavaIndexedSequence(method))
//        }
    }

}