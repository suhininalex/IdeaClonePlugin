package com.suhininalex.clones.core.clonefilter

import com.suhininalex.clones.core.TreeCloneClass

class LengthFilter(val minLength: Int): CloneClassFilter {
    override fun isAllowed(treeCloneClass: TreeCloneClass?) =
        treeCloneClass?.length ?: 0 > minLength
}