package com.main.helper;

import android.content.Context;
import android.widget.ArrayAdapter;

public final class DropDownHelper {

    public static ArrayAdapter<CharSequence> createSpinnerAdapter(Context context, int textArrayResId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context,
                textArrayResId,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }
}
