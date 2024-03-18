package com.example.budgetbuddy.income;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.budgetbuddy.R;

public class CategoryFragment extends Fragment {

    private ListView categoryListView;
    private String[] categoryNames = {"Salary", "Category 2", "Category 3"};
    private int[] categoryIcons = {R.drawable.salary, R.drawable.select_icon, R.drawable.img};
    private int selectedItemPosition = -1; // Track the currently selected item position

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        categoryListView = view.findViewById(R.id.category_list);

        // Create custom adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.category_list, categoryNames) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_list, parent, false);
                }

                ImageView iconImageView = convertView.findViewById(R.id.category_icon);
                TextView nameTextView = convertView.findViewById(R.id.category_name);

                iconImageView.setImageResource(categoryIcons[position]);
                nameTextView.setText(categoryNames[position]);

                // Highlight the selected item
                if (position == selectedItemPosition) {
                    convertView.setBackgroundColor(0x99CCCCCC); // Set the transparent color using hex value (default is 60% transparent)
                } else {
                    convertView.setBackgroundColor(android.R.color.transparent); // Set the background color to transparent
                }

                return convertView;
            }
        };

        categoryListView.setAdapter(adapter);

        // Add OnItemClickListener to the ListView
        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Update the selected item position
                selectedItemPosition = position;
                // Update the ListView to reflect the selected item
                adapter.notifyDataSetChanged();
                // Handle item selection here
                String selectedCategory = categoryNames[position];
                // Optionally, you can perform any action based on the selected category here
            }
        });

        Button okButton = view.findViewById(R.id.button_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedItemPosition != -1) {
                    // If a category is selected
                    String selectedCategory = categoryNames[selectedItemPosition];
                    // Notify the listener (parent fragment) about the selected category
                    if (getParentFragment() instanceof OnCategorySelectedListener) {
                        ((OnCategorySelectedListener) getParentFragment()).onCategorySelected(selectedCategory);
                    }
                }

            }
        });

        return view;
    }

    // Define an interface to communicate with the parent fragment
    public interface OnCategorySelectedListener {
        void onCategorySelected(String categoryName);
    }

}
