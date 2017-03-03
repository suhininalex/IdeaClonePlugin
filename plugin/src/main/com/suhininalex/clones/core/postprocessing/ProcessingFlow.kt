package com.suhininalex.clones.core.postprocessing

import com.intellij.psi.PsiMethod
import com.suhininalex.clones.core.CloneManager
import com.suhininalex.clones.core.structures.CloneClass
import com.suhininalex.clones.core.utils.withProgressBar
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.task
import nl.komponents.kovenant.thenApply
import java.lang.Exception


fun CloneManager.getAllFilteredClones(): Promise<List<CloneClass>, Exception> =
    task {
        getAllCloneClasses().toList()
    }.thenApply {
        withProgressBar("Filtering").filterSubClassClones().get()
    }.thenApply {
        withProgressBar("Siblings").splitSiblingClones().get()
    }.thenApply {
        withProgressBar("Same Ranges").mergeCloneClasses().get()
    }.thenApply {
        withProgressBar("Self covered").filterSelfCoveredClasses().get()
    }.fail {
        throw it
    }

fun CloneManager.getMethodFilteredClones(method: PsiMethod): List<CloneClass> =
    getAllMethodClasses(method).toList().filterSubClassClones().splitSiblingClones().mergeCloneClasses().filterSelfCoveredClasses()