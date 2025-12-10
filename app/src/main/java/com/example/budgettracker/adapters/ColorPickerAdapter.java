package com.example.budgettracker.adapters;


import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker.R;

import java.util.List;

// Recycler view adapter to support the Color Picker
public class ColorPickerAdapter extends RecyclerView.Adapter<ColorPickerAdapter.ColorViewHolder>{

    // Interface to provide onClick
    public interface OnItemClickListener {
        void onItemClick(int color);
    }

    private Context context;
    private List<Integer> colorList;
    private final OnItemClickListener onItemClick;


    public ColorPickerAdapter(Context context, List<Integer> colorList, OnItemClickListener onItemClick)
    {
        this.context = context;
        this.colorList = colorList;
        this.onItemClick = onItemClick;
    }


    // Create the ViewHolder class
    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Load the layout for each item
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.colour_picker_item, parent, false);
        return new ColorViewHolder(view);
    }

    // Bind each of the colours to a position in the RecyclerView
    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        int colour = colorList.get(position);
        holder.bind(colour);
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    public class ColorViewHolder extends RecyclerView.ViewHolder
    {
        private final View colorView;

        public ColorViewHolder(@NonNull View itemView) {
            super(itemView);
            colorView = itemView.findViewById(R.id.colorItem);
        }

        public void bind(final int colour)
        {
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.colour_square);
            if (drawable != null)
            {

                // Make the drawable mutable to differentiate it
                drawable = drawable.mutate();

                // Use a PorterDuff color filter to change the square's colour
                drawable.setColorFilter(new PorterDuffColorFilter(colour, PorterDuff.Mode.SRC_IN));
                colorView.setBackground(drawable);
            }

            // Set the onClick listener
            itemView.setOnClickListener(v -> {
                if (onItemClick != null) {
                    onItemClick.onItemClick(colour);
                }
            });
        }
    }
}
