package com.suhininalex.clones.ide.configuration

object PluginSettings: IdeaSettings("clone_finder") {
    var minCloneLength by IntProperty(50)
}

