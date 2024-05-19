package com.example.budgetbuddy.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.budgetbuddy.R;
import com.example.budgetbuddy.currency.CurrencyConverter;
import com.example.budgetbuddy.reminder.ReminderFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;

public class DemoActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        FirebaseApp.initializeApp(this);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    selectedFragment = new DemoHomeFragment();
                } else if (itemId == R.id.nav_statistics) {
                    selectedFragment = new DemoStatisticsFragment();
                } else if (itemId == R.id.nav_reminder) {
                    selectedFragment = new ReminderFragment();
                } else if (itemId == R.id.nav_currency) {
                    startActivity(new Intent(DemoActivity.this, CurrencyConverter.class));
                    return true;
                } else if (itemId == R.id.nav_settings) {
                    selectedFragment = new DemoSettingsFragment();
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                }
                return true;
            }
        });

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
            loadFragment(new DemoHomeFragment());
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBottomNavigation();
    }

    private void updateBottomNavigation() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container_demo);
        if (currentFragment instanceof DemoHomeFragment) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        } else if (currentFragment instanceof DemoStatisticsFragment) {
            bottomNavigationView.setSelectedItemId(R.id.nav_statistics);
        } else if (currentFragment instanceof DemoReminderFragment) {
            bottomNavigationView.setSelectedItemId(R.id.nav_reminder);
        } else if (currentFragment instanceof DemoSettingsFragment) {
            bottomNavigationView.setSelectedItemId(R.id.nav_settings);
        }
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_demo, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
            updateBottomNavigation();
        } else {
            super.onBackPressed();
        }
    }
}