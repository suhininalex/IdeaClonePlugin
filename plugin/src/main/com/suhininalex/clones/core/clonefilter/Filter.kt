package com.suhininalex.clones.core.clonefilter

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.suhininalex.clones.core.TreeCloneClass
import com.suhininalex.clones.core.callInEventQueue
import nl.komponents.kovenant.Deferred
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.deferred
import nl.komponents.kovenant.task
import java.lang.Exception

class FilterTask(val treeCloneClasses: List<TreeCloneClass>, val deferred: Deferred<List<TreeCloneClass>, Exception>) : Task.Backgroundable(null, "Filtering...", true) {
    var filteredTreeClones: List<TreeCloneClass>? = null

    val filter by lazy {
        createCommonFilter(treeCloneClasses)
    }

    override fun run(progressIndicator: ProgressIndicator) {
        filteredTreeClones = treeCloneClasses.filterIndexed { i, cloneClass ->
            if (progressIndicator.isCanceled) throw InterruptedException()
            progressIndicator.fraction = i.toDouble()/ treeCloneClasses.size
            filter.isAllowed(cloneClass)
        }
    }

    override fun onSuccess() {
        deferred.resolve(filteredTreeClones!!)
    }
}

fun Sequence<TreeCloneClass>.filterClones(): List<TreeCloneClass> {
    val clones = this.toList()
    val commonFilter = createCommonFilter(clones)
    return clones.filter { commonFilter.isAllowed(it)}
}

fun List<TreeCloneClass>.filterWithProgressbar(): Promise<List<TreeCloneClass>, Exception> {
    val deferred = deferred<List<TreeCloneClass>, Exception>()
    task {
        callInEventQueue { ProgressManager.getInstance().run(FilterTask(this, deferred))  }
    }
    return deferred.promise
}

fun createCommonFilter(treeCloneClasses: List<TreeCloneClass>): CloneClassFilter {
    val subclassFilter = SubclassFilter(treeCloneClasses)
    return subclassFilter //CloneClassFilter { subclassFilter.isAllowed(it) && SubSequenceFilter.isAllowed(it) } //&& CropTailFilter.isAllowed(it)
}