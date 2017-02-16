package com.suhininalex.clones.core.interfaces

import com.intellij.psi.PsiElement

interface Clone {
    val firstPsi: PsiElement
    val lastPsi: PsiElement
}