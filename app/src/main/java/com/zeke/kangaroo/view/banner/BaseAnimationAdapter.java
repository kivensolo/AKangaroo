package com.zeke.kangaroo.view.banner;

import android.view.View;
import android.widget.LinearLayout;

/**
 * author：KingZ
 * date：2020/2/21
 * description：基础的Animation适配器.
 * 自定义动画可继承扩展
 */
public abstract class BaseAnimationAdapter {

    private NBannerView mBannerView;

    protected final NBannerView getCircularView() {
        return mBannerView;
    }

    public void bindWithBannerView(NBannerView view){
        mBannerView = view;
    }

    protected final View getView(int indexOffset) {
        if (null == mBannerView) {
            return null;
        }
        final int centerIndex = mBannerView.getRecycleItemSize() / 2;
        NBannerView.ItemWrapper targetItem = mBannerView.findItem(cycleIndex(centerIndex + indexOffset));
        return (null != targetItem) ? targetItem.getView() : null;
    }

    protected final int getOffset(int indexOffset) {
        if (null == mBannerView) {
            return 0;
        }
        final View centerView = getView(0);
        final View targetView = getView(indexOffset);
        int offset = 0;
        if (null != centerView && null != targetView) {
            if (LinearLayout.VERTICAL == mBannerView.getOrientation()) {
                offset = targetView.getTop() - centerView.getTop() - mBannerView.getScrollY();
            } else { // HORIZONTAL
                offset = targetView.getLeft() - centerView.getLeft() - mBannerView.getScrollX();
            }
        }
        return offset;
    }

    protected final float getOffsetPercent(int indexOffset) {
        int maxOffset;
        if (LinearLayout.VERTICAL == mBannerView.getOrientation()) {
            maxOffset = getItemHeight();
        } else { // HORIZONTAL
            maxOffset = getItemWidth();
        }
        maxOffset += mBannerView.spaceBetweenItems;
        int targetOffset = getOffset(indexOffset);
        return Math.abs(targetOffset) * 1.0f / maxOffset;
    }

    protected final int getItemWidth() {
        if (null == mBannerView) {
            return 0;
        }
        return mBannerView.getItemWidth();
    }

    protected final int getItemHeight() {
        if (null == mBannerView) {
            return 0;
        }
        return mBannerView.getItemHeight();
    }

    protected final int cycleIndex(int indexOffset) {
        if (null == mBannerView) {
            return indexOffset;
        }
        return mBannerView.cycleItemIndex(indexOffset);
    }

    protected final int getChildCounts() {
        return mBannerView.getRecycleItemSize();
    }

    protected abstract void onLayout(boolean changed);

    protected abstract void onScrolled(int offset);

}

