package com.example.budgetbuddy.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.budgetbuddy.HomeFragment;
import com.example.budgetbuddy.R;
import com.example.budgetbuddy.ReminderFragment;
import com.example.budgetbuddy.SettingsFragment;
import com.example.budgetbuddy.StatisticsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DemoActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);


        bottomNavigationView = findViewById(R.id.bottom_navigation);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_demo, new DemoHomeFragment())
                    .commit();
        }


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home){
                    loadFragment(new DemoHomeFragment(), false);
                } else if(itemId == R.id.nav_statistics ){
                    loadFragment(new StatisticsFragment(), false);
                } else if(itemId == R.id.nav_reminder){
                    loadFragment(new DemoReminderFragment(), false);
                } else {
                    loadFragment(new SettingsFragment(), false);
                }

                return true;
            }
        });
    }

    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container_demo, fragment);

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction.commit();
    }

}