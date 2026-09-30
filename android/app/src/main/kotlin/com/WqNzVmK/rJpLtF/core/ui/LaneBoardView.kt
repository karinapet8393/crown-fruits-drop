package com.WqNzVmK.rJpLtF.core.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.WqNzVmK.rJpLtF.R
import com.WqNzVmK.rJpLtF.core.assets.AppAssets
import com.WqNzVmK.rJpLtF.core.assets.AssetImages
import com.WqNzVmK.rJpLtF.core.assets.FruitAssetMap
import com.WqNzVmK.rJpLtF.databinding.ViewLaneBoardBinding
import com.WqNzVmK.rJpLtF.domain.model.FallingItem

/**
 * The palace kitchen board: three chutes, the fruits sliding down them and the
 * golden basket at the bottom rim.
 *
 * Geometry is frame-safe by construction. The three lanes are weighted children of
 * a row that is laid out INSIDE this view's padding, so
 * `frame = board_pad + board_border` is subtracted before the lane width is shared
 * out and nothing can ever spill past the brass border.
 */
class LaneBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewLaneBoardBinding.inflate(LayoutInflater.from(context), this)

    private val lanes: List<View> = listOf(
        binding.laneLeft,
        binding.laneCenter,
        binding.laneRight,
    )

    private val spritePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { isFilterBitmap = true }
    private val flashPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val destination = RectF()

    private val fruitSizePx = resources.getDimensionPixelSize(R.dimen.sprite_fruit).toFloat()
    private val basketSizePx = resources.getDimensionPixelSize(R.dimen.sprite_basket).toFloat()
    private val laneRadiusPx = resources.displayMetrics.density * LANE_CORNER_DP

    private var items: List<FallingItem> = emptyList()
    private var basketLane: Int = 1
    private var basketCenterX: Float = -1f
    private var basketScale: Float = 1f
    private var flashLane: Int = NO_LANE
    private var flashStrength: Float = 0f
    private var highlightedLane: Int = NO_LANE

    private var laneTapListener: ((Int) -> Unit)? = null

    init {
        val frame = resources.getDimensionPixelSize(R.dimen.board_pad) +
            resources.getDimensionPixelSize(R.dimen.board_border)
        setPadding(frame, frame, frame, frame)
        setWillNotDraw(false)
        isClickable = true
    }

    fun setOnLaneTapListener(listener: (Int) -> Unit) {
        laneTapListener = listener
    }

    /** One rendered frame of the round. */
    fun render(
        items: List<FallingItem>,
        basketLane: Int,
        flashLane: Int,
        flashStrength: Float,
        mistakeStrength: Float,
        basketPulse: Float,
    ) {
        this.items = items
        this.basketLane = basketLane
        this.flashLane = flashLane
        this.flashStrength = flashStrength
        this.basketScale = basketPulse
        binding.boardFlash.alpha = mistakeStrength.coerceIn(0f, 1f) * MISTAKE_FLASH_MAX_ALPHA
        // Only when the basket actually changed chute. Swapping the three lane
        // backgrounds on every frame re-resolved three drawables and kept the
        // window in a permanent layout pass, so uiautomator never saw it settle.
        if (basketLane != highlightedLane) {
            highlightedLane = basketLane
            lanes.forEachIndexed { index, lane ->
                lane.setBackgroundResource(
                    if (index == basketLane) R.drawable.deco_lane_frame_active
                    else R.drawable.deco_lane_frame,
                )
            }
        }
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_UP) {
            val lane = laneAt(event.x)
            if (lane != NO_LANE) {
                performClick()
                laneTapListener?.invoke(lane)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean = super.performClick()

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (lanes.first().width <= 0) return

        drawLaneFlash(canvas)
        drawItems(canvas)
        drawBasket(canvas)
    }

    private fun drawLaneFlash(canvas: Canvas) {
        val lane = flashLane
        if (lane == NO_LANE || flashStrength <= 0f) return
        val alpha = (flashStrength.coerceIn(0f, 1f) * LANE_FLASH_MAX_ALPHA).toInt()
        flashPaint.color = context.getColor(R.color.green)
        flashPaint.alpha = alpha
        val target = lanes.getOrNull(lane) ?: return
        val left = binding.laneRow.left + target.left.toFloat()
        destination.set(left, contentTop(), left + target.width, contentBottom())
        canvas.drawRoundRect(destination, laneRadiusPx, laneRadiusPx, flashPaint)
    }

    private fun drawItems(canvas: Canvas) {
        val top = contentTop()
        val span = contentBottom() - top
        items.forEach { item ->
            val bitmap = AssetImages.bitmap(context, FruitAssetMap.assetFor(item.kind)) ?: return@forEach
            val centerX = laneCenterX(item.lane)
            val centerY = top + span * item.progress.coerceIn(0f, 1.05f)
            val half = fruitSizePx / 2f
            destination.set(centerX - half, centerY - half, centerX + half, centerY + half)
            canvas.drawBitmap(bitmap, null, destination, spritePaint)
        }
    }

    private fun drawBasket(canvas: Canvas) {
        val bitmap = AssetImages.bitmap(context, AppAssets.SPRITE_BASKET_GOLD) ?: return
        val targetX = laneCenterX(basketLane)
        basketCenterX = if (basketCenterX < 0f) targetX else basketCenterX + (targetX - basketCenterX) * BASKET_LERP
        val half = basketSizePx * basketScale / 2f
        val centerY = contentBottom() - basketSizePx / 2f
        destination.set(basketCenterX - half, centerY - half, basketCenterX + half, centerY + half)
        canvas.drawBitmap(bitmap, null, destination, spritePaint)
        if (kotlin.math.abs(targetX - basketCenterX) > 0.5f) {
            postInvalidateOnAnimation()
        }
    }

    private fun laneCenterX(lane: Int): Float {
        val target = lanes.getOrNull(lane) ?: lanes[lanes.size / 2]
        return binding.laneRow.left + target.left + target.width / 2f
    }

    private fun laneAt(x: Float): Int {
        lanes.forEachIndexed { index, lane ->
            val left = binding.laneRow.left + lane.left
            if (x >= left && x <= left + lane.width) return index
        }
        return NO_LANE
    }

    private fun contentTop(): Float = paddingTop.toFloat()

    private fun contentBottom(): Float = (height - paddingBottom).toFloat()

    private companion object {
        const val NO_LANE = -1
        const val LANE_CORNER_DP = 12f
        const val LANE_FLASH_MAX_ALPHA = 140f
        const val MISTAKE_FLASH_MAX_ALPHA = 0.4f
        const val BASKET_LERP = 0.3f
    }
}
