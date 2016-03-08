package pandiandcode.bubbleindicator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * Created by Rocio Ortega on 30/01/16.
 */
public class BubbleIndicatorView extends RelativeLayout {

    private static final float SCALE_OUT = 1.5f;
    private static final float SCALE_IN = 0.75f;
    private static final float SCALE_NORMAL = 1f;
    private static final int DELAY = 150;
    private static final int DURATION = 450;
    private int mDotsCount;
    private int mColor;
    private int mRadius;
    private int mDistance;
    private int mMaxDiameter;
    private int mSelectedPosition;
    private int mWidth;
    private Indicator mMoveIndicator;
    private OnClickListener mOnClickListener;

    public interface OnClickListener {
        void onClick(int position);
    }

    public BubbleIndicatorView(Context context) {
        super(context);
        init(null);
    }

    public BubbleIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public void selectIndicator(int position, boolean animate) {
        if (mSelectedPosition != position) {
            Indicator newIndicator = (Indicator) getChildAt(position);
            Indicator oldIndicator = null;

            if (newIndicator == null) {
                return;
            }

            if (mSelectedPosition >= 0) {
                oldIndicator = (Indicator) getChildAt(mSelectedPosition);
            }

            if (animate) {
                newIndicator.select(DELAY, null);
                if (oldIndicator != null) {
                    oldIndicator.unSelect(0, null);
                }
                mMoveIndicator.animate()
                        .setDuration(DURATION)
                        .translationX(getXPosition(position))
                        .scaleX(SCALE_IN)
                        .scaleY(SCALE_IN)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mMoveIndicator.setScaleX(SCALE_OUT);
                                mMoveIndicator.setScaleY(SCALE_OUT);
                            }
                        });

            } else {
                newIndicator.select();
                if (oldIndicator != null) {
                    oldIndicator.unSelect();
                }
                mMoveIndicator.setX(getXPosition(position));
            }
            mSelectedPosition = position;
        }
    }

    public void setDotsCount(int dotsCount) {
        mDotsCount = dotsCount;
        setupIndicator();
    }

    public void setColor(int color) {
        mColor = color;
        setupIndicator();
    }

    public void setDistance(int distance) {
        mDistance = distance;
        setupIndicator();
    }

    public void setRadius(int radius) {
        mRadius = radius;
        mMaxDiameter = calculateMaxDiameter();
        setMinimumHeight(mMaxDiameter);
        setupIndicator();
    }

    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(mWidth, h, oldw, oldh);

    }

    private void init(AttributeSet attrs) {
        mSelectedPosition = -1;
        mColor = Color.BLACK;
        mDistance = 30;
        mDotsCount = 3;
        mRadius = 30;
        mWidth = getContext().getResources().getDisplayMetrics().widthPixels;

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(
                    attrs, R.styleable.BubbleIndicatorView);

            mDotsCount = typedArray.getInteger(R.styleable.BubbleIndicatorView_biv_dotCount, mDotsCount);
            mRadius = typedArray.getDimensionPixelSize(R.styleable.BubbleIndicatorView_biv_radius, mRadius);
            mDistance = typedArray.getDimensionPixelSize(R.styleable.BubbleIndicatorView_biv_distance, mDistance);
            mColor = typedArray.getColor(R.styleable.BubbleIndicatorView_biv_color, mColor);
        }
        mMaxDiameter = calculateMaxDiameter();
        setMinimumHeight(mMaxDiameter);

        initIndicators();
        selectIndicator(0, false);
        setWillNotDraw(false);
    }

    private void initIndicators() {
        removeAllViews();
        for (int i = 0; i < mDotsCount; i++) {
            addIndicator(i, false);
        }
        mMoveIndicator = addIndicator(0, true);
        scaleOut(mMoveIndicator);
    }

    private Indicator addIndicator(int position, boolean isMove) {
        Indicator indicator = new Indicator(getContext());
        if (isMove) {
            addView(indicator);
        } else {
            addView(indicator, position);
            indicator.setTag(position);
            indicator.setOnClickListener(mOnIndicatorListener);
        }
        indicator.setX(getXPosition(position));

        return indicator;
    }

    private void setupIndicator() {
        initIndicators();
        int currentPosition = mSelectedPosition == 0 ? 0 : mSelectedPosition;
        mSelectedPosition = -1;
        selectIndicator(currentPosition, false);
    }

    private int calculateMaxDiameter() {
        return (int) (mRadius * 2 * SCALE_OUT);
    }

    private View.OnClickListener mOnIndicatorListener = new View.OnClickListener() {
        @Override public void onClick(View v) {
            int position = (int) v.getTag();
            if (mOnClickListener != null) {
                mOnClickListener.onClick(position);
            }
            selectIndicator(position, true);
        }
    };

    private void scaleOut(Indicator indicator) {
        indicator.setScaleX(SCALE_OUT);
        indicator.setScaleY(SCALE_OUT);
    }

    private float getXPosition(int position) {
        int totalWidth = (mMaxDiameter * mDotsCount) + (mDistance * (mDotsCount - 1));
        int margins = (int) ((mWidth - totalWidth) / 2 + (mRadius * SCALE_IN));
        return margins + (mDistance + mMaxDiameter) * position;
    }

    private class Indicator extends FrameLayout {

        private Paint mPaint;
        private int mDiameter;

        public Indicator(Context context) {
            super(context);
            mDiameter = mRadius * 2;
            initPaint();
            setMinimumWidth(mDiameter);
            setMinimumHeight(mDiameter);
            setWillNotDraw(false);
        }

        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
        }

        @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(mDiameter, mDiameter, oldw, oldh);
            initPaint();
            invalidate();
        }

        @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(mDiameter, mDiameter);
        }

        private void initPaint() {
            mPaint = new Paint();
            mPaint.setColor(mColor);
            mPaint.setStyle(Paint.Style.FILL);
        }

        public void select(int delay, AnimatorListenerAdapter callback) {
            animate().setStartDelay(delay)
                    .scaleX(SCALE_OUT)
                    .scaleY(SCALE_OUT)
                    .setListener(callback);
        }

        public void select() {
            setScaleX(SCALE_OUT);
            setScaleY(SCALE_OUT);
        }

        public void unSelect(int delay, AnimatorListenerAdapter callback) {
            animate()
                    .setStartDelay(delay)
                    .scaleX(SCALE_NORMAL)
                    .scaleY(SCALE_NORMAL)
                    .setListener(callback);
        }

        public void unSelect() {
            setScaleX(SCALE_NORMAL);
            setScaleY(SCALE_NORMAL);
        }
    }
}

