package com.WqNzVmK.rJpLtF.core.ui

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.WqNzVmK.rJpLtF.R
import com.WqNzVmK.rJpLtF.core.assets.loadAsset
import com.WqNzVmK.rJpLtF.databinding.ViewStepCardBinding

/**
 * A numbered tutorial step: badge, title, body, an optional AI sprite and an
 * optional check / cross glyph drawn as a vector (never as an emoji).
 */
class StepCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewStepCardBinding.inflate(LayoutInflater.from(context), this)

    private var accentColor: Int = context.getColor(R.color.blue)

    init {
        val typed = context.obtainStyledAttributes(attrs, R.styleable.StepCardView, defStyleAttr, 0)
        try {
            accentColor = typed.getColor(
                R.styleable.StepCardView_stepAccent,
                context.getColor(R.color.blue),
            )
            typed.getString(R.styleable.StepCardView_stepNumber)?.let { binding.stepNumber.text = it }
            typed.getString(R.styleable.StepCardView_stepTitle)?.let { binding.stepTitle.text = it }
            typed.getString(R.styleable.StepCardView_stepBody)?.let { binding.stepBody.text = it }
        } finally {
            typed.recycle()
        }
        applyAccent()
    }

    /** Attaches an AI sprite plus its spoken description to the step. */
    fun showSprite(assetName: String, description: String) {
        binding.stepSprite.visibility = View.VISIBLE
        binding.stepSprite.loadAsset(assetName)
        binding.stepSprite.contentDescription = description
    }

    fun showGlyph(drawableRes: Int) {
        binding.stepGlyph.visibility = View.VISIBLE
        binding.stepGlyph.setImageResource(drawableRes)
    }

    private fun applyAccent() {
        binding.stepNumber.setTextColor(accentColor)
        binding.stepCard.setStrokeColor((accentColor and 0x00FFFFFF) or (STROKE_ALPHA shl 24))
        binding.stepNumber.background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor((accentColor and 0x00FFFFFF) or (FILL_ALPHA shl 24))
            setStroke(STROKE_WIDTH_PX, accentColor)
        }
    }

    private companion object {
        const val STROKE_ALPHA = 0x66
        const val FILL_ALPHA = 0x26
        const val STROKE_WIDTH_PX = 3
    }
}
