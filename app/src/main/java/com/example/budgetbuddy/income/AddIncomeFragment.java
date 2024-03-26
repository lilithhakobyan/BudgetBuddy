package com.example.budgetbuddy.income;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetbuddy.R;
import com.example.budgetbuddy.adapter.CategoryAdapter;

public class AddIncomeFragment extends Fragment {

    private TextView selectCategoryTextView;
    private ImageView selectIconButton;
    private TextView incomeCategoryTextView;
    private ImageView incomeCategoryIconView;

    private String selectedCategoryName;
    private int selectedCategoryIcon;

    public AddIncomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_income, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout incomeCategoryLayout = view.findViewById(R.id.income_category);
        incomeCategoryTextView = view.findViewById(R.id.category_name);
        incomeCategoryIconView = view.findViewById(R.id.category_icon);
        selectCategoryTextView = view.findViewById(R.id.select_category);
        selectIconButton = view.findViewById(R.id.select_icon);



        incomeCategoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new CategoryFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        ImageView imageView = view.findViewById(R.id.close_income);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setMessage("Are you sure you want to discard the draft?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getParentFragmentManager().popBackStack();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}
