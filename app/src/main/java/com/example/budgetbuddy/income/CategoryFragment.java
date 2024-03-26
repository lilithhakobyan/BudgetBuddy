package com.example.budgetbuddy.income;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.graphics.Color;
import androidx.fragment.app.FragmentTransaction;

import com.example.budgetbuddy.R;
import com.example.budgetbuddy.adapter.CategoryAdapter;

public class CategoryFragment extends Fragment {

    private GridView gridView;
    private CategoryAdapter categoryAdapter;

    private ListView categoryListView;
    private ImageView close;
    private Button okButton;

    private String[] categoryNames = {"Salary", "Category 2", "Category 3"};
    private int[] categoryIcons = {R.drawable.salary, R.drawable.select_icon, R.drawable.img};
    private int selectedItemPosition = -1;
    private OnCategorySelectedListener categorySelectedListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        categoryListView = view.findViewById(R.id.category_list);
        close = view.findViewById(R.id.close_category);
        okButton = view.findViewById(R.id.button_ok);

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


                if (position == selectedItemPosition) {
                    convertView.setBackgroundColor(0x99CCCCCC);
                } else {
                    convertView.setBackgroundColor(Color.TRANSPARENT);
                }

                return convertView;
            }
        };

        categoryListView.setAdapter(adapter);

        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItemPosition = position;
                adapter.notifyDataSetChanged();

                String selectedCategory = categoryNames[position];
                int selectedIcon = categoryIcons[position]; // Get the corresponding icon

                if (categorySelectedListener != null) {
                    categorySelectedListener.onCategorySelected(selectedCategory, selectedIcon); // Pass both the category name and icon
                }
            }
        });



        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getParentFragmentManager() != null) {
                    getParentFragmentManager().popBackStack();
                }
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedItemPosition != -1) {
                    String selectedCategory = categoryNames[selectedItemPosition];
                    int selectedIcon = categoryIcons[selectedItemPosition];
                    Log.d("CategoryFragment", "Selected category: " + selectedCategory);
                    if (getParentFragment() instanceof OnCategorySelectedListener) {
                        ((OnCategorySelectedListener) getParentFragment()).onCategorySelected(selectedCategory, selectedIcon);
                    }
                    getParentFragmentManager().popBackStack();
                }
            }
        });


        return view;
    }

    public interface OnCategorySelectedListener {
        void onCategorySelected(String categoryName, int categoryIcon);
    }
}
