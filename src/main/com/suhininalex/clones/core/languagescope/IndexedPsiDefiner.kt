package com.suhininalex.clones.core.languagescope

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.roots.GeneratedSourcesFilter
import com.intellij.openapi.roots.TestSourcesFilter
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.PsiJavaFileImpl
import com.intellij.testIntegration.TestFinderHelper
import com.suhininalex.clones.core.structures.IndexedSequence
import com.suhininalex.clones.core.utils.isTestFile
import com.suhininalex.clones.core.utils.leafTraverse
import com.suhininalex.clones.ide.configuration.PluginSettings

/**
 * Defines files to be indexed
 * Provides file type, PSI Element, possible parents of the Element witch must be indexed
 */
interface IndexedPsiDefiner {

    /**
     * Defines files to be indexed
     * @see FileType.getName
     */
    val fileType: String

    fun createIndexedSequence(psiElement: PsiElement): IndexedSequence

    /**
     * Defines elements to be indexed
     * @see isIndexed
     */
    fun isIndexed(psiElement: PsiElement): Boolean

    fun getIndexedChildren(psiElement: PsiElement): List<PsiElement> =
        psiElement.leafTraverse({ isIndexed(it) }) { it.children.asSequence() }.toList()
}

