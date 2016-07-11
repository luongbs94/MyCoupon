package com.ln.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ln.app.MainApplication;
import com.ln.model.FontCache;

/**
 * Created by Nhahv on 7/2/2016.
 * <></>
 */
public class IconTextView extends TextView {

    public IconTextView(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public IconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public IconTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }


    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface(MainApplication.FONT, context);
        setTypeface(customFont);
    }

}
