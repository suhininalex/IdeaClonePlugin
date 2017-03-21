package com.suhininalex.clones.core.languagescope

import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.PsiElement
import com.suhininalex.clones.core.structures.IndexedSequence
import com.suhininalex.clones.core.utils.leafTraverse

interface IndexedPsiDefiner {

    /**
     * Defines files to be indexed
     * @see FileType.getName
     */
    val fileType: String

    /**
     * Defines elements to be indexed
     */
    fun isIndexed(psiElement: PsiElement): Boolean

    /**
     * Defines parents of indexed elements
     * @see com.suhininalex.clones.core.languagescope.java.JavaIndexedPsiDefiner
     */
    fun isIndexedParent(psiElement: PsiElement): Boolean

    fun createIndexedSequence(psiElement: PsiElement): IndexedSequence

    /**
     * @return first indexed parent element
     */
    fun getIndexedParent(psiElement: PsiElement): PsiElement? {
        var current = psiElement
        while (! isIndexed(current)) {
            current = current.parent ?: return null
        }
        return current
    }

    fun getIndexedChildren(psiElement: PsiElement): List<PsiElement> =
        psiElement.leafTraverse({ isIndexed(it) }) { it.children.asSequence() }.toList()
}

