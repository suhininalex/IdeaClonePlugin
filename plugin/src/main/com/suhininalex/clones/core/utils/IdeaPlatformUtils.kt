package com.suhininalex.clones.core.utils

import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.ElementType
import com.intellij.psi.tree.TokenSet

val PsiMethod.stringId: String
    get() =
    containingFile.containingDirectory.name + "." +
            containingClass!!.name + "." +
            name + "." +
            parameterList;

val Application: Application
    get() = ApplicationManager.getApplication()

fun Project.getAllPsiJavaFiles() =
        PsiManager.getInstance(this).findDirectory(baseDir)!!.getPsiJavaFiles()

fun PsiDirectory.getPsiJavaFiles(): Sequence<PsiJavaFile> =
        this.depthFirstTraverse { it.subdirectories.asSequence() }.flatMap { it.files.asSequence() }.filterIsInstance<PsiJavaFile>()

fun PsiElement.findTokens(filter: TokenSet): Sequence<PsiElement> =
        this.leafTraverse({it in filter}) {it.children.asSequence()}

operator fun TokenSet.contains(element: PsiElement): Boolean = this.contains(element.node?.elementType)

fun PsiElement.asSequence(): Sequence<PsiElement> =
        this.depthFirstTraverse { it.children.asSequence() }.filter { it.firstChild == null }

val javaTokenFilter = TokenSet.create(
        ElementType.WHITE_SPACE, ElementType.DOC_COMMENT, ElementType.C_STYLE_COMMENT, ElementType.END_OF_LINE_COMMENT, ElementType.REFERENCE_PARAMETER_LIST, ElementType.MODIFIER_LIST
)
