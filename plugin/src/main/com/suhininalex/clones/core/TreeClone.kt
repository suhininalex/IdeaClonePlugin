package com.suhininalex.clones.core

import com.intellij.psi.PsiElement
import com.suhininalex.clones.core.interfaces.Clone

class TreeClone(val firstElement: Token, val lastElement: Token): Clone {
    override val firstPsi: PsiElement
        get() = firstElement.source
    override val lastPsi: PsiElement
        get() = lastElement.source
}