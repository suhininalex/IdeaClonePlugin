package com.suhininalex.clones.core

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.impl.source.tree.ElementType
import com.intellij.psi.tree.TokenSet
import com.suhininalex.clones.core.utils.Application
import com.suhininalex.clones.core.utils.findTokens
import com.suhininalex.clones.core.utils.getAllPsiJavaFiles
import com.suhininalex.clones.core.utils.withProgressBar
import com.suhininalex.clones.ide.configuration.PluginLabels
import com.suhininalex.clones.ide.configuration.PluginSettings
import nl.komponents.kovenant.then

private val initializingLabel = PluginLabels.getLabel("progressbar-filtering-initializing")

private var cachedCloneManager: CloneIndexerManager? = null
val Project.cloneManager: CloneIndexerManager
    get() {
        if (cachedCloneManager?.project != this) {
            cachedCloneManager = CloneIndexerManager(this)
        }
        return cachedCloneManager!!
    }

class CloneIndexerManager(val project: Project){
    var initialized: Boolean = false
        private set

    var instance: CloneIndexer = CloneIndexer()
        private set

    fun cancel(){
        PluginSettings.enabledForProject = false
        instance = CloneIndexer()
        initialized = false
    }

    fun initialize(){
        if (! PluginSettings.enabledForProject) return

        val files: List<PsiJavaFile> = Application.runReadAction ( Computable {
            project.getAllPsiJavaFiles().toList()
        })


        files.withProgressBar(initializingLabel).foreach {
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