package com.example.budgettracker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

// Extends the FragmentStateAdapter class to create instances of each fragment
public class AppFragmentStateAdapter extends FragmentStateAdapter {

    // Inherited constructor from parent class, calls "super" to invoke parent constructor
    public AppFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity)
    {
        super(fragmentActivity);
    }

    // Specify how many fragments are in the program, currently three:
    // Overview, Add, and Transactions
    @Override
    public int getItemCount() {
        return 3;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
        // Switch to select which of the fragments to return
        switch (position) {
            case 2:
                return new TransactionsFragment();
            case 1:
                return new AddFragment();
            default: // By default, the "overview" window will open
                return new OverviewFragment();
        }
    }

}
