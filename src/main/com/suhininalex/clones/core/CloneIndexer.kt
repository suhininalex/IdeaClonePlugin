package com.suhininalex.clones.core

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.suhininalex.clones.core.languagescope.languageSerializer
import com.suhininalex.clones.core.postprocessing.*
import com.suhininalex.clones.core.structures.SourceToken
import com.suhininalex.clones.core.structures.TreeCloneClass
import com.suhininalex.clones.core.utils.*
import com.suhininalex.clones.ide.configuration.PluginSettings
import com.suhininalex.suffixtree.Node
import com.suhininalex.suffixtree.SuffixTree
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.collections.HashMap
import kotlin.concurrent.read
import kotlin.concurrent.write

object CloneIndexer {

    internal var tree = SuffixTree<SourceToken>()
    internal val rwLock = ReentrantReadWriteLock()

    fun clear() {
        fileSequenceIds.clear()
        tree = SuffixTree()
    }

    val fileSequenceIds = HashMap<VirtualFile, List<Long>>()

    fun addFile(psiFile: PsiFile): Unit = rwLock.write {
        if (psiFile.virtualFile in fileSequenceIds) return
        val indexedPsiDefiner = psiFile.project.languageSerializer.getIndexedPsiDefiner(psiFile)
        val ids = mutableListOf<Long>()
        indexedPsiDefiner?.getIndexedChildren(psiFile)?.map {
            val sequence = indexedPsiDefiner.createIndexedSequence(it).sequence.toList()
            if (sequence.size > PluginSettings.minCloneLength){
                val id = tree.addSequence(sequence)
                ids += id
            }
        }
        fileSequenceIds.put(psiFile.virtualFile, ids)
    }

    fun removeFile(virtualFile: VirtualFile): Unit = rwLock.write {
        val ids = fileSequenceIds[virtualFile] ?: return
        ids.forEach {
            tree.removeSequence(it)
        }
        fileSequenceIds.remove(virtualFile)
    }

    fun getAllFileCloneClasses(virtualFile: VirtualFile): List<TreeCloneClass> = rwLock.read {
        val ids = fileSequenceIds[virtualFile] ?: return emptyList()
        return ids
                .flatMap { tree.getAllSequenceClasses(it, PluginSettings.minCloneLength).toList() }
                .filterSubClassClones()
    }

    fun getAllCloneClasses(): Sequence<TreeCloneClass>  = rwLock.read {
        tree.getAllCloneClasses(PluginSettings.minCloneLength)
    }

}

fun SuffixTree<SourceToken>.getAllCloneClasses(minTokenLength: Int): Sequence<TreeCloneClass> =
     root.depthFirstTraverse { it.edges.asSequence().mapNotNull { it.terminal }}
             .map(::TreeCloneClass)
             .filter { it.length > minTokenLength }

fun SuffixTree<SourceToken>.getAllSequenceClasses(id: Long, minTokenLength: Int): Sequence<TreeCloneClass>  {
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