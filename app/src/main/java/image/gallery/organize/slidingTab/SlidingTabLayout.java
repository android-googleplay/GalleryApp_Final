package image.gallery.organize.slidingTab;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.viewpager.widget.ViewPager;

import image.gallery.organize.R;

import java.util.Objects;

import static image.gallery.organize.slidingTab.TabType.TEXT_ONLY;

public class SlidingTabLayout extends HorizontalScrollView {
    private static final int TEXT_ICON_TAB = R.layout.item_text_icon;

    public static final int FOCUSED_WHITE = 0xFFFFFFFF;
    public static final int NOT_FOCUSED_WHITE = 0xB3FFFFFF;

    private static final int TEXT_ID = R.id.TabText;
    private static final int ICON_ID = R.id.TabImage;

    private boolean mDistributeEvenly;

    private int customFocusedColor;
    private int customUnfocusedColor;

    private int textSize = 10;
    private boolean allCaps = true;

    private TabType tabType;
    private ActionBar actionBar;
    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mViewPagerPageChangeListener;

    private final SlidingTabStrip mTabStrip;

    public SlidingTabLayout(Context context) {
        this(context, null);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setHorizontalScrollBarEnabled(false);
        setFillViewport(true);

        mTabStrip = new SlidingTabStrip(context);
        addView(mTabStrip, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    public void setCustomUnfocusedColor(int customUnfocusedColor) {
        this.customUnfocusedColor = customUnfocusedColor;
    }

    public void setDistributeEvenly(boolean distributeEvenly) {
        mDistributeEvenly = distributeEvenly;
    }

    public void setTabType(TabType tabType) {
        this.tabType = tabType;
    }

    public void setSelectedIndicatorColors(int... colors) {
        mTabStrip.setSelectedIndicatorColors(colors);
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void setAllCaps(boolean allCaps) {
        this.allCaps = allCaps;
    }


    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mViewPagerPageChangeListener = listener;
    }

    public void setViewPager(ViewPager viewPager) {
        mTabStrip.removeAllViews();

        if (tabType == null)
            tabType = TEXT_ONLY;

        mViewPager = viewPager;
        if (viewPager != null) {
            viewPager.setOnPageChangeListener(new InternalViewPagerListener());
            populateTabStrip();

            SlidingFragmentPagerAdapter adapter = (SlidingFragmentPagerAdapter) mViewPager.getAdapter();
            if (actionBar != null) {
                assert adapter != null;
                actionBar.setTitle(adapter.getToolbarTitle(mViewPager.getCurrentItem()));
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mViewPager != null) {
            scrollToTab(mViewPager.getCurrentItem(), 0);
        }
    }

    private void populateTabStrip() {
        final SlidingFragmentPagerAdapter adapter = (SlidingFragmentPagerAdapter) mViewPager.getAdapter();
        final OnClickListener tabClickListener = new TabClickListener();

        if (tabType == null)
            tabType = TEXT_ONLY;

        int focused_color = Color.parseColor("#0081FA");
        int unfocused_color =Color.parseColor("#616161");

        customFocusedColor = focused_color;
        customUnfocusedColor = unfocused_color;

        for (int i = 0; i < Objects.requireNonNull(adapter).getCount(); i++) {
            View tabView = null;
            TextView tabTitleView = null;
            ImageView tabImageView = null;

            if (tabType == TabType.TEXT_ICON) {
                tabView = LayoutInflater.from(getContext()).inflate(TEXT_ICON_TAB, mTabStrip, false);
                tabTitleView = tabView.findViewById(TEXT_ID);
                tabImageView = tabView.findViewById(ICON_ID);
            }

            if (mDistributeEvenly) {
                assert tabView != null;
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                lp.width = 0;
                lp.weight = 1;
            }

            if (i == mViewPager.getCurrentItem()) {
                assert tabView != null;
                tabView.setSelected(true);
            }

            if (tabTitleView != null && adapter.getPageTitle(i) != null) {
                tabTitleView.setText(adapter.getPageTitle(i));
                tabTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.textSize);
                tabTitleView.setTextColor(i == mViewPager.getCurrentItem() ?
                    focused_color : unfocused_color);

                tabTitleView.setAllCaps(allCaps);
            }

            if (tabImageView != null && adapter.getPageDrawable(i) != null) {
                tabImageView.setImageDrawable(adapter.getPageDrawable(i));
                tabImageView.setColorFilter(i == mViewPager.getCurrentItem() ?
                    focused_color : unfocused_color, PorterDuff.Mode.SRC_ATOP);
            }

            assert tabView != null;
            tabView.setOnClickListener(tabClickListener);
            mTabStrip.addView(tabView);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() > 0) {
            View child = getChildAt(0);

            int height = (int) convertPixelsToDp(child.getMeasuredHeight());
            if (height > 0) {
                int desiredHeight = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics()
                );
                int newHeightSpec = MeasureSpec.makeMeasureSpec(desiredHeight, MeasureSpec.EXACTLY);

                super.onMeasure(widthMeasureSpec, newHeightSpec);
                return;
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void scrollToTab(int tabIndex, int positionOffset) {
        final int tabStripChildCount = mTabStrip.getChildCount();
        if (tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return;
        }

        View selectedChild = mTabStrip.getChildAt(tabIndex);
        if (selectedChild != null) {
            int targetScrollX = selectedChild.getLeft() + positionOffset;

            scrollTo(targetScrollX, 0);
        }
    }

    private class TabClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                if (v == mTabStrip.getChildAt(i)) {
                    mViewPager.setCurrentItem(i);
                    return;
                }
            }
        }
    }

    public float convertPixelsToDp(float px) {
        return px / ((float) getContext().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        private int mScrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int tabStripChildCount = mTabStrip.getChildCount();
            if ((position < 0) || (position >= tabStripChildCount)) {
                return;
            }

            mTabStrip.onViewPagerPageChanged(position, positionOffset);

            View selectedTitle = mTabStrip.getChildAt(position);
            int extraOffset = (selectedTitle != null)
                ? (int) (positionOffset * selectedTitle.getWidth())
                : 0;
            scrollToTab(position, extraOffset);

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrolled(position, positionOffset,
                    positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            TextView tabTitleView;
            ImageView tabImageView;

            SlidingFragmentPagerAdapter adapter = (SlidingFragmentPagerAdapter) mViewPager.getAdapter();
            if (adapter == null) return;

            int focused_color = customFocusedColor != 0 ? customFocusedColor : FOCUSED_WHITE;
            int unfocused_color = customUnfocusedColor != 0 ? customUnfocusedColor : NOT_FOCUSED_WHITE;

            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                mTabStrip.onViewPagerPageChanged(position, 0f);
                scrollToTab(position, 0);
            }

            if (actionBar != null) {
                actionBar.setTitle(adapter.getToolbarTitle(mViewPager.getCurrentItem()));
            }

            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                mTabStrip.getChildAt(i).setSelected(position == i);

                switch (tabType) {
                    case TEXT_ONLY:
                        tabTitleView = mTabStrip.getChildAt(i).findViewById(TEXT_ID);
                        tabTitleView.setTextColor(i == mViewPager.getCurrentItem() ?
                            focused_color : unfocused_color);
                        break;

                    case ICON_ONLY:
                        tabImageView = mTabStrip.getChildAt(i).findViewById(ICON_ID);
                        tabImageView.setColorFilter(i == mViewPager.getCurrentItem() ?
                            focused_color : unfocused_color, PorterDuff.Mode.SRC_ATOP);
                        break;

                    case TEXT_ICON:
                        tabTitleView = mTabStrip.getChildAt(i).findViewById(TEXT_ID);
                        tabImageView = mTabStrip.getChildAt(i).findViewById(ICON_ID);
                        tabTitleView.setTextColor(i == mViewPager.getCurrentItem() ?
                            focused_color : unfocused_color);
                        tabImageView.setColorFilter(i == mViewPager.getCurrentItem() ?
                            focused_color : unfocused_color, PorterDuff.Mode.SRC_ATOP);
                        break;
                }

            }
            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageSelected(position);
            }
        }

    }
}
