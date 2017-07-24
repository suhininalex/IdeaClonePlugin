package com.suhininalex.clones.ide.configuration

import com.suhininalex.clones.core.utils.BooleanProperty
import com.suhininalex.clones.core.utils.IdeaSettings
import com.suhininalex.clones.core.utils.IntProperty

object PluginSettings: IdeaSettings(nameSpace = "clone_finder") {

    private object defaults {
        val minCloneLength = 40
        val coverageSkipFilter = 70
        val enabledForProject = true
        val disableTestFolder = true
        val kotlinSearchEnabled = true
        val javaSearchEnabled = true
        val maxMemory = 350
        val enableGaps = false
        val minFragment = 10
    }

    val minFragment = defaults.minFragment

    var enableGaps by BooleanProperty(defaults.enableGaps)

    var maxMemory by IntProperty(defaults.maxMemory)

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

