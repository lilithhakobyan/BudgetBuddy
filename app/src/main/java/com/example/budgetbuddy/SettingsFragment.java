package com.example.budgetbuddy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.budgetbuddy.currency.CurrencyConverter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SettingsFragment extends Fragment {

    private static final int RESULT_LOAD_IMG = 1;
    private static final int RESULT_OK = -1;
    private TextView emailTextView, signOutTextView, currencyTextView;
    private ImageView profilePicture;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        mAuth = FirebaseAuth.getInstance();

        emailTextView = view.findViewById(R.id.email_text);
        signOutTextView = view.findViewById(R.id.log_out);
        currencyTextView = view.findViewById(R.id.currency_tv);


        currencyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), CurrencyConverter.class);
                startActivity(intent);
            }
        });


        signOutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        String userEmail = "Email: " + getUserEmail();
        emailTextView.setText(userEmail);

        return view;
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        getActivity().finish();
    }

    private String getUserEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getEmail();
        } else {
            return "User not logged in";
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            profilePicture.setImageURI(selectedImageUri);
        }
    }
}
