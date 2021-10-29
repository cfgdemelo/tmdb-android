package com.arctouch.codechallenge.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup

fun Context.fullscreenDialog(layout: Int): Dialog {
    return Dialog(this).apply {
        this.setContentView(layout)
        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        this.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        )

        this.show()
    }
}

