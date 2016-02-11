package com.suhininalex.clones.clonefilter

import com.suhininalex.clones.CloneClass
import com.suhininalex.clones.length

object CropTailFilter : CloneClassFilter {

    override fun isAllowed(cloneClass: CloneClass?) =
        cloneClass?.treeNode?.parentEdge?.length ?: 0 > maxTail

    val maxTail = 9
}