package com.example.budgettracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.budgettracker.adapters.AppFragmentStateAdapter;
import com.example.budgettracker.database.AppDB;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayoutMediator;

/*
 * The main class of the program. Responsibilities:
 *  Initialises a ViewPager2 and a AppFragmentStateAdaptor to handle the swipe view functionality
 *  Fragments are attached to MainActivity- the layout in activity_main.xml is shown behind them
 *  Attaches these to a TabLayout to enable the user to switch between the three tabs without swiping
 *  Title bar is automatically updated to reflect fragment title
 *  Receive new Transactions from the AddFragment and add them to the TransactionViewModel
 *
 *
 */

public class MainActivity extends AppCompatActivity
{
    // Create an instance of the TransactionViewModel

    // Create an instance of the Room Database

    public static AppDB appDB;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);

        // Initialise the TransactionViewModel- all other fragments should use this instance
        TransactionViewModel transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        /*
         window.statusBarColor is deprecated since Android 14
         WindowInsets is used instead to set the status bar colour
         */
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // Set the underlying background to blue, this is drawn over in white by the fragments
            // Also sets the bottom bar to blue :/
            //v.setBackground(ResourcesCompat.getDrawable(getResources(), R.color.budgetBlue, getTheme()));
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Connect to the MaterialToolbar to dynamically change the page title
        MaterialToolbar toolbar = findViewById(R.id.title_bar);
        toolbar.setTitle(R.string.overview);

        // Create the ViewPager and attach an AppFragmentStateAdaptor to it
        ViewPager2 vp = findViewById(R.id.swipePager);
        vp.setAdapter(new AppFragmentStateAdapter(this));

        // Attach the ViewPager to the TabLayout with a TabLayoutMediator
        new TabLayoutMediator(findViewById(R.id.tab_layout), vp,

                // Create a TabConfigurationStrategy to set the text for each tab
                (tab, position) ->
                {
                    switch (position)
                    {
                        case 0:
                            tab.setText("Overview");
                            break;
                        case 1:
                            tab.setText("Add");
                            break;
                        case 2:
                            tab.setText("Transactions");
                            break;
                    }
                }).attach();

        // Update the title bar to the name of the currently-displayed fragment
        vp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback()
        {
            @Override
            public void onPageSelected(int position)
            {
                switch (position)
                {
                    case 0:
                        toolbar.setTitle(R.string.overview);
                        break;
                    case 1:
                        toolbar.setTitle(R.string.add);
                        break;
                    case 2:
                        toolbar.setTitle(R.string.transactions);
                        break;
                }
                super.onPageSelected(position);
            }
        });
    }
        //
        public void settingsButtonPressed (View v)
        {
            Toast toast = Toast.makeText(this, "Settings", Toast.LENGTH_LONG);
            toast.show();
        }

        public void notificationsButtonPressed (View v)
        {
            Toast toast = Toast.makeText(this, "Notifications", Toast.LENGTH_LONG);
            toast.show();
        }
}