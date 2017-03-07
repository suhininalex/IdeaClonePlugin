package com.suhininalex.clones.core

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.impl.source.tree.ElementType
import com.intellij.psi.tree.TokenSet
import com.suhininalex.clones.core.structures.Token
import com.suhininalex.clones.core.structures.TreeCloneClass
import com.suhininalex.clones.core.utils.*
import com.suhininalex.clones.ide.configuration.PluginSettings
import com.suhininalex.suffixtree.Node
import com.suhininalex.suffixtree.SuffixTree
import nl.komponents.kovenant.then
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class CloneManager {

    internal val methodIds: MutableMap<String, Long> = HashMap()
    internal val tree = SuffixTree<Token>()
    internal val rwLock = ReentrantReadWriteLock()

    fun addMethod(method: PsiMethod) = rwLock.write {
        addMethodUnlocked(method)
    }

    fun removeMethod(method: PsiMethod) = rwLock.write {
        removeMethodUnlocked(method)
    }

    private fun addMethodUnlocked(method: PsiMethod) {
        if (method.stringId in methodIds) return
        val sequence = method.body?.asSequence()?.filterNot(::isNoiseElement)?.map(::Token)?.toList() ?: return
        val id = tree.addSequence(sequence)
        methodIds.put(method.stringId, id)
    }

    private fun removeMethodUnlocked(method: PsiMethod) {
        val id = methodIds[method.stringId] ?: return
        methodIds.remove(method.stringId)
        tree.removeSequence(id)
    }

    fun getAllCloneClasses(): Sequence<TreeCloneClass>  = rwLock.read {
        tree.getAllCloneClasses(PluginSettings.minCloneLength)
    }

    fun getAllMethodClasses(method: PsiMethod): Sequence<TreeCloneClass> = rwLock.read {
        val id = method.getId() ?: return emptySequence()
        return tree.getAllSequenceClasses(id, PluginSettings.minCloneLength).asSequence()
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

var cachedCloneManager: ProjectCloneManager? = null
fun Project.getCloneManager(): ProjectCloneManager {
    if (cachedCloneManager?.project != this){
        cachedCloneManager = ProjectCloneManager(this)
    }
    return cachedCloneManager!!
}


class ProjectCloneManager(val project: Project){
    var initialized: Boolean = false
        private set

    var instance: CloneManager = CloneManager()
        private set

    fun cancel(){
        PluginSettings.enabledForProject = false
        instance = CloneManager()
        initialized = false
    }

    fun initialize(){
        if (! PluginSettings.enabledForProject) return

        val files: List<PsiJavaFile> = Application.runReadAction ( Computable {
            project.getAllPsiJavaFiles().toList()
        })

        files.withProgressBar("Initializing").foreach {
            Application.runReadAction {
                it.findTokens(TokenSet.create(ElementType.METHOD)).forEach {
                    instance.addMethod(it as PsiMethod)
                }
            }
        }.then {
            initialized = true
        }.fail {
            cancel()
        }
    }
}