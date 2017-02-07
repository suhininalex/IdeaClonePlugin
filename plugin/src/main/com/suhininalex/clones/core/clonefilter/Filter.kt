package com.suhininalex.clones.core.clonefilter

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.psi.PsiElement
import com.suhininalex.clones.core.Clone
import com.suhininalex.clones.core.CloneClass
import com.suhininalex.clones.core.callInEventQueue
import com.suhininalex.clones.ide.firstPsi
import com.suhininalex.clones.ide.selectInEditor
import nl.komponents.kovenant.Deferred
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.deferred
import nl.komponents.kovenant.task
import java.lang.Exception

class FilterTask(val cloneClasses: List<CloneClass>, val deferred: Deferred<List<CloneClass>, Exception>) : Task.Backgroundable(null, "Filtering...", true) {
    var filteredClones: List<CloneClass>? = null

    val filter by lazy {
        createCommonFilter(cloneClasses)
    }

    override fun run(progressIndicator: ProgressIndicator) {
        filteredClones = cloneClasses.filterIndexed { i, cloneClass ->
            if (progressIndicator.isCanceled) throw InterruptedException()
            progressIndicator.fraction = i.toDouble()/cloneClasses.size
            filter.isAllowed(cloneClass)
        }
    }

    override fun onSuccess() {
        deferred.resolve(filteredClones!!)
    }
}

fun Sequence<CloneClass>.filterClones(): List<CloneClass> {
    val clones = this.toList()
    val commonFilter = createCommonFilter(clones)
    return clones.filter { commonFilter.isAllowed(it)}
}

fun List<CloneClass>.filterWithProgressbar(): Promise<List<CloneClass>, Exception> {
    val deferred = deferred<List<CloneClass>, Exception>()
    task {
        callInEventQueue { ProgressManager.getInstance().run(FilterTask(this, deferred))  }
    }
    return deferred.promise
}

fun createCommonFilter(cloneClasses: List<CloneClass>): CloneClassFilter {
    val subclassFilter = SubclassFilter(cloneClasses)
    return CloneClassFilter { subclassFilter.isAllowed(it) && SubSequenceFilter.isAllowed(it) } //&& CropTailFilter.isAllowed(it)
}