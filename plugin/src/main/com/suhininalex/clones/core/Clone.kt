package com.suhininalex.clones.core

import com.intellij.psi.PsiElement

class Clone(val cloneClass: CloneClass, val firstElement: Token, val lastElement: Token)

val Clone.firstPsi: PsiElement
    get() = firstElement.source

val Clone.lastPsi: PsiElement
    get() = lastElement.source