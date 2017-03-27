package com.suhininalex.clones.core.languagescope

import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.testIntegration.TestFinderHelper
import com.suhininalex.clones.core.structures.IndexedSequence
import com.suhininalex.clones.core.utils.leafTraverse
import com.suhininalex.clones.ide.configuration.PluginSettings

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
     * Defines parents of indexed elements
     * @see com.suhininalex.clones.core.languagescope.java.JavaIndexedPsiDefiner
     */
    fun isIndexedParent(psiElement: PsiElement): Boolean

    fun createIndexedSequence(psiElement: PsiElement): IndexedSequence

    /**
     * Alike isIndexedElement but it also checks plugin settings
     * @see isIndexedElement
     */
    fun isIndexed(psiElement: PsiElement): Boolean =
        if (PluginSettings.disableTestFolder && TestFinderHelper.isTest(psiElement.containingFile)) {
            false
        } else {
            isIndexedElement(psiElement)
        }

    /**
     * @return first indexed parent element
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

