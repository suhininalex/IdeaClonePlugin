package com.suhininalex.clones.ide;

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.GeneratedSourcesFilter
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.EverythingGlobalScope
import com.intellij.util.indexing.*
import com.intellij.util.io.EnumeratorIntegerDescriptor
import com.intellij.util.io.KeyDescriptor
import com.suhininalex.clones.core.CloneIndexer
import com.suhininalex.clones.core.languagescope.languageSerializer
import com.suhininalex.clones.core.utils.*
import com.suhininalex.clones.ide.configuration.PluginLabels
import com.suhininalex.clones.ide.configuration.PluginSettings

class CloneFinderIndex : ScalarIndexExtension<Int>(){

    companion object {
        val NAME: ID<Int, Void> = ID.create("CloneFinderIndexer")

        fun enshureUpToDate(project: Project){
            FileBasedIndex.getInstance().ensureUpToDate(NAME, project, EverythingGlobalScope(project))
        }

        fun rebuild(project: Project){
            project.languageSerializer.update()
            CloneIndexer.clear()
            FileBasedIndex.getInstance().requestRebuild(NAME)
        }
    }

    override fun getVersion(): Int = 1706281

    override fun dependsOnFileContent(): Boolean = true

    override fun getKeyDescriptor(): KeyDescriptor<Int> = EnumeratorIntegerDescriptor.INSTANCE

    override fun getName(): ID<Int, Void> = NAME

    override fun getIndexer(): DataIndexer<Int, Void, FileContent> = CloneFinderIndexer()

    override fun getInputFilter(): FileBasedIndex.InputFilter = FileBasedIndex.InputFilter { virtualFile ->
         PluginSettings.enabledForProject && CurrentProject?.languageSerializer?.isFileTypeSupported(virtualFile.fileType) ?: false
    }

}

class CloneFinderIndexer : DataIndexer<Int, Void, FileContent> {

    /**
     * Approximately 100 bytes per token (see SmallSuffixTree docs)
     */
    val usedMemoryInMb: Int
        get() = CloneIndexer.indexedTokens.toInt() * 100 / 1024 / 1024

    fun checkMemory(project: Project){
        if (usedMemoryInMb > PluginSettings.maxMemory){
            PluginSettings.enabledForProject = false
            CloneFinderIndex.rebuild(project)
            Application.invokeLater{
                val title = PluginLabels.getMessage("warning-memory-issue-title")
                val message = PluginLabels.getMessage("warning-memory-issue-message")
                Notifications.Bus.notify(Notification("Actions", title, message, NotificationType.WARNING))
            }
        }
    }

    override fun map(fileContent: FileContent): Map<Int, Void> {
        val psiFile = PsiManager.getInstance(fileContent.project).findFile(fileContent.file)!!
        if (psiFile.isSourceFile() && !psiFile.isDisabledTest() && !psiFile.isGenerated()){
            Logger.log("[Indexer] Add file suffixes ${psiFile.virtualFile}")
            Logger.log("[Indexer] Tokens in suffix tree ${CloneIndexer.indexedTokens}")
            checkMemory(psiFile.project)
            CloneIndexer.removeFile(fileContent.file)
            CloneIndexer.addFile(psiFile)
        }
        return emptyMap()
    }

    fun PsiFile.isGenerated(): Boolean{
        return GeneratedSourcesFilter.isGeneratedSourceByAnyFilter(virtualFile, project)
    }

    fun PsiFile.isDisabledTest(): Boolean {
        return PluginSettings.disableTestFolder && isTestFile()
    }
}