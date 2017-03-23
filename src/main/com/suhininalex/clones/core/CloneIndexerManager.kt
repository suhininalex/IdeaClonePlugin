package com.suhininalex.clones.core

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.suhininalex.clones.core.languagescope.LanguageIndexedPsiManager
import com.suhininalex.clones.core.utils.*
import com.suhininalex.clones.ide.configuration.PluginLabels
import com.suhininalex.clones.ide.configuration.PluginSettings
import nl.komponents.kovenant.then
import org.jetbrains.kotlin.psi.KtFile

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

        project.allPsiFiles.filter { it is KtFile }.filter{ ! it.isDirectory}.withProgressBar(initializingLabel).foreach {
            Application.runReadAction {
                val indexedPsiDefiner = LanguageIndexedPsiManager.getIndexedPsiDefiner(it)
                indexedPsiDefiner?.getIndexedChildren(it)?.forEach {
                    instance.addSequence(indexedPsiDefiner.createIndexedSequence(it))
                }
            }
        }.then {
            initialized = true
        }.fail {
            cancel()
        }
    }
}

