package com.suhininalex.clones.core.languagescope

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.roots.GeneratedSourcesFilter
import com.intellij.openapi.roots.TestSourcesFilter
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
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

    /**
     * Defines elements to be indexed
     * @see isIndexed
     */
    fun isIndexedElement(psiElement: PsiElement): Boolean

    /**
     * Defines parents of indexed elements (excluding PsiFile and above)
     * @see com.suhininalex.clones.core.languagescope.java.JavaIndexedPsiDefiner
     */
    fun isIndexedParent(psiElement: PsiElement): Boolean

    fun createIndexedSequence(psiElement: PsiElement): IndexedSequence

    /**
     * The same as isIndexedElement but also checks plugin settings
     * @see isIndexedElement
     */
    fun isIndexed(psiElement: PsiElement): Boolean {
        return  isIndexedElement(psiElement) &&  ! isDisabledTest(psiElement) //! isGenerated(psiElement)  &&
    }

    fun isGenerated(psiElement: PsiElement): Boolean{
        return GeneratedSourcesFilter.isGeneratedSourceByAnyFilter(psiElement.containingFile.virtualFile, psiElement.project)
    }

    fun isDisabledTest(psiElement: PsiElement): Boolean {
        return PluginSettings.disableTestFolder && psiElement.containingFile.isTestFile()
    }

    /**
     * @return first indexed parent of the element
     */
    fun getIndexedParent(psiElement: PsiElement): PsiElement? {
        var current = psiElement
        while (! isIndexed(current)) {
            if (current.parent !is PsiFile) {
                current = current.parent
            } else {
                return null
            }
        }
        return current
    }

    fun getIndexedChildren(psiElement: PsiElement): List<PsiElement> =
        psiElement.leafTraverse({ isIndexed(it) }) { it.children.asSequence() }.toList()
}

