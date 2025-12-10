package com.example.budgettracker.utility;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// Provides the item decoration to arrange the colour list into a grid on the colour picker
public class ColorGridDecoration extends RecyclerView.ItemDecoration
{
    private final int spanCount;
    private final int spacing;
    private final boolean edgeIncluded;

    public ColorGridDecoration(int spanCount, int spacing, boolean edgeIncluded)
    {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.edgeIncluded = edgeIncluded;
    }

    // Provide the item offsets to arrange each colour block clearly
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state)
    {
        int position = parent.getChildAdapterPosition(view);
        int column = position % spanCount;

        if (edgeIncluded)
        {
            outRect.left = spacing - column * spacing / spanCount;
            outRect.right = (column + 1) * spacing / spanCount;

            if (position < spanCount)
            {
                outRect.top = spacing;
            }
            outRect.bottom = spacing;
        } else
        {
            outRect.left = column * spacing / spanCount;
            outRect.right = spacing - (column + 1) * spacing / spanCount;

            if (position >= spanCount)
            {
                outRect.top = spacing;
            }
        }
    }
}
