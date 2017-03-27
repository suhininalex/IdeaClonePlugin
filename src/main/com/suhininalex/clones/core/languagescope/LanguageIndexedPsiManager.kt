package com.suhininalex.clones.core.languagescope

import com.intellij.psi.PsiElement
import com.suhininalex.clones.core.languagescope.java.JavaIndexedPsiDefiner
import com.suhininalex.clones.core.languagescope.kotlin.KtIndexedPsiDefiner

object LanguageIndexedPsiManager {

    private val indexedPsiDefiners = HashMap<String, IndexedPsiDefiner>()

    init {
        registerNewLanguage(JavaIndexedPsiDefiner())
        registerNewLanguage(KtIndexedPsiDefiner())
    }

    fun getIndexedPsiDefiner(psiElement: PsiElement): IndexedPsiDefiner? =
        indexedPsiDefiners[psiElement.containingFile?.fileType?.name]

    fun registerNewLanguage(indexedPsiDefiner: IndexedPsiDefiner){
        indexedPsiDefiners.put(indexedPsiDefiner.fileType, indexedPsiDefiner)
    }

    fun unregisterLanguage(indexedPsiDefiner: IndexedPsiDefiner){
        indexedPsiDefiners.remove(indexedPsiDefiner.fileType)
    }
}