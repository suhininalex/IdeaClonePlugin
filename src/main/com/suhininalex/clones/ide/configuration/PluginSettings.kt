package com.suhininalex.clones.ide.configuration

import com.suhininalex.clones.core.utils.BooleanProperty
import com.suhininalex.clones.core.utils.IdeaSettings
import com.suhininalex.clones.core.utils.IntProperty

object PluginSettings: IdeaSettings("clone_finder") {

    private object defaults {
        val minCloneLength = 20
        val coverageSkipFilter = 70
        val enabledForProject = true
    }

    var minCloneLength by IntProperty(defaults.minCloneLength)

    var coverageSkipFilter by IntProperty(defaults.coverageSkipFilter)

    var enabledForProject by BooleanProperty(defaults.enabledForProject, true)

    fun reset(){
        minCloneLength = defaults.minCloneLength
        coverageSkipFilter = defaults.coverageSkipFilter
        enabledForProject = defaults.enabledForProject
    }
}

