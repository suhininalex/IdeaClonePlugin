package com.suhininalex.clones.ide.configuration

import java.util.*

object PluginLabels {

    private val resourceBundle = ResourceBundle.getBundle("language")!!

    fun getLabel(key: String): String =
        resourceBundle.getString(key)
}