package com.suhininalex.clones.core.languagescope.java

import com.intellij.psi.PsiMethod
import com.suhininalex.clones.core.structures.IndexedSequence
import com.suhininalex.clones.core.structures.Token
import com.suhininalex.clones.core.utils.asSequence
import com.suhininalex.clones.core.utils.isNoiseElement

class JavaIndexedSequence(val psiMethod: PsiMethod): IndexedSequence {

    override val sequence: Sequence<Token>
        get() = psiMethod.body?.asSequence()?.filterNot(::isNoiseElement)?.map(::Token) ?: emptySequence()

    override val id: String
        get() = with (psiMethod) {
                    containingFile.containingDirectory.name + "." +
                    containingClass!!.name + "." +
                    name + "." +
                    parameterList
                }
}
