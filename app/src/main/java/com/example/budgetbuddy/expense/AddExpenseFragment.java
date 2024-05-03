package com.example.budgetbuddy.expense;

import static com.example.budgetbuddy.currency.CurrencyUtils2.fetchCurrencies;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.budgetbuddy.R;
import com.example.budgetbuddy.SharedViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AddExpenseFragment extends Fragment {

    public interface CategorySelectionListener {
        void onCategorySelected(String categoryName, int categoryIcon);
    }

    private TextView selectCategoryTextView;
    private ImageView selectIconButton;
    private ExpenseCategory selectedCategory;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private EditText descEditText;
    private EditText amountEditText;
    private String selectedCurrency;
    private Button save;
    private ImageView close;
    private SharedViewModel sharedViewModel;

    public AddExpenseFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_expense, container, false);

        Spinner spinner = view.findViewById(R.id.currency_spinner);

        List<String> currencies = fetchCurrencies();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCurrency = currencies.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCurrency = null;
            }
        });

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout expenseCategoryLayout = view.findViewById(R.id.expense_category);
        selectCategoryTextView = view.findViewById(R.id.select_category);
        selectIconButton = view.findViewById(R.id.select_icon);
        save = view.findViewById(R.id.button_save_expense);
        descEditText = view.findViewById(R.id.desc_edit_text);
        amountEditText = view.findViewById(R.id.amount_edit_text);
        TextView amountTextView = view.findViewById(R.id.amount_text_view);
        close = view.findViewById(R.id.close_expense);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setMessage("Are you sure you want to discard the draft?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (isAdded()) {
                                    getParentFragmentManager().popBackStack();
                                }
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

        expenseCategoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryDialog();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExpense();
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
                        selectedCategory = ExpenseCategory.values()[which];
                        updateSelectedCategory(selectedCategory.getCategoryName(), selectedCategory.getCategoryIcon());
                    }
                })
                .setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String[] getCategoryNamesArray() {
        String[] categoryNames = new String[ExpenseCategory.values().length];
        for (int i = 0; i < ExpenseCategory.values().length; i++) {
            categoryNames[i] = ExpenseCategory.values()[i].getCategoryName();
        }
        return categoryNames;
    }

    public void updateSelectedCategory(String categoryName, int categoryIcon) {
        selectCategoryTextView.setText(categoryName);
        selectIconButton.setImageResource(categoryIcon);
    }

    private void saveExpense() {
        String amountStr = amountEditText.getText().toString().trim();
        String description = descEditText.getText().toString().trim();

        if (amountStr.isEmpty()) {
            amountEditText.setError("Amount is required");
            amountEditText.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            descEditText.setError("Description is required");
            descEditText.requestFocus();
            return;
        }

        // Check if a category is selected
        if (selectedCategory == null) {
            Toast.makeText(requireContext(), "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCurrency == null || selectedCurrency.equals("Select currency")) {
            Toast.makeText(requireContext(), "Please select a currency", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        Expense expense = new Expense(amount, selectedCategory.getCategoryName(), description, selectedCurrency);
        db.collection("expense")
                .add(expense)
                .addOnSuccessListener(documentReference -> {

                    List<Expense> updatedExpenseList = sharedViewModel.getExpenseList().getValue();
                    updatedExpenseList.add(expense);
                    sharedViewModel.setExpenseList(updatedExpenseList);

                    Log.i("AddExpenseFragment", "Expense saved successfully");
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "Expense saved successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    String errorMessage = "Failed to save expense: " + e.getMessage();
                    Log.e("AddExpenseFragment", errorMessage, e);
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
