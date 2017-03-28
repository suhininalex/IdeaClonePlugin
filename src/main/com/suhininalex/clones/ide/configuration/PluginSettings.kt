package com.suhininalex.clones.ide.configuration

import com.suhininalex.clones.core.utils.BooleanProperty
import com.suhininalex.clones.core.utils.IdeaSettings
import com.suhininalex.clones.core.utils.IntProperty

object PluginSettings: IdeaSettings(nameSpace = "clone_finder") {

    private object defaults {
        val minCloneLength = 20
        val coverageSkipFilter = 70
        val enabledForProject = true
        val disableTestFolder = false
        val kotlinSearchEnabled = true
        val javaSearchEnabled = true
    }

    var minCloneLength by IntProperty(defaults.minCloneLength)

    var coverageSkipFilter by IntProperty(defaults.coverageSkipFilter)

    var enabledForProject by BooleanProperty(defaults.enabledForProject, projectScope = true)

    var disableTestFolder by BooleanProperty(defaults.disableTestFolder)

    var kotlinSearchEnabled by BooleanProperty(defaults.kotlinSearchEnabled)

    var javaSearchEnabled by BooleanProperty(defaults.javaSearchEnabled)

    fun reset(){
        minCloneLength = defaults.minCloneLength
        coverageSkipFilter = defaults.coverageSkipFilter
        enabledForProject = defaults.enabledForProject
        disableTestFolder = defaults.disableTestFolder
    }
}

