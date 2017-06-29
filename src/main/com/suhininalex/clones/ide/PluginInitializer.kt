package com.suhininalex.clones.ide

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.util.indexing.FileBasedIndex
import com.suhininalex.clones.core.languagescope.LanguageIndexedPsiManager
import com.suhininalex.clones.core.languagescope.java.JavaIndexedPsiDefiner
import com.suhininalex.clones.core.languagescope.kotlin.KtIndexedPsiDefiner
import com.suhininalex.clones.ide.configuration.PluginSettings

class PluginInitializer : StartupActivity {

    override fun runActivity(project: Project) {
        CloneFinderIndex.invalidate()
    }
}