package com.suhininalex.clones.core.languagescope.java

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.suhininalex.clones.core.languagescope.IndexedPsiDefiner
import com.suhininalex.clones.core.structures.IndexedSequence

class JavaIndexedPsiDefiner : IndexedPsiDefiner {

    override val fileType: String
        get() = "JAVA"

    override fun isIndexedElement(psiElement: PsiElement): Boolean =
        psiElement is PsiMethod

    override fun isIndexedParent(psiElement: PsiElement): Boolean =
        psiElement is PsiClass

    override fun createIndexedSequence(psiElement: PsiElement): IndexedSequence {
        require(isIndexed(psiElement))
        return JavaIndexedSequence(psiElement as PsiMethod)
    }

}