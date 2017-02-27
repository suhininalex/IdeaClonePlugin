package com.suhininalex.clones.core.clonefilter

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.suhininalex.clones.core.structures.TreeCloneClass
import com.suhininalex.clones.core.utils.callInEventQueue
import nl.komponents.kovenant.Deferred
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.deferred
import nl.komponents.kovenant.task
import java.lang.Exception

class FilterTask(val treeCloneClasses: List<TreeCloneClass>, val deferred: Deferred<List<TreeCloneClass>, Exception>) : Task.Backgroundable(null, "Filtering...", true) {
    var filteredTreeClones: List<TreeCloneClass>? = null

    val filter by lazy {
        SubclassFilter(treeCloneClasses)
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

//TODO rename
fun Sequence<TreeCloneClass>.filterClones(): List<TreeCloneClass> {
    val clones = this.toList()
    val subClassFilter = SubclassFilter(clones)
    return clones.filter { subClassFilter.isAllowed(it)}
}

fun List<TreeCloneClass>.filterWithProgressbar(): Promise<List<TreeCloneClass>, Exception> {
    val deferred = deferred<List<TreeCloneClass>, Exception>()
    task {
        callInEventQueue { ProgressManager.getInstance().run(FilterTask(this, deferred))  }
    }
    return deferred.promise
}