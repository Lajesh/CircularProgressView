package com.lajesh.circularprogressview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * This class represents the image view with circular progress
 * Author: Lajesh
 * Email: lajeshds2007@gmail.com
 * Created: 8/30/18
 * Modified: 8/30/18
 */
public class ImageProgressView extends AppCompatImageView {

    private static final String TAG = ImageProgressView.class.getSimpleName();
    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int DEFAULT_ANIMATION_TIME = 2000;
    private static final int DEFAULT_BORDER_WIDTH = 0;
    private static final int DEFAULT_BORDER_COLOR = Color.BLACK;
    private static final int DEFAULT_FILL_COLOR = Color.TRANSPARENT;
    private static final int DEFAULT_PROGRESS_COLOR = Color.BLUE;
    private static final boolean DEFAULT_BORDER_OVERLAY = false;
    private static final boolean DEFAULT_DRAW_ANTI_CLOCKWISE = false;
    private static final float DEFAULT_INNTER_DAIMMETER_FRACTION = 0.805f;
    private final RectF mDrawableRect = new RectF();
    private final RectF mBorderRect = new RectF();

    private final Matrix mShaderMatrix = new Matrix();
    private final Paint mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float mBaseStartAngle = 0f;
    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;
    private int mFillColor = DEFAULT_FILL_COLOR;
    private int mProgressColor = DEFAULT_PROGRESS_COLOR;
    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private int mBitmapHeight;
    private float mInnrCircleDiammeter;
    private float mDrawableRadius;
    private float mProgressValue = 0;
    private ValueAnimator mValueAnimator;
    private boolean mReady;
    private boolean mSetupPending;
    private boolean mBorderOverlay;
    private boolean mDrawAntiClockwise;
    private boolean animationState = true;
    private Context mContext;
    private String mIconUrl;


