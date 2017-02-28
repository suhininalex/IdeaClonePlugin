package com.suhininalex.clones.core.structures

import com.intellij.psi.PsiElement

data class RangeClone(override val firstPsi: PsiElement, override val lastPsi: PsiElement): Clone

