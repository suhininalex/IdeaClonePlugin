package com.suhininalex.clones.core.structures

import com.intellij.psi.PsiElement
import com.suhininalex.clones.core.Token
import com.suhininalex.clones.core.structures.Clone

class TreeClone(val firstElement: Token, val lastElement: Token): Clone {
    override val firstPsi: PsiElement
        get() = firstElement.source
    override val lastPsi: PsiElement
        get() = lastElement.source
}