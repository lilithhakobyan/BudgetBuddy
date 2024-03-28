package com.example.budgetbuddy.income;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.budgetbuddy.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AddIncomeFragment extends Fragment {

    private TextView selectCategoryTextView;
    private ImageView selectIconButton;
    private IncomeCategory selectedCategory;
    private FirebaseFirestore db;
    private EditText descEditText;
    private EditText amountEditText;
    private String selectedCurrency;
    private Button save;
    private ImageView close;



    public AddIncomeFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_income, container, false);

        Spinner spinner = view.findViewById(R.id.currency_spinner);


        List<String> currencies = new ArrayList<>();

        currencies.add("Select currency");
        currencies.add("USD");
        currencies.add("EUR");
        currencies.add("GBP");
        currencies.add("JPY");

        // Create an ArrayAdapter with the list of currencies
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to the spinner
        spinner.setAdapter(adapter);

        // Set a listener to handle item selection
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // If the hint is selected, do nothing
                if (position == 0) {
                    // Optionally, you can show a toast or perform some action to prompt the user to select a valid currency
                } else {
                    // Handle selected currency
                    String selectedCurrency = currencies.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle nothing selected
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout incomeCategoryLayout = view.findViewById(R.id.income_category);
        selectCategoryTextView = view.findViewById(R.id.select_category);
        selectIconButton = view.findViewById(R.id.select_icon);
        save = view.findViewById(R.id.button);
        descEditText = view.findViewById(R.id.desc_edit_text);
        amountEditText = view.findViewById(R.id.amount_edit_text);
        TextView amountTextView = view.findViewById(R.id.amount_text_view);
        close = view.findViewById(R.id.close_income);

        close.setOnClickListener(new View.OnClickListener() {
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

        incomeCategoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryDialog();
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveIncome();
                getParentFragmentManager().popBackStack();

            }
        });
    }

    private void showCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Category")
                .setItems(getCategoryNamesArray(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedCategory = IncomeCategory.values()[which];
                        updateSelectedCategory(selectedCategory.getCategoryName(), selectedCategory.getCategoryIcon());
                    }
                })
                .setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String[] getCategoryNamesArray() {
        String[] categoryNames = new String[IncomeCategory.values().length];
        for (int i = 0; i < IncomeCategory.values().length; i++) {
            categoryNames[i] = IncomeCategory.values()[i].getCategoryName();
        }
        return categoryNames;
    }

    public void updateSelectedCategory(String categoryName, int categoryIcon) {
        selectCategoryTextView.setText(categoryName);
        selectIconButton.setImageResource(categoryIcon);
    }

    public interface CategorySelectionListener {
        void onCategorySelected(String categoryName, int categoryIcon);
    }



    private void saveIncome() {
        String description = descEditText.getText().toString();
        String amountText = amountEditText.getText().toString();

        if (amountText.isEmpty()) {
            Toast.makeText(requireContext(), "Amount cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountText);

        String category = selectedCategory != null ? selectedCategory.getCategoryName() : "";

        String formattedIncomeDetails = String.format("Description: %s\nAmount: $%.2f\nCategory: %s\nCurrency: %s",
                description, amount, category, selectedCurrency);


        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirm Income Details")
                .setMessage(formattedIncomeDetails)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Income income = new Income(amount, category, description);

                        if (isAdded()) {
                            db.collection("income")
                                    .add(income)
                                    .addOnSuccessListener(documentReference -> {
                                        if (isAdded()) {
                                            Toast.makeText(requireContext(), "Income saved successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.e("AddIncomeFragment", "Fragment not attached to an activity");
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        if (isAdded()) {
                                            Toast.makeText(requireContext(), "Failed to save income: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.e("AddIncomeFragment", "Fragment not attached to an activity");
                                        }
                                    });
                        } else {
                            Log.e("AddIncomeFragment", "Fragment not attached to an activity");
                        }


                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

}
