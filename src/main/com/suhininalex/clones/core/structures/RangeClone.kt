package com.suhininalex.clones.core.structures

import com.intellij.psi.PsiElement

data class RangeClone(override val firstPsi: PsiElement, override val lastPsi: PsiElement): Clone{

    init {
        require(firstPsi.textRange.startOffset <= lastPsi.textRange.endOffset) {
            "file: ${firstPsi.containingFile}: ${firstPsi.textRange.startOffset} : ${lastPsi.textRange.endOffset}"
        }
    }
}