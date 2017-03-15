package com.jpyl.suspenddemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2017/1/23.
 */

public class PullZoomListview extends ListView implements AbsListView.OnScrollListener, View.OnTouchListener {
    private final int MAX_OFFSET = 1000;//最大偏移量
    private View header;//头布局
    private RelativeLayout child;//填充头布局子布局
    private View suspend;//悬浮View
    private View fixed;//固定View
    private View topBar;//顶部栏
    private boolean isVisable = true;//是否可见
    private float upY, moveY, lastY, downY = 0;
    private int height = 400;//header的高度
    private float damp = 1f;//阻尼系数,值越大阻力越大
    private ViewGroup.LayoutParams headerLp;
    private ViewGroup.LayoutParams childLp;
    public PullZoomListview(Context context) {
        super(context);
    }

    public PullZoomListview(Context context, AttributeSet attrs) {
        super(context, attrs);
        header = View.inflate(context, R.layout.header1, null);
        suspend = View.inflate(context, R.layout.header2, null);
        child = (RelativeLayout) header.findViewById(R.id.re);
        addHeaderView(header);
        addHeaderView(suspend);
        this.setOnTouchListener(this);
    }

    public void setFixed(View view) {
        this.fixed = view;
    }

    public void setTopBar(View view) {
        this.topBar = view;
    }

    public PullZoomListview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        if (1 >= i && 1 <= i + i1)//可见范围内
        {
            if (fixed.getTop() >= suspend.getTop()) {
                fixed.setVisibility(View.VISIBLE);
                topBar.setAlpha(1);
            } else {
                int value = suspend.getTop() - fixed.getTop();
                if (value <= fixed.getHeight() && 0 <= value) {
                    topBar.setAlpha(1 - (float) value / (float) fixed.getHeight());
                } else {
                    topBar.setAlpha(0);
                }
                fixed.setVisibility(View.INVISIBLE);
            }

        } else {
            topBar.setAlpha(1);
            fixed.setVisibility(View.VISIBLE);

        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //  Log.i("MYTAG", "MYTAG----按下");
                downY = motionEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                // Log.i("MYTAG", "MYTAG----弹起");

                lastY = 0;
                upY = motionEvent.getY();
                if (upY - downY > 0) {
                    final ViewGroup.LayoutParams headerLp = header.getLayoutParams();
                    final ViewGroup.LayoutParams childLp = child.getLayoutParams();
                    //  Log.i("MYTAG", "MYTAG---回弹动画-" + header.getHeight() + "--" + height);
                    ValueAnimator va = ValueAnimator.ofInt(header.getHeight(), height);
                    va.setDuration(Math.abs(500 * (header.getHeight() - height) / MAX_OFFSET));
                    va.setInterpolator(new DecelerateInterpolator());
                    va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            headerLp.height = (int) valueAnimator.getAnimatedValue();
                            childLp.height = headerLp.height;
                            header.setLayoutParams(headerLp);
                            child.setLayoutParams(childLp);
                        }
                    });
                    va.start();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //Log.i("MYTAG", "MYTAG----移动");
                moveY = motionEvent.getY();
                if (lastY == 0 || !isVisable) {
                    lastY = moveY;
                    break;
                }
                int offy = (int) (moveY - lastY);
                if (header.getHeight() <= height) {//header1可见,header1小于原来高度
                    ViewGroup.LayoutParams headerLp = header.getLayoutParams();
                    headerLp.height = (int) (header.getHeight() + offy);
                    header.setLayoutParams(headerLp);
                } else if (header.getHeight() <= height + MAX_OFFSET) {
                    ViewGroup.LayoutParams childLp = child.getLayoutParams();
                    childLp.height = (int) (child.getHeight() + offy * (1 - ((float) header.getHeight() / (height + MAX_OFFSET) * damp)));
                    if (childLp.height > height + MAX_OFFSET)
                        childLp.height = height + MAX_OFFSET;
                    child.setLayoutParams(childLp);
                    ViewGroup.LayoutParams headerLp = header.getLayoutParams();
                    headerLp.height = childLp.height;
                    if (headerLp.height > height + MAX_OFFSET)
                        headerLp.height = height + MAX_OFFSET;
                    header.setLayoutParams(headerLp);
                }
                lastY = moveY;
                break;
            default:
                break;
        }
        if (header.getHeight() > height && (header.getBottom() == header.getHeight())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }

    public void setScroll() {
        this.setOnScrollListener(this);
    }
}
