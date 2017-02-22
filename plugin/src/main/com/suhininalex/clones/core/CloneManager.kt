package com.suhininalex.clones.core

import com.intellij.psi.PsiMethod
import com.suhininalex.clones.core.clonefilter.*
import com.suhininalex.suffixtree.Node
import com.suhininalex.suffixtree.SuffixTree
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class CloneManager {

    internal val methodIds: MutableMap<String, Long> = HashMap()
    internal val tree = SuffixTree<Token>()
    internal val rwLock = ReentrantReadWriteLock()
    internal val lengthClassFilter = LengthFilter(25)

    fun addMethod(method: PsiMethod) = rwLock.write {
        addMethodUnlocked(method)
    }

    fun removeMethod(method: PsiMethod) = rwLock.write {
        removeMethodUnlocked(method)
    }

    fun updateMethod(method: PsiMethod) = rwLock.write {
        removeMethodUnlocked(method)
        addMethodUnlocked(method)
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
        tree.root.depthFirstTraverse { it.edges.asSequence().map { it.terminal }.filter { it != null } }
                .map(::TreeCloneClass)
                .filter { lengthClassFilter.isAllowed(it) }
    }

    fun getAllMethodClasses(method: PsiMethod): Sequence<TreeCloneClass> = rwLock.read {
        val classes = LinkedList<TreeCloneClass>()
        val visitedNodes = HashSet<Node>()
        val id = method.getId() ?: return emptySequence()// throw IllegalStateException("There are no such method in CloneManager (${method.stringId}).")

        for (branchNode in tree.getAllLastSequenceNodes(id)) {
            for (currentNode in branchNode.riseTraverser()){
                if (visitedNodes.contains(currentNode)) break;
                visitedNodes.add(currentNode)
                classes.addIf(TreeCloneClass(currentNode)) {lengthClassFilter.isAllowed(it)}
            }
        }
        return classes.asSequence()
    }

    fun PsiMethod.getId() = methodIds[stringId]
}