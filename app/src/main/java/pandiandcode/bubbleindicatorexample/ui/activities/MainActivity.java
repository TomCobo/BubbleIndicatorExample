package pandiandcode.bubbleindicatorexample.ui.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import pandiandcode.bubbleindicator.BubbleIndicatorView;
import pandiandcode.bubbleindicatorexample.R;
import pandiandcode.bubbleindicatorexample.ui.adapters.ExamplePageAdapter;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private BubbleIndicatorView mPagerIndicator;
    private ExamplePageAdapter mAdapter;
    private boolean mFromIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        referenceUI();
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);

        setupAdapter();
    }

    // Implemtented ButterKnife
    private void referenceUI() { 
        mViewPager = (ViewPager) findViewById(R.id.pagerStages);
        mPagerIndicator = (BubbleIndicatorView) findViewById(R.id.pageIndicator);
    }

    private void setupAdapter() {
        mAdapter = new ExamplePageAdapter();
        mViewPager.setAdapter(mAdapter);
        mPagerIndicator.setDotsCount(mAdapter.getCount());
        mPagerIndicator.setOnClickListener(mOnClickListener);
        mPagerIndicator.setColor(getResources().getColor(mAdapter.getTextColorReference(0)));

    }

    private void updateColor(int colorReference) {
        mPagerIndicator.setColor(getResources().getColor(colorReference));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(colorReference)));
    }

    ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override public void onPageSelected(int position) {
            if (!mFromIndicator) {
                updateColor(mAdapter.getTextColorReference(position));
                mPagerIndicator.selectIndicator(position, true);
            }
            mFromIndicator = false;
        }

        @Override public void onPageScrollStateChanged(int state) {

        }
    };

    BubbleIndicatorView.OnClickListener mOnClickListener = new BubbleIndicatorView.OnClickListener() {
        @Override public void onClick(int position) {
            mFromIndicator = true;
            updateColor(mAdapter.getTextColorReference(position));
            mViewPager.setCurrentItem(position, true);
        }
    };
}
