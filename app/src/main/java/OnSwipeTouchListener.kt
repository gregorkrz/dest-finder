package si.gregor.destfinder

import android.content.Context
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.View.OnTouchListener


import android.view.*
import android.view.GestureDetector



class OnSwipeTouchListener(ctx: Context, functions: MainActivity.SwipingFuncs) : OnTouchListener {
    private var gestureDetector: GestureDetector? = null
    private var swipingFuncs: MainActivity.SwipingFuncs? = null
    init {
        gestureDetector = GestureDetector(ctx, GestureListener())
        swipingFuncs = functions
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return gestureDetector!!.onTouchEvent(event)
    }

    private inner class GestureListener : SimpleOnGestureListener() {

        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            var result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            swipingFuncs!!.onSwipeRight()
                        } else {
                            swipingFuncs!!.onSwipeLeft()
                        }
                        result = true
                    }
                } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        swipingFuncs!!.onSwipeBottom()
                    } else {
                        swipingFuncs!!.onSwipeTop()
                    }
                    result = true
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            return result
        }

    }








}