package com.android.personal.usersystem.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

public class SquareTextureView extends TextureView {
    public SquareTextureView(Context context) {
        super(context);
    }

    public SquareTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SquareTextureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}
