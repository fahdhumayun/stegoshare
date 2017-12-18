// By Fahd Humayun
// CustomTextView (Class) - Used for custom font in the application.

package com.example.fahd.stegoshare;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by Fahd on 12/15/2017.
 */

public class CustomTextView extends android.support.v7.widget.AppCompatTextView {

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/CenturyGothic.ttf"));
    }
}
