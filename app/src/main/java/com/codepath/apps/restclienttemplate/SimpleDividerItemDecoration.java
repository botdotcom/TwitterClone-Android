package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// referred from: https://gist.github.com/polbins/e37206fbc444207c0e92
public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable divider;

    public SimpleDividerItemDecoration(Context context) {
        divider = context.getResources().getDrawable(R.drawable.line_divider);
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + parent.getPaddingBottom() + 30;
            int bottom = top + divider.getIntrinsicHeight();

            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }
}
