package com.suhininalex.clones.core

import com.intellij.psi.PsiMethod
import com.suhininalex.clones.core.structures.Token
import com.suhininalex.clones.core.structures.TreeCloneClass
import com.suhininalex.clones.core.utils.*
import com.suhininalex.suffixtree.Node
import com.suhininalex.suffixtree.SuffixTree
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class CloneManager {

    internal val methodIds: MutableMap<String, Long> = HashMap()
    internal val tree = SuffixTree<Token>()
    internal val rwLock = ReentrantReadWriteLock()
    internal val minTokenLength = 20

    fun addMethod(method: PsiMethod) = rwLock.write {
        addMethodUnlocked(method)
    }

    fun removeMethod(method: PsiMethod) = rwLock.write {
        removeMethodUnlocked(method)
    }

    private fun addMethodUnlocked(method: PsiMethod) {
        if (method.stringId in methodIds) return
        val sequence = method.body?.asSequence()?.filter { it !in javaTokenFilter }?.map(::Token)?.toList() ?: return
        val id = tree.addSequence(sequence)
        methodIds.put(method.stringId, id)
    }

    private fun removeMethodUnlocked(method: PsiMethod) {
        val id = methodIds[method.stringId] ?: return
        methodIds.remove(method.stringId)
        tree.removeSequence(id)
    }

    fun getAllCloneClasses(): Sequence<TreeCloneClass>  = rwLock.read {
        tree.getAllCloneClasses(minTokenLength)
    }

    fun getAllMethodClasses(method: PsiMethod): Sequence<TreeCloneClass> = rwLock.read {
        val id = method.getId() ?: return emptySequence()
        return tree.getAllSequenceClasses(id, minTokenLength).asSequence()
    }

    fun PsiMethod.getId() = methodIds[stringId]
}

fun SuffixTree<Token>.getAllCloneClasses(minTokenLength: Int): Sequence<TreeCloneClass>  =
        root.depthFirstTraverse { it.edges.asSequence().map { it.terminal }.filter { it != null } }
                .map(::TreeCloneClass)
                .filter { it.length > minTokenLength }

fun SuffixTree<Token>.getAllSequenceClasses(id: Long, minTokenLength: Int): Sequence<TreeCloneClass>  {
    val classes = LinkedList<TreeCloneClass>()
    val visitedNodes = HashSet<Node>()

    for (branchNode in this.getAllLastSequenceNodes(id)) {
        for (currentNode in branchNode.riseTraverser()){
            if (visitedNodes.contains(currentNode)) break;
            visitedNodes.add(currentNode)
            classes.addIf(TreeCloneClass(currentNode)) {it.length > minTokenLength}
        }
    }
    return classes.asSequence()
}