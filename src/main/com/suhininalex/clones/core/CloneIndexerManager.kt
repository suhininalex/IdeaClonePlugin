package com.suhininalex.clones.core

import com.intellij.openapi.project.Project
import com.intellij.testIntegration.TestFinderHelper
import com.suhininalex.clones.core.languagescope.LanguageIndexedPsiManager
import com.suhininalex.clones.core.utils.*
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

        initialized = false
        instance = CloneIndexer()

        if (! PluginSettings.enabledForProject) return

        project.allPsiFiles.withProgressBar(initializingLabel).foreach {
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

