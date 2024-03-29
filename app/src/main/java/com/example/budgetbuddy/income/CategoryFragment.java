package com.example.budgetbuddy.income;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.example.budgetbuddy.income.AddIncomeFragment;

public class CategoryFragment extends Fragment implements AddIncomeFragment.CategorySelectionListener {

    private ListView categoryListView;
    private ImageView close;
    private Button saveButton;

    private String[] categoryNames = {"Salary", "Other"};
    private int[] categoryIcons = {R.drawable.salary, R.drawable.select_icon};
    private int selectedItemPosition = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        categoryListView = view.findViewById(R.id.category_list);
        close = view.findViewById(R.id.close_category);
        saveButton = view.findViewById(R.id.button_save);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.category_list, categoryNames) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                ViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_list, parent, false);
                    holder = new ViewHolder();
                    holder.iconImageView = convertView.findViewById(R.id.category_icon);
                    holder.nameTextView = convertView.findViewById(R.id.category_name);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.iconImageView.setImageResource(categoryIcons[position]);
                holder.nameTextView.setText(categoryNames[position]);

                convertView.setBackgroundColor(selectedItemPosition == position ? 0x99CCCCCC : Color.TRANSPARENT);

                return convertView;
            }
        };

        categoryListView.setAdapter(adapter);

        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItemPosition = position;
                adapter.notifyDataSetChanged();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedItemPosition != -1) {
                    String selectedCategory = categoryNames[selectedItemPosition];
                    int selectedIcon = categoryIcons[selectedItemPosition];
                    Log.d("CategoryFragment", "Selected category: " + selectedCategory);

                    onCategorySelected(selectedCategory, selectedIcon);

                    getParentFragmentManager().popBackStack();
                }
            }
        });

        return view;
    }

    private static class ViewHolder {
        ImageView iconImageView;
        TextView nameTextView;
    }

    @Override
    public void onCategorySelected(String categoryName, int categoryIcon) {
        if (getParentFragment() instanceof AddIncomeFragment) {
            ((AddIncomeFragment) getParentFragment()).updateSelectedCategory(categoryName, categoryIcon);
        }
    }
}
