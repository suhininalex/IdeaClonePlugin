package com.suhininalex.clones.core.utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.suhininalex.clones.core.Token
import com.suhininalex.clones.core.structures.TreeCloneClass
import com.suhininalex.clones.core.structures.Clone
import com.suhininalex.clones.ide.ProjectClonesInitializer
import com.suhininalex.clones.ide.document
import com.suhininalex.suffixtree.Edge
import java.lang.IllegalArgumentException

fun TreeCloneClass.tokenSequence(): Sequence<Token> =
        treeNode.descTraverser().asSequence().map { it.parentEdge }.filter { it != null }.flatMap(Edge::asSequence)

@Suppress("UNCHECKED_CAST")
fun Edge.asSequence(): Sequence<Token> {
    if (isTerminal) {
        throw IllegalArgumentException("You should never call this method for terminating edge.")
    } else {
        return (sequence.subList(begin, end + 1) as MutableList<Token>).asSequence()
    }
}

//TODO remove offset
fun Clone.getTextRangeInMethod(offset: Int) = TextRange(firstPsi.textRange.startOffset - offset, lastPsi.textRange.endOffset-offset)

fun Project.getCloneManager() = ProjectClonesInitializer.getInstance(this)

fun Clone.printText(){
    val range = TextRange(firstPsi.textRange.startOffset, lastPsi.textRange.endOffset)
    println(firstPsi.document.getText(range))
}
