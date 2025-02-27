package com.example.a2048game

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent

open class SwipeGestureListener(context: Context) : GestureDetector.SimpleOnGestureListener() {
    private val SWIPE_THRESHOLD = 100
    private val SWIPE_VELOCITY_THRESHOLD = 100

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (e1 == null || e2 == null) return false

        val diffX = e2.x - e1.x
        val diffY = e2.y - e1.y

        // Если горизонтальное перемещение больше вертикального
        return if (kotlin.math.abs(diffX) > kotlin.math.abs(diffY)) {
            if (kotlin.math.abs(diffX) > SWIPE_THRESHOLD && kotlin.math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    onSwipeRight()
                } else {
                    onSwipeLeft()
                }
                true
            } else {
                false
            }
        } else {
            if (kotlin.math.abs(diffY) > SWIPE_THRESHOLD && kotlin.math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    onSwipeDown()
                } else {
                    onSwipeUp()
                }
                true
            } else {
                false
            }
        }
    }

    open fun onSwipeRight() {}
    open fun onSwipeLeft() {}
    open fun onSwipeUp() {}
    open fun onSwipeDown() {}
}