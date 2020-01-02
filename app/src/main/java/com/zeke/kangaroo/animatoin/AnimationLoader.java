package com.zeke.kangaroo.animatoin;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.zeke.kangaroo.R;

/**
 * author：KingZ
 * date：2020/1/2
 * description：动画创建器
 */
public class AnimationLoader {
    private AnimationLoader() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * Load a shake animation.
     * @param context Context
     * @return A shake animation.
     */
    public static Animation loadShake(Context context){
        return AnimationUtils.loadAnimation(context,R.anim.shake);
    }
}
