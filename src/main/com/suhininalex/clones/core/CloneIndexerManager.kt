package com.suhininalex.clones.core

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.suhininalex.clones.core.languagescope.LanguageIndexedPsiManager
import com.suhininalex.clones.core.languagescope.kotlin.KtIndexedPsiDefiner
import com.suhininalex.clones.core.utils.*
import com.suhininalex.clones.ide.configuration.PluginLabels
import com.suhininalex.clones.ide.configuration.PluginSettings
import nl.komponents.kovenant.then
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtElementImpl
import org.jetbrains.kotlin.psi.psiUtil.allChildren

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




//        val ktIndexedPsiDefiner = KtIndexedPsiDefiner()
//        project.allPsiFiles
//                .filter { it.fileType.name == ktIndexedPsiDefiner.fileType }
//                .forEach {
//                    println(it.name)
//                    ktIndexedPsiDefiner.getIndexedChildren(it).forEach {
//                        val sequence = ktIndexedPsiDefiner.createIndexedSequence(it)
//                        println(sequence.id)
//                        println(sequence.sequence.toList())
//                        println("-------------------")
//                    }
//                    println("============================")
//                }


        if (! PluginSettings.enabledForProject) return

        project.allPsiFiles.filter { ".kt" in it.name }.withProgressBar(initializingLabel).foreach {
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

