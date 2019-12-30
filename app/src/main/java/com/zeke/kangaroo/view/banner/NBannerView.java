package com.zeke.kangaroo.view.banner;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * 增强型Banner.<br>
 * 首页轮播展示的需求，通常使用ViewPager就能满足需求。<br>
 * 但经常随着需求变动，样式或者动画的修改，这时ViewPager往往改起来有点复杂了，<br>
 * 并且要循环滑动时是最麻烦的, 此组件为专门为Banner设计的。
 * <p>特性:<br>
 * 循环滑动:
 *      手势/定时自动循环滑动啦，使用BaseAdapter实现内部视图复用，减
 *      少内存消耗滑动卡顿等问题。<br>
 * 可配置属性：自定义滑动动画，手势快速滑动，滑动方向垂直或水平。
 *
 * Item缩进: <br>
 * 缩进中心视图，并且展示左右视图，类似与PC网易云音乐首页Banner的样式。
 */
public class NBannerView extends ViewGroup {

    private static final int MOVE_SLOP = 10;

    private static final int MIN_RECYCLE_ITEM_SIZE = 3;

    private boolean isAutoCycle = false;

    private boolean isAutoCycleToNext = true;

    private int intervalOnAutoCycle = 4000;

    private int durationOnAutoScroll = 300;

    private int durationOnInertial = 800;

    private int durationOnPacking = 300;

    private float overRatio = 0.5f;

    private int orientation = LinearLayout.HORIZONTAL;

    private int spaceBetweenItems = 0;

    private int leftIndent, topIndent, rightIndent, bottomIndent;

    private boolean isClick2Selected = true;

    private boolean isAutoCheckRecycleItemSize = true;

    private float inertialRatio = 0.5f;

    private OnItemScrolledListener onItemScrolledListener;
    private OnItemSelectedListener onItemSelectedListener;
    private AnimationAdapter animationAdapter;
    private IIndicator indicator;

    private AdapterDataSetObserver dataSetObserver;
    private BaseAdapter adapter;
    private ArrayList<ItemWrapper> items = null;

    private Rect[] itemsBounds = null;

    private VelocityTracker velocityTracker = null;

    private final Runnable autoCycleRunnable = new Runnable() {
        @Override
        public void run() {
            if (isShown() && adapter != null && adapter.getCount() > 0) {
                moveItems(isAutoCycleToNext ? 1 : -1);
            }
            postDelayed(this, intervalOnAutoCycle);
        }
    };

    public NBannerView(Context context) {
        super(context);
        init();
    }

