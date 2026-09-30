package com.WqNzVmK.rJpLtF.core.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.ViewCompat
import com.WqNzVmK.rJpLtF.R
import com.WqNzVmK.rJpLtF.core.config.GameConfig
import com.WqNzVmK.rJpLtF.databinding.ViewMistakeDotsBinding

/** Three lamps that light up red as the head cook loses patience. */
class MistakeDotsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewMistakeDotsBinding.inflate(LayoutInflater.from(context), this)

    private val dots: List<ImageView> = listOf(
        binding.dotOne,
        binding.dotTwo,
        binding.dotThree,
    )

    init {
        render(0)
    }

    fun render(mistakes: Int) {
        dots.forEachIndexed { index, dot ->
            val lit = index < mistakes
            dot.setImageResource(if (lit) R.drawable.ic_dot_full else R.drawable.ic_dot_empty)
            dot.alpha = if (lit) 1f else 0.6f
        }
        val description = context.getString(R.string.desc_mistakes, mistakes, GameConfig.MAX_MISTAKES)
        contentDescription = description
        ViewCompat.setStateDescription(this, description)
    }
}
