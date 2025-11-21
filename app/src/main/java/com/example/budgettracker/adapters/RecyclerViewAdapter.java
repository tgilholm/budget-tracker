package com.example.budgettracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker.R;
import com.example.budgettracker.Transaction;
import com.example.budgettracker.enums.TransactionType;
import com.example.budgettracker.utility.ColorHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// The adapter for the RecyclerView in OverviewFragment
// Extends ListAdapter to improve performance- does not need to rewrite entire list on update
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
{
    // The layout for the items in the RecyclerView
    @LayoutRes
    protected final int resource;

    // Hold a list of transactions
    protected final List<Transaction> _transactions = new ArrayList<>();

    public RecyclerViewAdapter(List<Transaction> transactions, @LayoutRes int resource)
    {
        this._transactions.addAll(transactions);    // Instantiate list with transactions
        this.resource = resource;                   // Instantiate layout
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        // Inflate the layout for each item in the RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Transaction transaction = _transactions.get(position);    // Get the transaction at the current position
        holder.bind(transaction);   // Set the data in the view elements
    }

    @Override
    public int getItemCount()
    {
        return _transactions.size();
    }

    // Update a transaction without resetting the entire RecyclerView using DiffUtil
    public void updateTransactions(List<Transaction> transactions)
    {
        // Compare the list held in the Adapter with the new list
        final TransactionDiffCallback diffCallback = new TransactionDiffCallback(this._transactions, transactions);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);    // Calculate the result of the operation


        this._transactions.clear();                         // Clear the list held in the Adapter
        this._transactions.addAll(transactions);            // Add the new list to the Adapter
        diffResult.dispatchUpdatesTo(this);        // Tells the RecyclerView to update the display
    }


    // ViewHolder for the RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView textCategory;
        private final TextView textDateTime;
        private final TextView textAmount;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            // Find the textViews from the layout
            textDateTime = itemView.findViewById(R.id.textDateTime);
            textCategory = itemView.findViewById(R.id.textCategory);
            textAmount = itemView.findViewById(R.id.textAmount);
        }

        // Set layout elements to the transaction data
        public void bind(Transaction transaction)
        {
            // Get the category and amount fields
            textCategory.setText(transaction.getCategory());
            textAmount.setText(String.format(Locale.getDefault(), "£%.2f", transaction.getAmount()));

            // For positive (income) transactions, set the color to green
            if (transaction.getType() == TransactionType.INCOMING)
            {
                textAmount.setTextColor(ColorHandler.resolveColorID(itemView.getContext(), R.color.brightGreen));
            } else
            {
                // For negative transactions set the color to red and prepend a '-'
                String negatedString = "-" + textAmount.getText();
                textAmount.setText(negatedString);
                textAmount.setTextColor(ColorHandler.resolveColorID(itemView.getContext(), R.color.brightRed));
            }
            textDateTime.setText(transaction.getDateTimeString());
        }
    }
}