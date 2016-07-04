package com.ln.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ln.app.MainApplication;

/**
 * Created by Nhahv on 7/2/2016.
 * <></>
 */
public class IconTextView extends TextView {

    public IconTextView(Context context) {
        super(context);
        setTypefaces(context);
    }

    public IconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypefaces(context);
    }

    public IconTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypefaces(context);
    }

    private void setTypefaces(Context context) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), MainApplication.FONT);
        setTypeface(typeface);
    }
}
