package com.suhininalex.clones.core.clonefilter

import com.suhininalex.clones.core.CloneClass
import com.suhininalex.clones.core.length

object CropTailFilter : CloneClassFilter {

    override fun isAllowed(cloneClass: CloneClass?) =
        cloneClass?.treeNode?.parentEdge?.length ?: 0 > maxTail

    val maxTail = 9
}