package com.suhininalex.clones.core.postprocessing

import com.intellij.psi.PsiMethod
import com.suhininalex.clones.core.CloneIndexer
import com.suhininalex.clones.core.structures.CloneClass
import com.suhininalex.clones.core.utils.withProgressBar
import com.suhininalex.clones.ide.configuration.PluginLabels
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.task
import nl.komponents.kovenant.thenApply
import java.lang.Exception

val subClassFiltering = PluginLabels.getLabel("progressbar-filtering-subclass")
val siblingFiltering = PluginLabels.getLabel("progressbar-filtering-sibling")
val mergeFiltering = PluginLabels.getLabel("progressbar-filtering-merge")
val selfcoveredFiltering = PluginLabels.getLabel("progressbar-filtering-selfcovered")

fun CloneIndexer.getAllFilteredClones(): Promise<List<CloneClass>, Exception> =
    task {
        getAllCloneClasses().toList()
    }.thenApply {
        withProgressBar(subClassFiltering).filterSubClassClones().get()
    }.thenApply {
        withProgressBar(siblingFiltering).splitSiblingClones().get()
    }.thenApply {
        withProgressBar(mergeFiltering).mergeCloneClasses().get()
    }.thenApply {
        withProgressBar(selfcoveredFiltering).filterSelfCoveredClasses().get()
    }.fail {
        throw it
    }

fun CloneIndexer.getMethodFilteredClones(method: PsiMethod): List<CloneClass> =
    getAllMethodClasses(method).toList().filterSubClassClones().splitSiblingClones().mergeCloneClasses().filterSelfCoveredClasses()