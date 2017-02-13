package com.suhininalex.clones.ide.configuration

import com.intellij.ide.util.PropertiesComponent
import kotlin.reflect.KProperty

/**
 * Properties have to be changed only from these delegators
 */
open class IdeaSettings(val nameSpace: String)

class StringProperty(val defaultValue: String) {
    operator fun getValue(thisRef: IdeaSettings, property: KProperty<*>): String {
        return PropertiesComponent.getInstance().getValue("${thisRef.nameSpace}.${property.name}", defaultValue)
    }

    operator fun setValue(thisRef: IdeaSettings, property: KProperty<*>, value: String) {
        PropertiesComponent.getInstance().setValue("${thisRef.nameSpace}.${property.name}", value)
    }
}

class BooleanProperty(val defaultValue: Boolean) {
    operator fun getValue(thisRef: IdeaSettings, property: KProperty<*>): Boolean {
        return PropertiesComponent.getInstance().getBoolean("${thisRef.nameSpace}.${property.name}", defaultValue)
    }

    operator fun setValue(thisRef: IdeaSettings, property: KProperty<*>, value: Boolean) {
        PropertiesComponent.getInstance().setValue("${thisRef.nameSpace}.${property.name}", value)
    }
}

class IntProperty(val defaultValue: Int) {
    operator fun getValue(thisRef: IdeaSettings, property: KProperty<*>): Int {
        return PropertiesComponent.getInstance().getInt("${thisRef.nameSpace}.${property.name}", defaultValue)
    }

    operator fun setValue(thisRef: IdeaSettings, property: KProperty<*>, value: Int) {
        PropertiesComponent.getInstance().setValue("${thisRef.nameSpace}.${property.name}", value.toString())
    }
}
