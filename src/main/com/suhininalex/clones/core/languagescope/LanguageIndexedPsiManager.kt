package com.suhininalex.clones.core.languagescope

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.suhininalex.clones.core.languagescope.java.JavaIndexedPsiDefiner
import com.suhininalex.clones.core.languagescope.kotlin.KtIndexedPsiDefiner
import com.suhininalex.clones.ide.configuration.PluginSettings

class LanguageIndexedPsiService {

    private val indexedPsiDefiners = HashMap<String, IndexedPsiDefiner>()

    fun clear() {
        indexedPsiDefiners.clear()
    }

    fun getIndexedPsiDefiner(psiElement: PsiElement): IndexedPsiDefiner? =
        indexedPsiDefiners[psiElement.containingFile?.fileType?.name]

    fun registerNewLanguage(indexedPsiDefiner: IndexedPsiDefiner){
        indexedPsiDefiners.put(indexedPsiDefiner.fileType, indexedPsiDefiner)
    }

    fun unregisterLanguage(fileType: String){
        indexedPsiDefiners.remove(fileType)
    }

    fun isFileTypeSupported(fileType: FileType): Boolean {
        return  fileType.name in indexedPsiDefiners.values.map { it.fileType }.toSet()
    }

    fun update(){
        clear()
        if (PluginSettings.javaSearchEnabled) registerNewLanguage(JavaIndexedPsiDefiner())
        if (PluginSettings.kotlinSearchEnabled) registerNewLanguage(KtIndexedPsiDefiner())
    }
}

val Project.languageSerializer: LanguageIndexedPsiService
    get() = ServiceManager.getService(this, LanguageIndexedPsiService::class.java)!!