    public NBannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NBannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setRecycleItemSize(MIN_RECYCLE_ITEM_SIZE);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        velocityTracker = VelocityTracker.obtain();
        isAttachedToWindow = true;
        resumeAutoCycle();
    }

    private boolean isAttachedToWindow = false;

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        velocityTracker.clear();
        velocityTracker.recycle();
        isAttachedToWindow = false;
        interceptAutoCycle();
    }

    public final int getCurrentIndex() {
        ItemWrapper centerItem = findItem(getRecycleItemSize() / 2);
        return null != centerItem ? centerItem.dataIndex : -1;
    }

    public final NBannerView setCurrentIndex(int dataIndex) {
        updateAllItemView(dataIndex);
        notifyOnItemSelected();
        return this;
    }

    public final NBannerView setRecycleItemSize(int size) {
        if (size < MIN_RECYCLE_ITEM_SIZE) {
            return this;
        }
        if (size % 2 == 0) {
            size++;
        }
        if (getRecycleItemSize() == size) {
            return this;
        }
        final int tmpIndex = getCurrentIndex();
        if (null != items) {
            for (ItemWrapper item : items) {
                item.recycle();
            }
            items.clear();
        } else {
            items = new ArrayList<>(size);
        }
        for (int i = 0; i < size; i++) {
            items.add(new ItemWrapper(i));
        }
        setCurrentIndex((tmpIndex >= 0 && tmpIndex < adapter.getCount()) ? tmpIndex : 0);
        return this;
    }

    public final int getRecycleItemSize() {
        return null == items ? 0 : items.size();
    }

    public NBannerView setAdapter(BaseAdapter cycleAdapter) {
        if (adapter != null) {
            adapter.unregisterDataSetObserver(dataSetObserver);
        }
        if (cycleAdapter != null) {
            dataSetObserver = new AdapterDataSetObserver();
            cycleAdapter.registerDataSetObserver(dataSetObserver);
        }
        adapter = cycleAdapter;
        if (null != adapter) {
            adapter.notifyDataSetChanged();
        }
        return this;
    }

    public BaseAdapter getAdapter() {
        return adapter;
    }

    public NBannerView setOverRatio(float ratio) {
        this.overRatio = Math.max(0, Math.min(1.0f, ratio));
        return this;
    }

    public float getOverRatio() {
        return this.overRatio;
    }

    public NBannerView setOrientation(int orientation) {
        this.orientation = orientation == LinearLayout.VERTICAL ? orientation : LinearLayout.HORIZONTAL;
        registerCheckRecycleItemSize();
        requestLayout();
        return this;
    }

    public int getOrientation() {
        return orientation;
    }

    public NBannerView setDurationOnPacking(int duration) {
        this.durationOnPacking = Math.max(0, duration);
        return this;
    }

    public int getDurationOnPacking() {
        return durationOnPacking;
    }

    public NBannerView setDurationOnInertial(int duration) {
        durationOnInertial = Math.max(0, duration);
        return this;
    }

    public int getDurationOnInertial() {
        return durationOnInertial;
    }

    public NBannerView setDurationOnAutoScroll(int duration) {
        durationOnAutoScroll = Math.max(0, duration);
        return this;
    }

    public int getDurationOnAutoScroll() {
        return durationOnAutoScroll;
    }

    public NBannerView setIntervalOnAutoCycle(int interval) {
        intervalOnAutoCycle = Math.max(0, interval);
        return this;
    }

    public int getIntervalOnAutoCycle() {
        return intervalOnAutoCycle;
    }

    public boolean isAutoCycle() {
        return isAutoCycle;
    }

    public NBannerView setAutoCycle(boolean auto, boolean moveToNext) {
        isAutoCycle = auto;
        isAutoCycleToNext = moveToNext;
        if (auto) {
            if (isAttachedToWindow) {
                // auto start when already attached to window
                resumeAutoCycle();
            }
        } else {
            interceptAutoCycle();
        }
        return this;
    }

    public NBannerView setIndent(int left, int top, int right, int bottom) {
        leftIndent = left;
        topIndent = top;
        rightIndent = right;
        bottomIndent = bottom;
        registerCheckRecycleItemSize();
        requestLayout();
        return this;
    }

    public NBannerView setSpaceBetweenItems(int space) {
        spaceBetweenItems = space;
        registerCheckRecycleItemSize();
        requestLayout();
        return this;
    }

    public int getSpaceBetweenItems() {
        return spaceBetweenItems;
    }

    public NBannerView setClick2Selected(boolean isClick2Selected) {
        this.isClick2Selected = isClick2Selected;
        return this;
    }

    public boolean isClick2Selected() {
        return isClick2Selected;
    }

    public NBannerView setInertialRatio(float ratio) {
        this.inertialRatio = Math.max(0.0f, ratio);
        return this;
    }

    public float getInertialRatio() {
        return inertialRatio;
    }

    public NBannerView setAutoCheckRecycleItemSize(boolean autoCheckRecycleItemSize) {
        isAutoCheckRecycleItemSize = autoCheckRecycleItemSize;
        return this;
    }

    public boolean isAutoCheckRecycleItemSize() {
        return isAutoCheckRecycleItemSize;
    }

    public final NBannerView moveItems(final int changed) {
        if (!isMoving && 0 != changed) {
            final int offset = ((orientation == LinearLayout.HORIZONTAL ? getItemWidth() : getItemHeight()) + spaceBetweenItems) * changed;
            autoMove(offset, durationOnAutoScroll, new Runnable() {
                @Override
                public void run() {
                    autoPacking();
                }
            });
        }
        return this;
    }

    public NBannerView setOnItemScrolledListener(OnItemScrolledListener scrolledListener) {
        this.onItemScrolledListener = scrolledListener;
        return this;
    }

    public NBannerView setOnItemSelectedListener(OnItemSelectedListener selectedListener) {
        this.onItemSelectedListener = selectedListener;
        return this;
    }

    public NBannerView setAnimationAdapter(AnimationAdapter animationAdapter) {
        if (null != this.animationAdapter) {
            this.animationAdapter.circularView = null;
        }
        this.animationAdapter = animationAdapter;
        if (null != this.animationAdapter) {
            this.animationAdapter.circularView = this;
            requestLayout();
        }
        return this;
    }

    public NBannerView setIndicator(IIndicator indicator) {
        this.indicator = indicator;
        return this;
    }

    protected void notifyOnItemScrolled(int offset) {
        ItemWrapper centerItem = findItem(getRecycleItemSize() / 2);
        if (null == centerItem) {
            return;
        }
        if (null != onItemScrolledListener) {
            onItemScrolledListener.onItemScrolled(this, centerItem.dataIndex, offset);
        }
        if (null != animationAdapter) {
            animationAdapter.onScrolled(offset);
        }
    }

    private int lastCenterItemDataIndex = -1;

    protected void notifyOnItemSelected() {
        ItemWrapper centerItem = findItem(getRecycleItemSize() / 2);
        if (null == centerItem) {
            return;
        }
        int centerDataIndex = centerItem.dataIndex;
        if (lastCenterItemDataIndex != centerDataIndex) {
            if (null != onItemSelectedListener) {
                onItemSelectedListener.onItemSelected(this, centerDataIndex);
            }
            if (null != indicator) {
                indicator.setCurrentIndex(centerDataIndex);
            }
            lastCenterItemDataIndex = centerDataIndex;
        }
    }

    private void resumeAutoCycle() {
        if (isAutoCycle) {
            removeCallbacks(autoCycleRunnable);
            postDelayed(autoCycleRunnable, intervalOnAutoCycle);
        }
    }

    private void interceptAutoCycle() {
        removeCallbacks(autoCycleRunnable);
    }

    private int getItemWidth() {
        return itemsBounds.length > 0 ? itemsBounds[0].width() : 0;
    }

    private int getItemHeight() {
        return itemsBounds.length > 0 ? itemsBounds[0].height() : 0;
    }

    protected final ItemWrapper findItem(int itemIndex) {
        for (int i = 0; i < getRecycleItemSize(); i++) {
            ItemWrapper item = items.get(i);
            if (item.itemIndex == itemIndex) {
                return item;
            }
        }
        return null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        resetItemsBounds(leftIndent, topIndent, getMeasuredWidth() - rightIndent, getMeasuredHeight() - bottomIndent, spaceBetweenItems);

        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(getItemWidth(), MeasureSpec.EXACTLY);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(getItemHeight(), MeasureSpec.EXACTLY);
        final int size = getChildCount();
        for (int i = 0; i < size; i++) {
            getChildAt(i).measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        alignAllItemPosition();
        if (null != animationAdapter) {
            animationAdapter.onLayout(changed);
        }
    }

    private void registerCheckRecycleItemSize() {
        if (!isAutoCheckRecycleItemSize) {
            return;
        }
        addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                removeOnLayoutChangeListener(this);
                post(new Runnable() {
                    @Override
                    public void run() {
                        final int d = (LinearLayout.VERTICAL == getOrientation() ? getItemHeight() : getItemWidth()) + spaceBetweenItems;
                        if (d <= 0) {
                            return;
                        }
                        final int m = ((LinearLayout.VERTICAL == getOrientation() ? getMeasuredHeight() : getMeasuredWidth()) / 2) / d + 1;
                        if (m >= 2) {
                            setRecycleItemSize(2 * m + 1);
                        } else {
                            setRecycleItemSize(MIN_RECYCLE_ITEM_SIZE);
                        }
                    }
                });
            }
        });
    }

    private void resetItemsBounds(int l, int t, int r, int b, int space) {
        itemsBounds = new Rect[getRecycleItemSize()];
        final int centerIndex = itemsBounds.length / 2;
        int left, top, right, bottom;
        int m;
        for (int i = 0; i < itemsBounds.length; i++) {
            Rect rect = new Rect();
            m = centerIndex - i;
            // 0,1,2, center, 4,5,6
            if (orientation == LinearLayout.VERTICAL) {
                left = l;
                top = t - m * (b - t + space);
                right = r;
                bottom = top + (b - t);
            } else { // LinearLayout.HORIZONTAL
                left = l - m * (r - l + space);
                top = t;
                right = left + (r - l);
                bottom = b;
            }
            rect.set(left, top, right, bottom);
            itemsBounds[i] = rect;
        }
    }

    private void alignAllItemPosition() {
        int size = Math.min(itemsBounds.length, getRecycleItemSize());
        for (int i = 0; i < size; i++) {
            Rect bounds = itemsBounds[i];
            findItem(i).layout(bounds);
        }
    }

    private void updateAllItemView(int centerDataIndex) {
        if (adapter == null || getRecycleItemSize() == 0 || centerDataIndex < 0) {
            return;
        }
        final int centerIndex = getRecycleItemSize() / 2;
        ItemWrapper item;
        //center
        item = findItem(centerIndex);
        item.setDataIndex(centerDataIndex);
        item.updateView();
        // left/top
        for (int i = 1; i <= centerIndex; i++) {
            item = findItem(centerIndex - i);
            item.setDataIndex(cycleDataIndex(centerDataIndex - i));
            item.updateView();
        }
        // right/bottom
        for (int i = 1; i <= centerIndex; i++) {
            item = findItem(centerIndex + i);
            item.setDataIndex(cycleDataIndex(centerDataIndex + i));
            item.updateView();
        }
    }

    private final PointF lastPoint = new PointF();
    private boolean isMoving = false;
    /**
     * 是否需要拦截Touch事件分发,拦截的事件进入onTouchEvent
     */
    private boolean needIntercept = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean superState = super.dispatchTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                needIntercept = false;
                lastPoint.set(event.getX(), event.getY());
                // 禁止父视图中的触摸事件，使事件派发到当前视图中
                // 处理ListView，ScrollView 嵌套的手势事件派发问题
                getParent().requestDisallowInterceptTouchEvent(true);
                return true;// can't return superState.
            case MotionEvent.ACTION_MOVE:
                float absXDiff = Math.abs(event.getX() - lastPoint.x);
                float absYDiff = Math.abs(event.getY() - lastPoint.y);
                // Vertical/Horizontal moving distance > MOVE_SLOP
                if (orientation == LinearLayout.HORIZONTAL) {
                    if (absXDiff > absYDiff && absXDiff > MOVE_SLOP) {
                        needIntercept = true;
                    } else if (absYDiff > absXDiff && absYDiff > MOVE_SLOP) {
                        // restore touch event in parent
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                } else if (orientation == LinearLayout.VERTICAL) {
                    if (absYDiff > absXDiff && absYDiff > MOVE_SLOP) {
                        needIntercept = true;
                    } else if (absXDiff > absYDiff && absXDiff > MOVE_SLOP) {
                        // restore touch event in parent
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
                // pause auto switch
                interceptAutoCycle();
                return superState;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                // restart auto switch
                resumeAutoCycle();
                return superState;
            default:
                return superState;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean superState = super.onInterceptTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return superState;
            case MotionEvent.ACTION_MOVE:
                return needIntercept;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                return superState;
            default:
                return superState;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean superState = super.onTouchEvent(event);
        velocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastPoint.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                if (getRecycleItemSize() != 0 || getChildCount() != 0) {
                    velocityTracker.computeCurrentVelocity(1);
                    float xDiff = event.getX() - lastPoint.x;
                    float yDiff = event.getY() - lastPoint.y;
                    float absXDiff = Math.abs(xDiff);
                    float absYDiff = Math.abs(yDiff);
                    if (orientation == LinearLayout.HORIZONTAL && absXDiff > absYDiff) {
                        move((int) -xDiff);
                    } else if (orientation == LinearLayout.VERTICAL && absYDiff > absXDiff) {
                        move((int) -yDiff);
                    }
                }
                lastPoint.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isMoving) {
                    final int maxOffset = getItemWidth() + spaceBetweenItems;
                    final int offset;
                    final float velocity;
                    // 使用VelocityTracker获取手指离开时的滑动速度
                    if (LinearLayout.HORIZONTAL == orientation) {
                        offset = getScrollX();
                        velocity = velocityTracker.getXVelocity();
                    } else {
                        offset = getScrollY();
                        velocity = velocityTracker.getYVelocity();
                    }
                    // 根据手指离开视图时的速度计算惯性距离
                    int inertialDis = -(int) (velocity * durationOnInertial * inertialRatio);
                    if (Math.abs(inertialDis) + Math.abs(offset) <= maxOffset) {
                        inertialDis = 0;
                    }
                    // 开始自动滑动惯性距离
                    autoMove(inertialDis, durationOnInertial, new Runnable() {
                        @Override
                        public void run() {
                            // 自动滑动（惯性）之后停靠
                            autoPacking();
                        }
                    });
                }
                velocityTracker.clear();
                break;
            default:
                return superState;
        }
        return true;
    }

    protected final void move(final int offset) {
        isMoving = true;
        int scrolled, maxOffset;
        if (orientation == LinearLayout.VERTICAL) {
            scrollBy(0, offset);
            scrolled = getScrollY();
            maxOffset = getItemHeight() + spaceBetweenItems;
        } else { // HORIZONTAL
            scrollBy(offset, 0);
            scrolled = getScrollX();
            maxOffset = getItemWidth() + spaceBetweenItems;
        }
        notifyOnItemScrolled(offset);
        final int overOffset = Math.abs(scrolled) - maxOffset;
        if (overOffset >= 0) {
            final int size = getRecycleItemSize();
            ItemWrapper item;
            if (scrolled > 0) {
                // scroll right or down，index -1
                for (int i = 0; i < size; i++) {
                    item = findItem(i);
                    item.itemIndex -= 1;
                }
            } else if (scrolled < 0) {
                for (int i = size - 1; i >= 0; i--) {
                    item = findItem(i);
                    item.itemIndex += 1;
                }
            }
            // cycleItemIndex：使视图展示内容与adapter中的数据下标进行绑定，形成循环
            for (ItemWrapper tmp : items) {
                tmp.itemIndex = cycleItemIndex(tmp.itemIndex);
            }
            if (orientation == LinearLayout.VERTICAL) {
                scrollTo(0, scrolled > 0 ? overOffset : -overOffset);
            } else { // HORIZONTAL
                scrollTo(scrolled > 0 ? overOffset : -overOffset, 0);
            }
            // 以中心视图作为参考点，向左右/上下两个方向更新视图
            updateAllItemView(getCurrentIndex());
            // 根据参数对齐视图位置和更新大小
            alignAllItemPosition();
        }
    }

    private ValueAnimator autoScroller = null;

    protected final void autoMove(final int offset, final int duration, final Runnable callback) {
        if (autoScroller != null && autoScroller.isStarted()) {
            autoScroller.cancel();
            autoScroller = null;
        }
        if (Math.abs(offset) < 10 || duration < 10) {
            if (offset != 0) {
                move(offset);
            }
            isMoving = false;
            resumeAutoCycle();
            if (null != callback) {
                callback.run();
            }
        } else {
            autoScroller = ValueAnimator.ofInt(0, offset).setDuration(duration);
            autoScroller.setInterpolator(new DecelerateInterpolator());
            autoScroller.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                private int lastValue;

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int currentValue = (int) animation.getAnimatedValue();
                    move(currentValue - lastValue);
                    lastValue = currentValue;
                }
            });
            autoScroller.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    isMoving = true;
                    interceptAutoCycle();
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isMoving = false;
                    resumeAutoCycle();
                    if (null != callback) {
                        callback.run();
                    }
                }
            });
            autoScroller.start();
        }
    }

    protected final void autoPacking() {
        int offset, maxOffset;
        if (orientation == LinearLayout.VERTICAL) {
            offset = getScrollY();
            maxOffset = getItemHeight() + spaceBetweenItems;
        } else {
            offset = getScrollX();
            maxOffset = getItemWidth() + spaceBetweenItems;
        }
        if (0 != offset) {
            final int absOffset = Math.abs(offset);
            if (absOffset >= maxOffset * overRatio) {
                offset = (maxOffset - absOffset) * (offset / absOffset);
            } else {
                offset = -offset;
            }
        }
        autoMove(offset, durationOnPacking, new Runnable() {
            @Override
            public void run() {
                notifyOnItemSelected();
            }
        });
    }

    protected final int cycleDataIndex(int dataIndex) {
        if (adapter == null) {
            return -1;
        }
        int count = adapter.getCount();
        if (count < 2) {
            return 0;
        }
        if (dataIndex > count - 1) {
            dataIndex = dataIndex % count;
        } else if (dataIndex < 0) {
            dataIndex = (count + dataIndex % count) % count;
        }
        return dataIndex;
    }

    protected final int cycleItemIndex(int itemIndex) {
        final int count = getRecycleItemSize();
        if (count < 2) {
            return 0;
        }
        if (itemIndex > count - 1) {
            itemIndex = itemIndex % count;
        } else if (itemIndex < 0) {
            itemIndex = (count + itemIndex % count) % count;
        }
        return itemIndex;
    }

    private final class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            final int tmpIndex = getCurrentIndex();
            if (null != items) {
                for (ItemWrapper item : items) {
                    item.recycle();
                }
            }
            lastCenterItemDataIndex = -1;
            final int newIndex = (tmpIndex >= 0 && tmpIndex < adapter.getCount()) ? tmpIndex : 0;
            setCurrentIndex(newIndex);
            requestLayout();
            if (null != indicator) {
                indicator.setPointerSize(adapter.getCount());
                indicator.setCurrentIndex(newIndex);
            }
        }

        @Override
        public void onInvalidated() {
            invalidate();
        }
    }

    private final class ItemWrapper {

        private static final int NONE = 0x00;
        private static final int USING = 0x01;

        private int state;

        private int itemIndex;
        private int dataIndex;
        private View view;

        ItemWrapper(int itemIndex) {
            this.state = NONE;
            this.itemIndex = itemIndex;
            this.dataIndex = -1;
            this.view = null;
        }

        void layout(Rect bounds) {
            if (null != view) {
                view.layout(bounds.left, bounds.top, bounds.right, bounds.bottom);
                view.invalidate();
            }
        }

        void setDataIndex(int index) {
            if (index != dataIndex) {
                state = NONE;
            }
            this.dataIndex = index;
        }

        void updateView() {
            if (adapter != null && dataIndex >= 0 && dataIndex < adapter.getCount() && state == NONE) {
                state = USING;
                View convertView = adapter.getView(dataIndex, view, NBannerView.this);
                if (convertView == view) {
                    // nothing to do
                } else {
                    // remove old view
                    removeView();
                    // add new view
                    if (convertView != null) {
                        if (convertView.getParent() != NBannerView.this) {
                            addView(convertView);
                        }
                    }
                }
                view = convertView;
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isClick2Selected()) {
                            moveItems(itemIndex - getRecycleItemSize() / 2);
                        }
                    }
                });
            }
        }

        void removeView() {
            if (view != null && view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            view = null;
        }

        void recycle() {
            removeView();
            state = NONE;
            dataIndex = -1;
        }

    }

    public interface OnItemScrolledListener {
        void onItemScrolled(NBannerView v, int dataIndex, float offset);
    }

    public interface OnItemSelectedListener {
        void onItemSelected(NBannerView v, int dataIndex);
    }

    public static abstract class AnimationAdapter {

        private NBannerView circularView;

        protected final NBannerView getCircularView() {
            return circularView;
        }

        protected final View getView(int indexOffset) {
            if (null == circularView) {
                return null;
            }
            final int centerIndex = circularView.getRecycleItemSize() / 2;
            ItemWrapper targetItem = circularView.findItem(cycleIndex(centerIndex + indexOffset));
            return (null != targetItem) ? targetItem.view : null;
        }

        protected final int getOffset(int indexOffset) {
            if (null == circularView) {
                return 0;
            }
            final View centerView = getView(0);
            final View targetView = getView(indexOffset);
            int offset = 0;
            if (null != centerView && null != targetView) {
                if (LinearLayout.VERTICAL == circularView.getOrientation()) {
                    offset = targetView.getTop() - centerView.getTop() - circularView.getScrollY();
                } else { // HORIZONTAL
                    offset = targetView.getLeft() - centerView.getLeft() - circularView.getScrollX();
                }
            }
            return offset;
        }

        protected final float getOffsetPercent(int indexOffset) {
            final int maxOffset;
            if (LinearLayout.VERTICAL == circularView.getOrientation()) {
                maxOffset = getItemHeight() + circularView.spaceBetweenItems;
            } else { // HORIZONTAL
                maxOffset = getItemWidth() + circularView.spaceBetweenItems;
            }
            int targetOffset = getOffset(indexOffset);
            return Math.abs(targetOffset) * 1.0f / maxOffset;
        }

        protected final int getItemWidth() {
            if (null == circularView) {
                return 0;
            }
            return circularView.getItemWidth();
        }

        protected final int getItemHeight() {
            if (null == circularView) {
                return 0;
            }
            return circularView.getItemHeight();
        }

        protected final int cycleIndex(int indexOffset) {
            if (null == circularView) {
                return indexOffset;
            }
            return circularView.cycleItemIndex(indexOffset);
        }

        protected final int getSize() {
            return circularView.getRecycleItemSize();
        }

        protected abstract void onLayout(boolean changed);

        protected abstract void onScrolled(int offset);

    }
}