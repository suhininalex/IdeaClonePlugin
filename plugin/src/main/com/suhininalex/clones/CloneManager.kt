package com.suhininalex.clones

import clones.Utils
import com.intellij.psi.PsiMethod
import com.intellij.psi.impl.source.tree.ElementType
import com.intellij.psi.tree.TokenSet
import com.suhininalex.clones.clonefilter.SubclassFilter
import com.suhininalex.suffixtree.Node
import com.suhininalex.suffixtree.SuffixTree
import stream
import java.util.HashMap
import java.util.HashSet
import java.util.Stack
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

class CloneManager(internal val minCloneLength: Int) {

    //TODO String id! srsly?
    internal val methodIds: MutableMap<String, Long> = HashMap()
    internal val tree = SuffixTree<Token>()
    internal val rwLock: ReadWriteLock = ReentrantReadWriteLock()


    private inline fun getWriteLock() = rwLock.writeLock()

    private inline fun getReadLock() = rwLock.readLock()

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

    fun getMethodFilteredClones(method: PsiMethod): List<CloneClass> = getReadLock() use {
        getFilteredClones(getAllMethodClones(method))
     }

    private fun getTokenFilter() =
            TokenSet.create(ElementType.WHITE_SPACE, ElementType.SEMICOLON, ElementType.RBRACE, ElementType.LBRACE, ElementType.DOC_COMMENT, ElementType.C_STYLE_COMMENT)

    private fun addMethodUnlocked(method: PsiMethod) {
        val methodBody = method?.body ?: throw IllegalArgumentException("There are no body in the method!")
        val id = tree.addSequence(Utils.makeTokenSequence(methodBody, getTokenFilter()))
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
                    val cloneClass = CloneClass(terminal)
                    if (cloneClass.checkClone()) add(cloneClass)
                }
        }
    }

    fun getFilteredClones(cloneClasses: List<CloneClass>): List<CloneClass> {
        val subClassFilter = SubclassFilter(cloneClasses)
        return cloneClasses.filter { subClassFilter.isAllowed(it) }
    }

    private fun getAllMethodClones(method: PsiMethod): List<CloneClass> {
        val visitedNodes = HashSet<Node>()
        val id = methodIds[method.getStringId()] ?: throw IllegalStateException("There are no such method!")

        return buildLinkedList {
            for (branchNode in tree.getAllLastSequenceNodes(id)) {
                for (currentNode in branchNode.riseIterator()){
                    if (visitedNodes.contains(currentNode)) break;
                    visitedNodes.add(currentNode)
                    val cloneClass = CloneClass(currentNode)
                    if (cloneClass.checkClone()) add(cloneClass)
                }
            }
        }
    }

    private fun CloneClass.checkClone() = !isEmpty && length > minCloneLength
}