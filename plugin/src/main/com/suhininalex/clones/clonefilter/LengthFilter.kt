package com.suhininalex.clones.clonefilter

import com.suhininalex.clones.CloneClass

class LengthFilter(val minLength: Int): CloneClassFilter{
    override fun isAllowed(cloneClass: CloneClass?) =
        cloneClass?.length ?: 0 > minLength
}