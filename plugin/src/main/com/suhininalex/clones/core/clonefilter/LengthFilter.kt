package com.suhininalex.clones.core.clonefilter

import com.suhininalex.clones.core.CloneClass

class LengthFilter(val minLength: Int): CloneClassFilter {
    override fun isAllowed(cloneClass: CloneClass?) =
        cloneClass?.length ?: 0 > minLength
}