    public ImageProgressView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public ImageProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.mContext = context;
    }

    public ImageProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageProgressView, defStyle, 0);

        mBorderWidth = a.getDimensionPixelSize(R.styleable.ImageProgressView_border_width, DEFAULT_BORDER_WIDTH);
        mBorderColor = a.getColor(R.styleable.ImageProgressView_border_color, DEFAULT_BORDER_COLOR);
        mBorderOverlay = a.getBoolean(R.styleable.ImageProgressView_border_overlay, DEFAULT_BORDER_OVERLAY);
        mDrawAntiClockwise = a.getBoolean(R.styleable.ImageProgressView_draw_anticlockwise, DEFAULT_DRAW_ANTI_CLOCKWISE);
        mFillColor = a.getColor(R.styleable.ImageProgressView_fill_color, DEFAULT_FILL_COLOR);
        mInnrCircleDiammeter = a.getFloat(R.styleable.ImageProgressView_centercircle_diammterer, DEFAULT_INNTER_DAIMMETER_FRACTION);
        mProgressColor = a.getColor(R.styleable.ImageProgressView_progress_color, DEFAULT_PROGRESS_COLOR);
        mBaseStartAngle = a.getFloat(R.styleable.ImageProgressView_progress_startAngle, 0);
        mIconUrl = a.getString(R.styleable.ImageProgressView_imageurl);
        mProgressValue = a.getFloat(R.styleable.ImageProgressView_progressvalue, 0.0f);
        a.recycle();
        init();
    }

    private void init() {
        // init animator
        mValueAnimator = ValueAnimator.ofFloat(0, mProgressValue);
        mValueAnimator.setDuration(DEFAULT_ANIMATION_TIME);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                setValueWithNoAnimation((float) valueAnimator.getAnimatedValue());
            }
        });
        super.setScaleType(SCALE_TYPE);
        mReady = true;

        if (mSetupPending) {
            setup();
            mSetupPending = false;
        }
        mBitmap = getBitmapFromDrawable(getDrawable());
        if (null != mContext && null != mIconUrl && !mIconUrl.isEmpty())
            Picasso.with(mContext).load(mIconUrl).resize(105, 105).centerCrop().into(new IconTarget());
    }

    @Override
    public ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != SCALE_TYPE) {
            throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
        }
    }

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        if (adjustViewBounds) {
            throw new IllegalArgumentException("adjustViewBounds not supported.");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();

        canvas.rotate(mBaseStartAngle, mDrawableRect.centerX(), mDrawableRect.centerY());

        if (mBorderWidth > 0) {
            mBorderPaint.setColor(mBorderColor);
            canvas.drawArc(mBorderRect, 0, 360, false, mBorderPaint);
        }

        mBorderPaint.setColor(mProgressColor);

        float sweetAngle = mProgressValue / 100 * 360;
        canvas.drawArc(mBorderRect, 0, mDrawAntiClockwise ? -sweetAngle : sweetAngle, false, mBorderPaint);

        canvas.restore();

        canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), mDrawableRadius, mBitmapPaint);
        if (mFillColor != Color.TRANSPARENT) {
            canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), mDrawableRadius, mFillPaint);
        }

    }

    public void setValue(float newValue) {
        if (animationState) {

            if (mValueAnimator.isRunning()) {
                mValueAnimator.cancel();
            }

            mValueAnimator.setFloatValues(mProgressValue, newValue);
            mValueAnimator.start();
        } else {
            setValueWithNoAnimation(newValue);
        }

    }

    public void setValueWithNoAnimation(float newValue) {
        mProgressValue = newValue;
        invalidate();
    }

    public void setIconUrl(String iconUrl) {
        this.mIconUrl = iconUrl;
        init();
    }

    public String getIconUrl() {
        return mIconUrl;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        setup();
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        super.setPaddingRelative(start, top, end, bottom);
        setup();
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(@ColorInt int borderColor) {
        if (borderColor == mBorderColor) {
            return;
        }

        mBorderColor = borderColor;
        mBorderPaint.setColor(mBorderColor);
        invalidate();
    }

    public void setBorderProgressColor(@ColorInt int borderColor) {
        if (borderColor == mProgressColor) {
            return;
        }

        mProgressColor = borderColor;
        invalidate();
    }


    /**
     * Return the color drawn behind the circle-shaped drawable.
     *
     * @return The color drawn behind the drawable
     **/
    public int getFillColor() {
        return mFillColor;
    }

    /**
     * Set a color to be drawn behind the circle-shaped drawable. Note that
     * this has no effect if the drawable is opaque or no drawable is set.
     *
     * @param fillColor The color to be drawn behind the drawable
     *                  Fill color support is going to be removed in the future
     */
    public void setFillColor(@ColorInt int fillColor) {
        if (fillColor == mFillColor) {
            return;
        }

        mFillColor = fillColor;
        mFillPaint.setColor(fillColor);
        invalidate();
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        if (borderWidth == mBorderWidth) {
            return;
        }

        mBorderWidth = borderWidth;
        setup();
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            Log.v(TAG, e.getMessage());
            return null;
        }
    }


    public class IconTarget implements Target {

        public IconTarget() {
            // Default constructor
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mBitmap = bitmap;
            setup();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            // Nothing to do here
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            // Nothing to do here
        }
    }

    private void setup() {

        if (!mReady) {
            mSetupPending = true;
            return;
        }

        if (getWidth() == 0 && getHeight() == 0) {
            return;
        }

        if (mBitmap == null) {
            invalidate();
            return;
        }

        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);

        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setStrokeCap(Paint.Cap.ROUND);

        mFillPaint.setStyle(Paint.Style.FILL);
        mFillPaint.setAntiAlias(true);
        mFillPaint.setColor(mFillColor);

        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();

        mBorderRect.set(calculateBounds());

        mDrawableRect.set(mBorderRect);
        if (!mBorderOverlay && mBorderWidth > 0) {
            mDrawableRect.inset(mBorderWidth, mBorderWidth);
        }

        mDrawableRadius = Math.min(mDrawableRect.height() / 2, mDrawableRect.width() / 2);

        if (mInnrCircleDiammeter > 1)
            mInnrCircleDiammeter = 1;

        mDrawableRadius = mDrawableRadius * mInnrCircleDiammeter;
        updateShaderMatrix();
        invalidate();
    }


    private static int getMeasurementSize(int measureSpec, int defaultSize) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.EXACTLY:
                return size;

            case MeasureSpec.AT_MOST:
                return Math.min(defaultSize, size);

            case MeasureSpec.UNSPECIFIED:
            default:
                return defaultSize;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getMeasurementSize(widthMeasureSpec, 600);
        int height = getMeasurementSize(heightMeasureSpec, 600);
        setMeasuredDimension(width, height);
    }

    private RectF calculateBounds() {
        int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        int sideLength = Math.min(availableWidth, availableHeight);

        float left = getPaddingLeft() + (availableWidth - sideLength) / 2f;
        float top = getPaddingTop() + (availableHeight - sideLength) / 2f;

        return new RectF(left + getBorderWidth(), top + getBorderWidth(), left + sideLength - getBorderWidth(), top + sideLength - getBorderWidth());
    }

    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);

        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }

        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mDrawableRect.left, (int) (dy + 0.5f) + mDrawableRect.top);

        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

}

