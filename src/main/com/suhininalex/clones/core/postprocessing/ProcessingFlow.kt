package com.suhininalex.clones.core.postprocessing

import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.vfs.VirtualFile
import com.suhininalex.clones.core.CloneIndexer
import com.suhininalex.clones.core.structures.CloneClass
import com.suhininalex.clones.core.utils.backgroundTask
import com.suhininalex.clones.core.utils.withProgressBar
import com.suhininalex.clones.ide.configuration.PluginLabels
import com.suhininalex.clones.ide.configuration.PluginSettings
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.task
import nl.komponents.kovenant.thenApply
import java.lang.Exception

val subClassFiltering = PluginLabels.getMessage("progressbar-filtering-subclass")
val siblingFiltering = PluginLabels.getMessage("progressbar-filtering-sibling")
val mergeFiltering = PluginLabels.getMessage("progressbar-filtering-merge")
val selfcoveredFiltering = PluginLabels.getMessage("progressbar-filtering-selfcovered")
val beforeFiltering = PluginLabels.getMessage("progressbar-filtering-before-filters")

fun CloneIndexer.getAllFilteredClones(): Promise<List<CloneClass>, Exception> =
    task {
        ProgressManager.getInstance().backgroundTask(beforeFiltering){ getAllCloneClasses().notLongestSequenceFilter() }.get()
    }.thenApply {
        withProgressBar(subClassFiltering).filterSubClassClones().get().validClonesFilter()
    }.thenApply {
        withProgressBar(siblingFiltering).splitSiblingClones().get()
    }.thenApply {
        withProgressBar(mergeFiltering).mergeCloneClasses().get()
    }.thenApply {
        withProgressBar(selfcoveredFiltering).filterSelfCoveredClasses().get()
    }.fail {
        throw it
    }

fun CloneIndexer.getFileFilteredClones(virtualFile: VirtualFile): List<CloneClass> {
    val clones = getAllFileCloneClasses(virtualFile)
            .notLongestSequenceFilter()
            .validClonesFilter()
    if (PluginSettings.enableGaps) {
        val (gappedClones, monoliteClones) = clones.uniteNearbyClones(virtualFile)
        val allClones = gappedClones + monoliteClones.splitSiblingClones().mergeCloneClasses()
        return allClones.filterSelfCoveredClasses()
    } else {
        return clones.splitSiblingClones().mergeCloneClasses().filterSelfCoveredClasses()
    }
}