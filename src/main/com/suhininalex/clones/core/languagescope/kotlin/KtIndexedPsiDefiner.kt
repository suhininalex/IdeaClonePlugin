package com.suhininalex.clones.core.languagescope.kotlin

import com.intellij.psi.PsiElement
import com.intellij.testIntegration.TestFinderHelper
import com.suhininalex.clones.core.languagescope.IndexedPsiDefiner
import com.suhininalex.clones.core.structures.IndexedSequence
import com.suhininalex.clones.ide.configuration.PluginSettings
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtPropertyAccessor

class KtIndexedPsiDefiner: IndexedPsiDefiner {

    override val fileType: String
        get() = "Kotlin"

    override fun isIndexedElement(psiElement: PsiElement): Boolean {
        val typeString = psiElement.node.elementType.toString()
        return typeString == "FUN" || typeString == "PROPERTY_ACCESSOR"
    }

    override fun isIndexedParent(psiElement: PsiElement): Boolean =
        psiElement is KtClassOrObject

    override fun createIndexedSequence(psiElement: PsiElement): IndexedSequence =
        when (psiElement) {
            is KtFunction -> KtIndexedFunSequence(psiElement)
            is KtPropertyAccessor -> KtIndexedPropertySequence(psiElement)
            else -> throw IllegalArgumentException("$psiElement must be type of KtFunction or KtPropertyAccessor")
        }
}