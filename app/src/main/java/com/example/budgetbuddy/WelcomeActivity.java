package com.example.budgetbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.budgetbuddy.demo.DemoActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class WelcomeActivity extends AppCompatActivity {

    private Button login;
    private Button skip;
    private Button testMode;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mAuth = FirebaseAuth.getInstance();

        login = findViewById(R.id.log_in);
        skip = findViewById(R.id.skip_for_now);
        testMode = findViewById(R.id.test_mode);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, DemoActivity.class));
            }
        });

        testMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performTestLogin();
            }
        });
    }

    private void performTestLogin() {
        String testEmail = "sictst1@gmail.com";
        String testPassword = "Samsung2023";

        mAuth.signInWithEmailAndPassword(testEmail, testPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(WelcomeActivity.this, "Logged in as Test User", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(WelcomeActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}