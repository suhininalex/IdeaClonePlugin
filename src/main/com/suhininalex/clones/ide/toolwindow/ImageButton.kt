package com.suhininalex.clones.ide.toolwindow

import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.Icon
import javax.swing.JLabel

class ImageButton(val imageNormal: Icon, imageHover: Icon = imageNormal, val imageSelect: Icon = imageNormal): JLabel(imageNormal) {

    var selected = false
        set(value) {
            field = value
            icon = if (selected) imageSelect else imageNormal
        }


    private val mouseListener = object: MouseAdapter() {
        override fun mouseEntered(e: MouseEvent) {
            if (! selected) this@ImageButton.icon = imageHover
        }

        override fun mouseExited(e: MouseEvent) {
            if (! selected) this@ImageButton.icon = imageNormal
        }
    }

    init {
        addMouseListener(mouseListener)
    }

}
