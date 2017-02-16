package com.suhininalex.clones.core.clonefilter

import com.suhininalex.clones.core.TreeCloneClass
import com.suhininalex.clones.core.length

object CropTailFilter : CloneClassFilter {

    override fun isAllowed(treeCloneClass: TreeCloneClass?) =
        treeCloneClass?.treeNode?.parentEdge?.length ?: 0 > maxTail

    val maxTail = 9
}