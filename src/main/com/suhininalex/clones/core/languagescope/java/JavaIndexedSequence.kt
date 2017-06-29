package com.suhininalex.clones.core.languagescope.java

import com.intellij.psi.PsiMethod
import com.suhininalex.clones.core.structures.IndexedSequence
import com.suhininalex.clones.core.structures.SourceToken
import com.suhininalex.clones.core.utils.asSequence
import com.suhininalex.clones.core.utils.isNoiseElement

class JavaIndexedSequence(val psiMethod: PsiMethod): IndexedSequence {

    override val sequence: Sequence<SourceToken>
        get() = psiMethod.body?.asSequence()?.filterNot(::isNoiseElement)?.map(::SourceToken) ?: emptySequence()

    val stringId = psiMethod.containingFile.name + psiMethod.name + psiMethod.parameterList + psiMethod.containingClass
            //TODO мы не храним уже ссылки
    override val id: Int = stringId.hashCode()
            //psiMethod.node.hashCode()
}
