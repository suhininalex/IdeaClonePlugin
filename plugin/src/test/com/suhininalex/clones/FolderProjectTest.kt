package com.suhininalex.clones

import com.intellij.psi.PsiDirectory
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import com.suhininalex.clones.core.*
import com.suhininalex.clones.core.utils.childrenMethods
import kotlin.properties.Delegates

open class FolderProjectTest(val testFolder: String) : LightCodeInsightFixtureTestCase() {

    val cloneManager = CloneManager()

    var baseDirectoryPsi by Delegates.notNull<PsiDirectory>()

    override fun getTestDataPath() = testFolder

    override fun setUp() {
        super.setUp()
        val directory = myFixture.copyDirectoryToProject("/", "")
        baseDirectoryPsi = myFixture.psiManager.findDirectory(directory)!!
        baseDirectoryPsi.childrenMethods.forEach {
            cloneManager.addMethod(it)
        }
    }

}