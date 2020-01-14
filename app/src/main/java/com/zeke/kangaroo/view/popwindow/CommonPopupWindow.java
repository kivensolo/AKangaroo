package com.zeke.kangaroo.view.popwindow;

import android.content.Context;
import android.view.View;
import android.widget.PopupWindow;

import com.zeke.kangaroo.utils.UIUtils;

/**
 * author: King.Z <br>
 * date:  2016/8/23 19:52 <br>
 * description: 通用PopupWindow <br>
 *
 * 因PopupWindow的创建方法比较繁琐,但很多东西是可以复用的，<br>
 * 所以，通过建造者模式构建一个公用的popupWindow <br>
 *
 *   CommonPopupWindow popWindow = new CommonPopupWindow.Builder() <br>
 *      .setView(R.id.xxx)  <br>
 *      .setXXX() <br>
 *      .create(context);<br>
 *   popWindows.setBackgroundDrawable(Drawable);<br>
 *
 *   //从view下方弹起 <br>
 *   popWindows.showAsDropDown(View);<br>
 *   //向上弹出 <br>
 *   popupWindow.showAsDropDown(view, 0, -(popupWindow.getHeight() + view.getMeasuredHeight()));<br>
 *   //向左弹出 <br>
 *   popupWindow.showAsDropDown(view, -popupWindow.getWidth(), -view.getHeight());
 */
public class CommonPopupWindow extends PopupWindow {
    final PopupController controller;

    public interface ViewInterface {
        void onChildViewCreate(View popView, int layoutResId);
    }

    private CommonPopupWindow(Context context) {
        controller = new PopupController(context, this);
    }

    @Override
    public int getWidth() {
        return controller.mPopupView.getMeasuredWidth();
    }

    @Override
    public int getHeight() {
        return controller.mPopupView.getMeasuredHeight();
    }
    @Override
    public void dismiss() {
        super.dismiss();
        //if(controller.){
            controller.setBackGroundLevel(1.0f);
        //}
    }

    public static class Builder {
        private final PopupController.PopupParams params;
        private ViewInterface listener;

        public Builder(Context context) {
            params = new PopupController.PopupParams(context);
        }

        /**
         * @param layoutResId 设置PopupWindow 布局ID
         * @return Builder
         */
        public Builder setView(int layoutResId) {
            params.mView = null;
            params.layoutResId = layoutResId;
            return this;
        }

         /**
         * @param view 设置PopupWindow布局
         * @return Builder
         */
        public Builder setView(View view) {
            params.mView = view;
            params.layoutResId = 0;
            return this;
        }

        /**
         * 设置宽度和高度 如果不设置 默认是wrap_content
         * @param width view宽度
         * @param height view高度
         * @return Builder
         */
        public Builder setSize(int width, int height) {
            params.mWidth = width;
            params.mHeight = height;
            return this;
        }
        /**
         * 设置背景灰色程度
         *
         * @param level 0.0f-1.0f
         * @return Builder
         */
        public Builder setBackGroundLevel(float level) {
            params.isShowBg = true;
            params.bg_level = level;
            return this;
        }
        /**
         * 是否可点击Outside消失
         *
         * @param touchable 是否可点击
         * @return Builder
         */
        public Builder setOutsideTouchable(boolean touchable) {
            params.isTouchable = touchable;
            return this;
        }

        public Builder setFocusable(boolean focusable) {
            params.isFocusable = focusable;
            return this;
        }

        public Builder setClippingEnabled(boolean enabled) {
            params.mClippingEnabled = enabled;
            return this;
        }

        public Builder setAnimationStyle(int animationStyle) {
            params.isShowAnim = true;
            params.animationStyle = animationStyle;
            return this;
        }

        public CommonPopupWindow create() {
            final CommonPopupWindow popupWindow = new CommonPopupWindow(params.ctx);
            params.apply(popupWindow.controller);
            if (listener != null && params.layoutResId != 0) {
                listener.onChildViewCreate(popupWindow.controller.mPopupView, params.layoutResId);
            }
            UIUtils.measureWidthAndHeight(popupWindow.controller.mPopupView);
            return popupWindow;
        }

        public Builder setListener(ViewInterface listener) {
            this.listener = listener;
            return this;
        }
    }
}
