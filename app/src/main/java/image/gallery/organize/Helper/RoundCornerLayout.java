package image.gallery.organize.Helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Canvas;
import android.widget.FrameLayout;

import image.gallery.organize.R;


public class RoundCornerLayout extends FrameLayout {
    // CONSTANTS
    private final static float CORNER_RADIUS_DEFAULT = 10.0f;

    // VARIABLES
    private boolean mTopLeftEnabled = true;
    private boolean mTopRightEnabled = true;
    private boolean mBottomLeftEnabled = true;
    private boolean mBottomRightEnabled = true;
    private float mCornerRadius = CORNER_RADIUS_DEFAULT;

    // IMPLEMENTS
    public RoundCornerLayout(Context context) {
        this(context, null);
    }

    public RoundCornerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundCornerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setupAttributes(attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        final int count = canvas.save();
        final Path path = new Path();
        final RectF rect = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
        final float[] arrayRadius = {0, 0, 0, 0, 0, 0, 0, 0};

        if (mTopLeftEnabled) {
            arrayRadius[0] = mCornerRadius;
            arrayRadius[1] = mCornerRadius;
        }

        if (mTopRightEnabled) {
            arrayRadius[2] = mCornerRadius;
            arrayRadius[3] = mCornerRadius;
        }

        if (mBottomRightEnabled) {
            arrayRadius[4] = mCornerRadius;
            arrayRadius[5] = mCornerRadius;
        }

        if (mBottomLeftEnabled) {
            arrayRadius[6] = mCornerRadius;
            arrayRadius[7] = mCornerRadius;
        }

        path.addRoundRect(rect, arrayRadius, Path.Direction.CW);
        canvas.clipPath(path);

        super.dispatchDraw(canvas);

        canvas.restoreToCount(count);
    }

    public void setRadius(float radius) {
        mCornerRadius = radius;
        invalidate();
    }

    public float getRadius() {
        return mCornerRadius;
    }

    public void setCornerEnabled(boolean topLeft, boolean topRight, boolean bottomLeft, boolean bottomRight) {
        mTopLeftEnabled = topLeft;
        mTopRightEnabled = topRight;
        mBottomLeftEnabled = bottomLeft;
        mBottomRightEnabled = bottomRight;
        invalidate();
    }

    private void setupAttributes(AttributeSet attrs) {
        final float radius = getPixelValue(CORNER_RADIUS_DEFAULT);
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SkyRoundCornerLayout);

        mCornerRadius = a.getDimension(R.styleable.SkyRoundCornerLayout_cornerRadius, radius);

        if (a.hasValue(R.styleable.SkyRoundCornerLayout_topEnabled)) {
            mTopLeftEnabled = a.getBoolean(R.styleable.SkyRoundCornerLayout_topEnabled, true);
            mTopRightEnabled = mTopLeftEnabled;
        } else {
            mTopLeftEnabled = a.getBoolean(R.styleable.SkyRoundCornerLayout_topLeftEnabled, true);
            mTopRightEnabled = a.getBoolean(R.styleable.SkyRoundCornerLayout_topRightEnabled, true);
        }

        if (a.hasValue(R.styleable.SkyRoundCornerLayout_bottomEnabled)) {
            mBottomLeftEnabled = a.getBoolean(R.styleable.SkyRoundCornerLayout_bottomEnabled, true);
            mBottomRightEnabled = mBottomLeftEnabled;
        } else {
            mBottomLeftEnabled = a.getBoolean(R.styleable.SkyRoundCornerLayout_bottomLeftEnabled, true);
            mBottomRightEnabled = a.getBoolean(R.styleable.SkyRoundCornerLayout_bottomRightEnabled, true);
        }

        a.recycle();
    }

    private float getPixelValue(float dip) {
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, metrics);
    }
}