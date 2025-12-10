package com.example.budgettracker.adapters;


import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker.R;
import com.example.budgettracker.utility.ColorHandler;

import java.util.List;

// Recycler view adapter to support the Color Picker
public class ColorPickerAdapter extends RecyclerView.Adapter<ColorPickerAdapter.ColorViewHolder>
{

    // Interface to provide onClick
    public interface OnItemClickListener
    {
        void onItemClick(int color);
    }

    private final Context context;
    private final List<Integer> colorList;
    private final OnItemClickListener onItemClick;

    private int selectedPosition = -1;  // Nothing selected by default


    public ColorPickerAdapter(Context context, List<Integer> colorList, OnItemClickListener onItemClick)
    {
        this.context = context;
        this.colorList = colorList;
        this.onItemClick = onItemClick;
    }


    // Create the ViewHolder class
    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        // Load the layout for each item
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.colour_picker_item, parent, false);
        return new ColorViewHolder(view);
    }

    // Bind each of the colours to a position in the RecyclerView
    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position)
    {
        int colour = colorList.get(position);

        // The isSelected logic prevents checkmarks remaining after another item is clicked
        // It passes "position == selectedPosition" as a boolean, which is only true if
        // another item has not been clicked
        holder.bind(colour, position == selectedPosition);
    }

    @Override
    public int getItemCount()
    {
        return colorList.size();
    }

    public class ColorViewHolder extends RecyclerView.ViewHolder
    {
        private final View colorView;   // The background colour block
        private final View checkmarkView;   // The checkmark icon

        public ColorViewHolder(@NonNull View itemView)
        {
            super(itemView);
            colorView = itemView.findViewById(R.id.colorItem);
            checkmarkView = itemView.findViewById(R.id.checkmark);
        }

        public void bind(final int colour, boolean isSelected)
        {
            @ColorInt
            int backgroundColour = ColorHandler.getColorARGB(context, colour);

            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.colour_square);
            if (drawable != null)
            {

                // Make the drawable mutable to differentiate it
                drawable = drawable.mutate();

                // Use a PorterDuff color filter to change the square's colour
                drawable.setColorFilter(new PorterDuffColorFilter(backgroundColour, PorterDuff.Mode.SRC_IN));
                colorView.setBackground(drawable);
            }

            // Adapt the colour of the checkmark icon depending on the luminance of the background
            drawable = ContextCompat.getDrawable(context, R.drawable.checkmark);
            if (drawable != null)
            {

                // Make the drawable mutable to differentiate it
                drawable = drawable.mutate();

                // Adapt the colour of the checkmark icon depending on the luminance of the background


                drawable.setColorFilter(new PorterDuffColorFilter(ColorHandler.resolveForegroundColor(context, backgroundColour), PorterDuff.Mode.SRC_IN));
                checkmarkView.setBackground(drawable);
            }

            // If this item is selected, display a checkmark- if not, hide it completely
            checkmarkView.setVisibility(isSelected ? View.VISIBLE : View.GONE);

            // Set the onClick listener
            itemView.setOnClickListener(v ->
            {
                int previousPosition = selectedPosition;
                selectedPosition = getAbsoluteAdapterPosition();

                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);

                if (onItemClick != null)
                {
                    onItemClick.onItemClick(colour);
                }
            });
        }
    }
}
