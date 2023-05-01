package com.example.workersapp.Utilities;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RatingBar;

import androidx.core.content.ContextCompat;

import com.example.workersapp.R;

public class StarRatingBar extends RatingBar {

    public StarRatingBar( Context context, AttributeSet attrs) {
        super(context, attrs);

        // Set the progress drawable to the custom star drawable
        setProgressDrawable( ContextCompat.getDrawable(context, R.drawable.ic_staryallow));
    }
}
