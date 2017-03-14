package com.suhininalex.clones.core.utils

import com.intellij.ide.util.PropertiesComponent
import kotlin.reflect.KProperty

/**
 * Properties have to be changed only from these delegators
 */
open class IdeaSettings(val nameSpace: String)

class StringProperty(val defaultValue: String, val projectScope: Boolean = false) {
    operator fun getValue(thisRef: IdeaSettings, property: KProperty<*>): String {
        return propertiesComponent(projectScope).getValue("${thisRef.nameSpace}.${property.name}", defaultValue)
    }

    operator fun setValue(thisRef: IdeaSettings, property: KProperty<*>, value: String) {
        propertiesComponent(projectScope).setValue("${thisRef.nameSpace}.${property.name}", value)
    }
}

class BooleanProperty(val defaultValue: Boolean, val projectScope: Boolean = false) {
    operator fun getValue(thisRef: IdeaSettings, property: KProperty<*>): Boolean {
        return propertiesComponent(projectScope).getBoolean("${thisRef.nameSpace}.${property.name}", defaultValue)
    }

    operator fun setValue(thisRef: IdeaSettings, property: KProperty<*>, value: Boolean) {
        propertiesComponent(projectScope).setValue("${thisRef.nameSpace}.${property.name}", value.toString())
    }
}

class IntProperty(val defaultValue: Int, val projectScope: Boolean = false) {
    operator fun getValue(thisRef: IdeaSettings, property: KProperty<*>): Int {
        return propertiesComponent(projectScope).getInt("${thisRef.nameSpace}.${property.name}", defaultValue)
    }

    operator fun setValue(thisRef: IdeaSettings, property: KProperty<*>, value: Int) {
        propertiesComponent(projectScope).setValue("${thisRef.nameSpace}.${property.name}", value.toString())
    }
}

private fun propertiesComponent(projectScope: Boolean): PropertiesComponent =
    if (projectScope && CurrentProject != null) PropertiesComponent.getInstance(CurrentProject)
    else PropertiesComponent.getInstance()
