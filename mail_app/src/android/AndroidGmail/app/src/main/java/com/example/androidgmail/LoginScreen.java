package com.example.androidgmail;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import android.widget.Button;
import android.widget.EditText;

import com.example.androidgmail.viewmodels.UserViewModel;
import java.util.HashMap;
import java.util.Map;

public class LoginScreen extends AppCompatActivity {

    private UserViewModel userViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        TextView signupLink = findViewById(R.id.signupLink);
        signupLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginScreen.this, SignUpScreen.class);
            startActivity(intent);
        });


        EditText emailField = findViewById(R.id.emailEditText);
        EditText passwordField = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", email);
            credentials.put("password", password);

            userViewModel.loginAndInit(credentials).observe(this, ok -> {
                if (ok.equals("Login Successful")) {
                    Toast.makeText(this, ok, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, ok, Toast.LENGTH_LONG).show();
                }
            });
        });

    }
}