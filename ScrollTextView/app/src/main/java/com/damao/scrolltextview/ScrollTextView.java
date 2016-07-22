package com.damao.scrolltextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;



/**
 * Created by dy on 2016/5/6.
 * www.91carry.com
 * <p>
 * 可以上下滚动的TextView
 * 需要在xml里面添加属性
 * android:scrollbars="vertical"
 */
public class ScrollTextView extends TextView implements View.OnTouchListener {
    private int minHeight = 200;//实际最低高度
    private int maxHeight = 800;//实际最高高度


    private int maxHeightRaw = maxHeight;//虚拟最大高度

    public void setMaxHeight(float maxHeight) {
        this.maxHeight = (int)maxHeight;
        maxHeightRaw = this.maxHeight;
    }

    public float getMinHeightScroll() {
        return minHeight;
    }

    public void setMinHeight(int minHeight) {
        this.minHeight = minHeight;
    }

    public ScrollTextView(Context context) {
        super(context);
        init();
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScrollTextView_attrs);
        minHeight = a.getDimensionPixelSize(R.styleable.ScrollTextView_attrs_height_min, 200);
        setHeight(minHeight);
        maxHeight = a.getDimensionPixelSize(R.styleable.ScrollTextView_attrs_height_max, 1000);
        maxHeightRaw = maxHeight;
        init();
        a.recycle();
    }

    public ScrollTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }


    private void init() {
        setOnTouchListener(this);
        setClickable(true);
        setLongClickable(true);

    }

    /**
     * 重置TextView
     */
    public void resetTextView() {
        setMovementMethod(null);
        init();
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = minHeight;
        setLayoutParams(params);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);

        ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int h = getLineCount() * getLineHeight() + getLineHeight() / 2 + getPaddingTop();
                if (h < minHeight) {
                    maxHeight = minHeight;
                } else if (h < maxHeight) {
                    maxHeight = h;
                }
            }
        });
    }

    private float  downly,  upy;
    private float  y,  ym;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                y = event.getRawY();
                downly = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (getMovementMethod() != null) {
                    setMovementMethod(null);
                    init();
                }

                ym = event.getRawY();


                ViewGroup.LayoutParams params = getLayoutParams();

                if (params.height >= maxHeightRaw && event.getY() > getPaddingTop()) {   //如果已经铺满整个屏幕，则不能滑动缩小
                    if (getMovementMethod() == null) {
                        setMovementMethod(ScrollingMovementMethod.getInstance());
                        setScrollbarFadingEnabled(true);
                        init();
                    }
                    params.height = maxHeight;
                    setLayoutParams(params);
                    return false;
                }

                int h = params.height += (y - ym);
                if (h < minHeight) {
                    params.height = minHeight;
                    setLayoutParams(params);
                    return false;
                }

                if (h >= maxHeight) {
                    if (getMovementMethod() == null) {
                        setMovementMethod(ScrollingMovementMethod.getInstance());
                        setScrollbarFadingEnabled(true);
                        init();
                    }
                    params.height = maxHeight;
                } else {
                    if (getMovementMethod() != null) {
                        setMovementMethod(null);
                        init();
                    }
                    y = ym;
                    params.height = h;
                }
                setLayoutParams(params);
                break;
            case MotionEvent.ACTION_UP:
                ViewGroup.LayoutParams params_up = getLayoutParams();
                upy = event.getRawY();

                if (upy < downly) {
                    params_up.height = maxHeight;
                    if (getMovementMethod() == null) {
                        setMovementMethod(ScrollingMovementMethod.getInstance());
                        setScrollbarFadingEnabled(true);
                        init();
                    }
                    setLayoutParams(params_up);
                } else if (upy == downly || params_up.height < maxHeightRaw) {
                    resetTextView();
                }
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);

        if (iLayoutChanged != null) {
            iLayoutChanged.onLayoutChanged(params);
        }
    }


    private ILayoutChanged iLayoutChanged;

    public void setIlayoutChanged(ILayoutChanged ilayoutChanged) {
        this.iLayoutChanged = ilayoutChanged;
    }

    public  interface ILayoutChanged {
         void onLayoutChanged(ViewGroup.LayoutParams params);
    }
}