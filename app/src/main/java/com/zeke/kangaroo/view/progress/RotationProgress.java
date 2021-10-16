package com.zeke.kangaroo.view.progress;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.zeke.kangaroo.R;
import com.zeke.kangaroo.utils.UIUtils;

/**
 * Created by KingZ on 2016/1/5.
 * Discription: 旋转Loading加载圈
 */
@Deprecated
public class RotationProgress extends ProgressDialog{

    private Context mContext;
//    private LightTextView tv_message;
	private ImageView iv_progress;
	private RotateAnimation rotateAnimation;

    public RotationProgress(Context context) {
        super(context);
        mContext = context;
    }

    public RotationProgress(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.progress_dialog);
        android.view.WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = UIUtils.dip2px(200);
        params.height = UIUtils.dip2px(200);
        getWindow().setAttributes(params);

        initViews(savedInstanceState);
    }

    private void initViews(Bundle savedInstanceState) {

        //旋转动画设置
//        rotateAnimation = new RotateAnimation(0, 360 - (700 / 360), Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setInterpolator(new LinearInterpolator()); //设置线性变速
		rotateAnimation.setDuration(1000);
		rotateAnimation.setRepeatCount(-1); //-1：反复循环  0：只运动一次

        //ImageView设置
        iv_progress = (ImageView) findViewById(R.id.iv_progress);
        iv_progress.getLayoutParams().width = UIUtils.dip2px(100);
        iv_progress.getLayoutParams().height = UIUtils.dip2px(100);
            //设置尺寸Type(是一个枚举类型的)
        iv_progress.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        iv_progress.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.loading_new));
        iv_progress.startAnimation(rotateAnimation);

    }

    public void start() {
		iv_progress.startAnimation(rotateAnimation);
	}

    @Override
    public void show() {
        if (!isShowing()) {
			super.show();
		}
		start();
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
			super.dismiss();
		}
		iv_progress.clearAnimation();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
