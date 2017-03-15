package com.jpyl.suspenddemo;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    ListView listView;
    View header1, header2;
    TextView textView, tv1;
    LinearLayout ll;
    ImageView iv;
    RelativeLayout re;
    float x, y, ly, pX, pY = 0;
    int h;
    boolean visible = true;
    final int MAXMOVE = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listview);
        header1 = View.inflate(this, R.layout.header1, null);
        header2 = View.inflate(this, R.layout.header2, null);
        tv1 = (TextView) findViewById(R.id.tv01);
        textView = (TextView) findViewById(R.id.tv);
        ll = (LinearLayout) findViewById(R.id.ll);
        iv = (ImageView) header1.findViewById(R.id.iv);
        re = (RelativeLayout) header1.findViewById(R.id.re);
        listView.addHeaderView(header1);
        listView.addHeaderView(header2);
        header1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                h = header1.getHeight();
                header1.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            list.add("第" + i + "项");
        }
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if (i == 0) {
                    visible = true;
                } else {
                    visible = false;
                }
                if (1 >= i && 1 <= i + i1)//可见范围内
                {
                    if (textView.getTop() >= header2.getTop()) {
                        textView.setVisibility(View.VISIBLE);
                        ll.setAlpha(1);
                    } else {
                        int value = header2.getTop() - textView.getTop();
                        if (value <= textView.getHeight() && 0 <= value) {
                            ll.setAlpha(1 - (float) value / (float) textView.getHeight());
                        } else {
                            ll.setAlpha(0);
                        }
                        textView.setVisibility(View.INVISIBLE);
                    }

                } else {
                    ll.setAlpha(1);
                    textView.setVisibility(View.VISIBLE);

                }
            }
        });
        listView.setAdapter(aa);
        listView.setOnTouchListener(this);

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                pX = motionEvent.getX();
                pY = motionEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                ly = 0;
                x = motionEvent.getX();
                y = motionEvent.getY();
                if (y - pY > 0) {
                    final ViewGroup.LayoutParams lp = header1.getLayoutParams();
                    final ViewGroup.LayoutParams lp01 = re.getLayoutParams();
                    ValueAnimator va = ValueAnimator.ofInt(header1.getHeight(), h);
                    va.setDuration(Math.abs(500*(header1.getHeight()-h)/MAXMOVE));
                    va.setInterpolator(new DecelerateInterpolator());
                    va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            lp.height = (int) valueAnimator.getAnimatedValue();
                            lp01.height = lp.height;
                            header1.setLayoutParams(lp);
                            re.setLayoutParams(lp01);
                        }
                    });
                    va.start();
                } else {


                }
                break;
            case MotionEvent.ACTION_MOVE:
                float y = motionEvent.getY();
                if (ly == 0 || !visible) {
                    ly = y;
                    break;
                }
                int offy = (int) (y - ly);

                if (header1.getHeight() <= h) {//header1可见,header1小于原来高度
                    ViewGroup.LayoutParams lp = header1.getLayoutParams();
                    lp.height = (int) (header1.getHeight() + offy);
                    header1.setLayoutParams(lp);
                } else if (header1.getHeight() <= h + MAXMOVE) {
                    ViewGroup.LayoutParams lp01 = re.getLayoutParams();
//                    Log.i("MYTAG","MYTAG----"+offy * (1 - ((float) header1.getHeight() / (h + MAXMOVE)*0.8)));
                    lp01.height = (int) (re.getHeight() + offy * (1 - ((float) header1.getHeight() / (h + MAXMOVE)*0.8)));
                    if (lp01.height > h + MAXMOVE)
                        lp01.height = h + MAXMOVE;
                    re.setLayoutParams(lp01);
                    ViewGroup.LayoutParams lp = header1.getLayoutParams();
                    lp.height = lp01.height;
                    if (lp.height > h + MAXMOVE)
                        lp.height = h + MAXMOVE;
                    header1.setLayoutParams(lp);

                }
                ly = y;
                break;
            default:
                break;
        }
        if (header1.getHeight() > h && (header1.getBottom() == header1.getHeight())) {
            return true;
        }
        return false;
    }

}
