package com.example.budgetbuddy.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.budgetbuddy.AboutUs;
import com.example.budgetbuddy.LoginActivity;
import com.example.budgetbuddy.R;

public class DemoSettingsFragment extends Fragment {

    private TextView LogInTextView, aboutUs;

    public DemoSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_demo_settings, container, false);

        aboutUs = view.findViewById(R.id.about_us_text);
        LogInTextView = view.findViewById(R.id.log_in);

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_demo, new AboutUs())
                        .addToBackStack(null)
                        .commit();
            }
        });

        LogInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
