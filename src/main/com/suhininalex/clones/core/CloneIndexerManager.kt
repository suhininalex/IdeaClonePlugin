package com.suhininalex.clones.core

import com.intellij.openapi.project.Project
import com.intellij.testIntegration.TestFinderHelper
import com.suhininalex.clones.core.languagescope.LanguageIndexedPsiManager
import com.suhininalex.clones.core.languagescope.java.JavaIndexedPsiDefiner
import com.suhininalex.clones.core.languagescope.kotlin.KtIndexedPsiDefiner
import com.suhininalex.clones.core.utils.*
import com.suhininalex.clones.ide.configuration.PluginLabels
import com.suhininalex.clones.ide.configuration.PluginSettings
import nl.komponents.kovenant.task
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

    fun cancel(): Unit = synchronized(this) {
        PluginSettings.enabledForProject = false
        instance = CloneIndexer()
        initialized = false
    }

    fun initialize(): Unit = synchronized(this){

        initialized = false
        instance = CloneIndexer()
        LanguageIndexedPsiManager.initialize()

        if (! PluginSettings.enabledForProject) return

        task {
            project.allPsiFiles
        }.then {
            it.withProgressBar(initializingLabel).foreach {
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
}

private fun LanguageIndexedPsiManager.initialize(){
    clear()
    if (PluginSettings.javaSearchEnabled) registerNewLanguage(JavaIndexedPsiDefiner())
    if (PluginSettings.kotlinSearchEnabled) registerNewLanguage(KtIndexedPsiDefiner())
}
