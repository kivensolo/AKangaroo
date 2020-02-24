package com.zeke.kangaroo.view.banner;

/**
 * author：KingZ
 * date：2020/2/21
 * description：Banner内部item的状态抽象接口
 */
public interface ItemChangedListener {
    void onItemScrolled(NBannerView v, int dataIndex, float offset);
    void onItemSelected(NBannerView v, int dataIndex);
}
