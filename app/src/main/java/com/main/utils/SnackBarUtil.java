package com.main.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.google.android.material.snackbar.Snackbar;

public final class SnackBarUtil {

    private final static int DEFAULT_SUCCESS_COLOR = 0xFF4CAF50;
    private final static int  DEFAULT_FAILED_COLOR = 0xFFF44336;
    private final static int  DEFAULT_FONT_COLOR = 0xFFFFFFFF;

    public static void snackbar(Context context,String message, boolean isSuccess) {
        if (context instanceof Activity) {
            View rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
                    .setBackgroundTint(isSuccess ? DEFAULT_SUCCESS_COLOR : DEFAULT_FAILED_COLOR)
                    .setTextColor(DEFAULT_FONT_COLOR);

            View snackbarView = snackbar.getView();
            ViewGroup.LayoutParams lp = snackbarView.getLayoutParams();

            if (lp instanceof FrameLayout.LayoutParams) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) lp;
                params.gravity = Gravity.TOP;
                params.topMargin = 120;
                snackbarView.setLayoutParams(params);
            }

            snackbar.show();
        }
        return;
    }
}
