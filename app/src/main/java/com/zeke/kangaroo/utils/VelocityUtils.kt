package com.zeke.kangaroo.utils

import android.view.VelocityTracker
import android.view.ViewConfiguration

/**
 * author：KingZ
 * date：2019/3/24
 * description： 手势速度工具类
 */
object VelocityUtils {
    /**
     * 根据每秒滑动的像素值，获取X方向上的手势滑动速度
     * @param tracker [VelocityTracker] 速度追踪器对象
     * @return X速度值
     */
    fun getscrollerVelocity_X(tracker: VelocityTracker): Float {
        tracker.computeCurrentVelocity(1000)
        return tracker.xVelocity
    }

    /**
     * 根据每秒滑动的像素值，获取Y方向上的手势滑动速度
     * @param tracker [VelocityTracker] 速度追踪器对象
     * @return Y速度值
     */
    fun getscrollerVelocity_Y(tracker: VelocityTracker): Float {
        tracker.computeCurrentVelocity(1000)
        return tracker.yVelocity
    }

    /**
     * 回收速度追踪器对象
     * @param tracker [VelocityTracker] 速度追踪器
     */
    fun recycleVelocityTracker(tracker: VelocityTracker?) {
        if (tracker != null) {
            tracker.clear()
            tracker.recycle()
        }
    }

    /**
     * 根据[ViewConfiguration]获取Fling的最小速度值(以每秒像素值为单位)
     * @param vc ViewConfiguration对象
     * @return 最小速度值
     */
    fun getScaledMinFlingVelocity(vc: ViewConfiguration): Int {
        return vc.scaledMinimumFlingVelocity
    }

    /**
     * 根据[ViewConfiguration]获取Fling的最大速度值(以每秒像素值为单位)
     * @param vc ViewConfiguration对象
     * @return 最大速度值
     */
    fun getScaledMaxFlingVelocity(vc: ViewConfiguration): Int {
        return vc.scaledMaximumFlingVelocity
    }
}