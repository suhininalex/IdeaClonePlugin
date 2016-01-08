package com.suhininalex.clones

import clones.Utils
import com.intellij.psi.PsiMethod
import com.intellij.psi.impl.source.tree.ElementType
import com.intellij.psi.tree.TokenSet
import com.suhininalex.clones.clonefilter.LengthFilter
import com.suhininalex.clones.clonefilter.SubclassFilter
import com.suhininalex.suffixtree.Node
import com.suhininalex.suffixtree.SuffixTree
import stream
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock

class CloneManager(internal val minCloneLength: Int) {

    internal val methodIds: MutableMap<String, Long> = HashMap()
    internal val tree = SuffixTree<Token>()
    internal val rwLock = ReentrantReadWriteLock()
    internal val lengthFilter = LengthFilter(minCloneLength)

    private fun getWriteLock() = rwLock.writeLock()

    private fun getReadLock() = rwLock.readLock()

    fun addMethod(method: PsiMethod) = getWriteLock() use {
        addMethodUnlocked(method)
    }

    fun removeMethod(method: PsiMethod) = getWriteLock() use {
        removeMethodUnlocked(method)
    }

    fun updateMethod(method: PsiMethod) = getWriteLock() use {
        removeMethodUnlocked(method)
        addMethodUnlocked(method)
    }

    fun getAllFilteredClones(): List<CloneClass> = getReadLock() use {
        getFilteredClones(getAllCloneClasses())
    }

    fun getMethodFilteredClasses(method: PsiMethod): List<CloneClass> = getReadLock() use {
        getFilteredClones(getAllMethodClones(method))
     }

    private fun getTokenFilter() =
            TokenSet.create(ElementType.WHITE_SPACE, ElementType.SEMICOLON, ElementType.RBRACE, ElementType.LBRACE, ElementType.DOC_COMMENT, ElementType.C_STYLE_COMMENT)

    private fun addMethodUnlocked(method: PsiMethod) {
        val methodBody = method.body ?: return
        val id = tree.addSequence(Utils.makeTokenSequence(methodBody, getTokenFilter(), method))
        methodIds.put(method.getStringId(), id)
    }

    private fun removeMethodUnlocked(method: PsiMethod) {
        val id = methodIds[method.getStringId()] ?: throw IllegalArgumentException("There are no such method!")
        methodIds.remove(method.getStringId())
        tree.removeSequence(id)
    }

    private fun getAllCloneClasses() = buildLinkedList<CloneClass> {
        val stack = Stack<Node>()
        stack.push(tree.root)

        while (!stack.isEmpty()) {
            stack.pop()
                .edges.stream()
                .map { it.terminal }
                .filter { it != null }
                .forEach { terminal ->
                    stack.push(terminal)
                    addIf(CloneClass(terminal)) {lengthFilter.isAllowed(it)}
                }
        }
    }

    fun getFilteredClones(cloneClasses: List<CloneClass>): List<CloneClass> {
        val subClassFilter = SubclassFilter(cloneClasses)
        return cloneClasses.filter { subClassFilter.isAllowed(it) }
    }

    private fun getAllMethodClones(method: PsiMethod): List<CloneClass> {
        val visitedNodes = HashSet<Node>()
        val id = method.getId() ?: throw IllegalStateException("There are no such method!")

        return buildLinkedList {
            for (branchNode in tree.getAllLastSequenceNodes(id)) {
                for (currentNode in branchNode.riseTraverser()){
                    if (visitedNodes.contains(currentNode)) break;
                    visitedNodes.add(currentNode)
                    addIf(CloneClass(currentNode)) {lengthFilter.isAllowed(it)}
                }
            }
        }
    }

    fun PsiMethod.getId() = methodIds[getStringId()]
}