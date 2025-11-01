package com.example.budgettracker;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayoutMediator;

/*
 * The main class of the program
 * Initialises a ViewPager2 and a AppFragmentStateAdaptor to handle the swipe view functionality
 * Fragments are attached to MainActivity- the layout in activity_main.xml is shown behind them
 * Attaches these to a TabLayout to enable the user to switch between the three tabs without swiping
 * Title bar is automatically updated to reflect fragment title
 */

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connects a new TextView with the id "title" in activity_main.xml
        TextView title = findViewById(R.id.title);
        title.setText(R.string.overview);

        // Creates the ViewPager and attaches an AppFragmentStateAdaptor to it
        ViewPager2 vp = findViewById(R.id.swipePager);
        vp.setAdapter(new AppFragmentStateAdapter(this));

        // Attach the ViewPager to the TabLayout with a TabLayoutMediator
        new TabLayoutMediator(findViewById(R.id.tab_layout), vp,

                // Creates a TabConfigurationStrategy to set the text for each tab
                (tab, position) -> {
                    switch (position) {
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
                }
        ).attach();

        // Update the title bar to the title of the currently-displayed fragment
        vp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position)
                {
                    case 0:
                        title.setText(R.string.overview);
                        break;
                    case 1:
                        title.setText(R.string.add);
                        break;
                    case 2:
                        title.setText(R.string.transactions);
                        break;
                }
                super.onPageSelected(position);
            }
        });
    }
}
