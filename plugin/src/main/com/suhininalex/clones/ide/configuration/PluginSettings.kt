package com.suhininalex.clones.ide.configuration

object PluginSettings: IdeaSettings("clone_finder") {
    var minCloneLength = 20// by IntProperty(40)
    var coverageSkipFilter = 70
}

