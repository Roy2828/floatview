package com.feiyu.floatingview.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.feiyu.floatingview.R;


/**
 * @author hwj 2018/9/26
 * @Package com.transfar.peaceinn.ui.module.help.widget.ShadowLayout
 * @Title: ShadowLayout
 * @Description: (自定义阴影控件 ， 用法 ： 包裹任意view ， 设置阴影和偏移量)
 * Copyright (c) 浙江传化陆鲸科技有限公司版权所有
 * Create DateTime: 2018/9/26
 */
public class ShadowLayout extends FrameLayout {

    private int mShadowColor;
    private float mShadowRadius;
    private float mCornerRadius;
    private float mDx;
    private float mDy;

    private boolean mInvalidateShadowOnSizeChanged = true;
    private boolean mForceInvalidateShadow = false;
    private boolean mIsOval;

    public ShadowLayout(Context context) {
        super(context);
        initView(context, null);
    }

    public ShadowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ShadowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return 0;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return 0;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0 && (getBackground() == null || mInvalidateShadowOnSizeChanged || mForceInvalidateShadow)) {
            mForceInvalidateShadow = false;
            setBackgroundCompat(w, h);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mForceInvalidateShadow) {
            mForceInvalidateShadow = false;
            setBackgroundCompat(right - left, bottom - top);
        }
    }

    public void setInvalidateShadowOnSizeChanged(boolean invalidateShadowOnSizeChanged) {
        mInvalidateShadowOnSizeChanged = invalidateShadowOnSizeChanged;
    }

    public void invalidateShadow() {
        mForceInvalidateShadow = true;
        requestLayout();
        invalidate();
    }

    private void initView(Context context, AttributeSet attrs) {
        initAttributes(context, attrs);

        int xPadding = (int) (mShadowRadius + Math.abs(mDx));
        int yPadding = (int) (mShadowRadius + Math.abs(mDy));
        setPadding(xPadding, yPadding, xPadding, yPadding);
    }

    @SuppressWarnings("deprecation")
    private void setBackgroundCompat(int w, int h) {
        Bitmap bitmap = createShadowBitmap(w, h, mCornerRadius, mShadowRadius, mDx, mDy, mShadowColor, Color.TRANSPARENT);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(drawable);
        } else {
            setBackground(drawable);
        }
    }


    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray attr = getTypedArray(context, attrs, R.styleable.ShadowLayout);
        if (attr == null) {
            return;
        }

        try {
                mCornerRadius = attr.getDimension(R.styleable.ShadowLayout_sl_cornerRadius, getResources().getDimension(R.dimen.default_corner_radius));
            mShadowRadius = attr.getDimension(R.styleable.ShadowLayout_sl_shadowRadius, getResources().getDimension(R.dimen.default_shadow_radius));
            mDx = attr.getDimension(R.styleable.ShadowLayout_sl_dx, 0);
            mDy = attr.getDimension(R.styleable.ShadowLayout_sl_dy, 0);
            mIsOval = attr.getBoolean(R.styleable.ShadowLayout_sl_oval, false);
            mShadowColor = attr.getColor(R.styleable.ShadowLayout_sl_shadowColor, getResources().getColor(R.color.default_shadow_color));
        } finally {
            attr.recycle();
        }
    }

    private TypedArray getTypedArray(Context context, AttributeSet attributeSet, int[] attr) {
        return context.obtainStyledAttributes(attributeSet, attr, 0, 0);
    }

    private Bitmap createShadowBitmap(int shadowWidth, int shadowHeight, float cornerRadius, float shadowRadius,
                                      float dx, float dy, int shadowColor, int fillColor) {

        Bitmap output = Bitmap.createBitmap(shadowWidth, shadowHeight, Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(output);

        RectF shadowRect = new RectF(
                shadowRadius,
                shadowRadius,
                shadowWidth - shadowRadius,
                shadowHeight - shadowRadius);

        if (dy > 0) {
            shadowRect.top += dy;
            shadowRect.bottom -= dy;
        } else if (dy < 0) {
            shadowRect.top += Math.abs(dy);
            shadowRect.bottom -= Math.abs(dy);
        }

        if (dx > 0) {
            shadowRect.left += dx;
            shadowRect.right -= dx;
        } else if (dx < 0) {
            shadowRect.left += Math.abs(dx);
            shadowRect.right -= Math.abs(dx);
        }

        Paint shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setColor(fillColor);
        shadowPaint.setStyle(Paint.Style.FILL);

        if (!isInEditMode()) {
            shadowPaint.setShadowLayer(shadowRadius, mDx, mDy, shadowColor);
        }

        if (mIsOval) {
            canvas.drawCircle(shadowRect.centerX(), shadowRect.centerY(), Math.min(shadowRect.width(), shadowRect.height()) / 2, shadowPaint);
        } else {
            canvas.drawRoundRect(shadowRect, cornerRadius, cornerRadius, shadowPaint);
        }
        return output;
    }

}
