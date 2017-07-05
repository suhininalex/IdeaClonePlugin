package com.suhininalex.clones.ide

import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.vfs.newvfs.events.VFileMoveEvent
import com.suhininalex.clones.core.CloneIndexer

class FileListener(): BulkFileListener.Adapter() {

    override fun before(events: List<VFileEvent>) {
        events
            .filter { it is VFileDeleteEvent || it is VFileMoveEvent }
            .mapNotNull { it.file }
            .forEach {
                CloneIndexer.removeFile(it)
            }
    }

}

