package com.example.budgetbuddy;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class AboutUs extends Fragment {

    public AboutUs() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_about_us, container, false);

        TextView messageSentTextView = view.findViewById(R.id.message_sent);

        String boldItalicText = "Welcome to BudgetBuddy!";
        String normalText = "\nOur app has been designed to help you track your expenses and manage your finances in a hassle-free way. We believe that budgeting should not be a daunting task, but rather an easy and effective way to take control of your finances.";
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(boldItalicText);
        int start = builder.length();
        builder.append(normalText);
        int end = builder.length();
        builder.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        messageSentTextView.setText(builder);

        return view;
    }
}
