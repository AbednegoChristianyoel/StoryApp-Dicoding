package com.dicoding.abednego.storyapp.customview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doOnTextChanged
import com.dicoding.abednego.storyapp.R

class CustomEmailEditText : AppCompatEditText {

    constructor(context: Context) : super(context) { init() }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init() }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { init() }

    private fun init() {
        doOnTextChanged { s, _, _, _ ->
            if (s != null) {
                error = if (s.isNotEmpty()) {
                    if (!s.toString().matches(Regex("[a-zA-Z\\d._-]+@[a-z]+\\.+[a-z]+"))) {
                        context.getString(R.string.invalid_email)
                    } else null
                } else {
                    context.getString(R.string.empty_email)
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        isSingleLine = true
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}