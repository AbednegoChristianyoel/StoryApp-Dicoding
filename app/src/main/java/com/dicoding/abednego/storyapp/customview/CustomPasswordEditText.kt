package com.dicoding.abednego.storyapp.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.dicoding.abednego.storyapp.R

class CustomPasswordEditText : AppCompatEditText {

    constructor(context: Context) : super(context) { init() }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init() }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { init() }

    private var charLength = 0

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                charLength = s.length
                error =
                    if (charLength < 8) context.getString(R.string.validation_password) else null
            }
            override fun afterTextChanged(edt: Editable?) {
            }
        })
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        maxLines = 1
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}