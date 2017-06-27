package com.suhininalex.clones.core.structures

import com.intellij.psi.PsiElement

class TreeClone(val firstElement: SourceToken, val lastElement: SourceToken): Clone {
    override val firstPsi: PsiElement
        get() = firstElement.source

    override val lastPsi: PsiElement
        get() = lastElement.source
}