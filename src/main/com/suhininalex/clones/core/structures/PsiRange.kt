package com.suhininalex.clones.core.structures

import com.intellij.psi.PsiElement

interface PsiRange {
    val firstPsi: PsiElement
    val lastPsi: PsiElement
}
