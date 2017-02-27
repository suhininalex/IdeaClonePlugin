package com.suhininalex.clones.core.structures

import com.intellij.psi.PsiElement

interface Clone {
    val firstPsi: PsiElement
    val lastPsi: PsiElement
}