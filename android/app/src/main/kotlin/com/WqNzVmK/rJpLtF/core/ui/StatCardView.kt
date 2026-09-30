package com.WqNzVmK.rJpLtF.core.ui

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import com.WqNzVmK.rJpLtF.R
import com.WqNzVmK.rJpLtF.databinding.ViewStatCardBinding

/**
 * One enamel-and-brass statistic tile: accent dot, big number, small caps label.
 * The very same component is used on the menu sheet and on the result screen, so
 * the two screens can never drift apart visually.
 */
class StatCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewStatCardBinding.inflate(LayoutInflater.from(context), this)

    private var accentColor: Int = context.getColor(R.color.gold)

    init {
        val typed = context.obtainStyledAttributes(attrs, R.styleable.StatCardView, defStyleAttr, 0)
        try {
            accentColor = typed.getColor(
                R.styleable.StatCardView_statAccent,
                context.getColor(R.color.gold),
            )
            typed.getString(R.styleable.StatCardView_statLabel)?.let { binding.statLabel.text = it }
            typed.getString(R.styleable.StatCardView_statValue)?.let { binding.statValue.text = it }
        } finally {
            typed.recycle()
        }
        applyAccent()
    }

    /** Sets the big number and keeps the accessibility state in sync. */
    fun bind(value: String, label: String? = null) {
        binding.statValue.text = value
        if (label != null) {
            binding.statLabel.text = label
        }
        ViewCompat.setStateDescription(binding.statCard, "${binding.statLabel.text} $value")
        contentDescription = "${binding.statLabel.text} $value"
    }

    fun setAccent(color: Int) {
        accentColor = color
        applyAccent()
    }

    private fun applyAccent() {
        binding.statValue.setTextColor(accentColor)
        binding.statCard.setStrokeColor(withAlpha(accentColor, STROKE_ALPHA))
        val dot = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(accentColor)
        }
        binding.statAccentDot.background = dot
    }

    private fun withAlpha(color: Int, alpha: Int): Int =
        (color and 0x00FFFFFF) or (alpha shl 24)

    private companion object {
        const val STROKE_ALPHA = 0x55
    }
}
