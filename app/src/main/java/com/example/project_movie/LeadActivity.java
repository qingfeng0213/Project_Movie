package com.example.project_movie;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LeadActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private int[] mImageIds = new int[]{R.drawable.tu1, R.drawable.tu2, R.drawable.tu3,R.drawable.tu4};
    private ArrayList<ImageView> mImageViewList;
    private LinearLayout llContainer;
    private ImageView ivRedPoint;
    private int mPaintDis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lead);
        //隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initView();
    }



    protected void initView() {
        mViewPager = findViewById(R.id.vp_guide);
        llContainer = findViewById(R.id.ll_container);
        ivRedPoint = findViewById(R.id.iv_red);


        initData();
        GuideAdapter adapter = new GuideAdapter();
        //添加动画效果
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        mViewPager.setAdapter(adapter);

        //监听布局是否已经完成  布局的位置是否已经确定
        ivRedPoint.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //避免重复回调        出于兼容性考虑，使用了过时的方法
                ivRedPoint.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                //布局完成了就获取第一个小灰点和第二个之间left的距离
                mPaintDis = llContainer.getChildAt(1).getLeft() - llContainer.getChildAt(0).getLeft();
                System.out.println("距离：" + mPaintDis);
            }
        });


        //ViewPager滑动Pager监听
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //滑动过程中的回调
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //当滑到第二个Pager的时候，positionOffset百分比会变成0，position会变成1，所以后面要加上position*mPaintDis
                int letfMargin = (int) (mPaintDis * positionOffset) + position * mPaintDis;
                //在父布局控件中设置他的leftMargin边距
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivRedPoint.getLayoutParams();
                params.leftMargin = letfMargin;
                ivRedPoint.setLayoutParams(params);
            }


            /**
             * 设置按钮最后一页显示，其他页面隐藏
             * @param position
             */
            @Override
            public void onPageSelected(int position) {
                System.out.println("position:" + position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                System.out.println("state:" + state);
            }
        });
    }

    class GuideAdapter extends PagerAdapter {

        //item的个数
        @Override
        public int getCount() {
            return mImageViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        //初始化item布局
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = mImageViewList.get(position);
            container.addView(view);
            return view;
        }

        //销毁item
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }


    protected void initData() {
        mImageViewList = new ArrayList<>();
        for (int i = 0; i < mImageIds.length; i++) {
            //创建ImageView把mImgaeViewIds放进去
            ImageView view = new ImageView(this);
            view.setBackgroundResource(mImageIds[i]);
            //添加到ImageView的集合中
            mImageViewList.add(view);
            //小圆点
            ImageView pointView = new ImageView(this);
            pointView.setImageResource(R.drawable.shape_point1);
            //初始化布局参数，父控件是谁，就初始化谁的布局参数
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i > 0) {
                //当添加的小圆点的个数超过一个的时候就设置当前小圆点的左边距为20dp;
                params.leftMargin = 20;
            }
            //设置小灰点的宽高包裹内容
            pointView.setLayoutParams(params);
            //将小灰点添加到LinearLayout中
            llContainer.addView(pointView);
        }
    }

    private class DepthPageTransformer implements ViewPager.PageTransformer {

        private static final float MIN_SCALE = 0.75f;
        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);
            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);
            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);
                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);
                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
             }
            }
        }
    }
