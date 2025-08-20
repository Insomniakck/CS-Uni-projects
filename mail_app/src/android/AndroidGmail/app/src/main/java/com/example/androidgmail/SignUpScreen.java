package com.example.androidgmail;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidgmail.entities.User;
import com.example.androidgmail.viewmodels.UserViewModel;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Locale;

public class SignUpScreen extends AppCompatActivity {
    private ImageView profileImageView;
    private Uri selectedImageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.sign_up_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        profileImageView = findViewById(R.id.profileImageView);

        profileImageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        TextView loginLink = findViewById(R.id.loginLink);
        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(SignUpScreen.this, LoginScreen.class));
        });

        Button signUpButton = findViewById(R.id.signUpButton);
        EditText firstNameField = findViewById(R.id.firstNameEditText);
        EditText lastNameField = findViewById(R.id.lastNameEditText);
        EditText birthdateField = findViewById(R.id.birthdateEditText);
        EditText emailField = findViewById(R.id.createEmailEditText);
        EditText passwordField = findViewById(R.id.createPasswordEditText);
        EditText confirmPasswordField = findViewById(R.id.confirmPasswordEditText);

        birthdateField.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(SignUpScreen.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Format as DD/MM/YYYY
                        String formattedDate = String.format(Locale.US, "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                        birthdateField.setText(formattedDate);
                    },
                    year, month, day);

            datePickerDialog.show();
        });


        signUpButton.setOnClickListener(v -> {
            String firstName = firstNameField.getText().toString().trim();
            String lastName = lastNameField.getText().toString().trim();
            String birthday = birthdateField.getText().toString().trim();
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String confirmPassword = confirmPasswordField.getText().toString().trim();

            if (!password.equals(confirmPassword)) {
                Toast.makeText(SignUpScreen.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            File profileImage = null;
            if (selectedImageUri != null) {
                try {
                    profileImage = copyUriToFile(selectedImageUri);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to read image file", Toast.LENGTH_SHORT).show();
                    return; // abort signup if image copy fails
                }
            }

            userViewModel.createUser(
                    profileImage, // can be null
                    firstName,
                    lastName,
                    birthday,
                    email,
                    password
            ).observe(this, success -> {

                if ("success".equals(success)) {
                    Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginScreen.class));
                    finish();
                } else {
                    Toast.makeText(this, success, Toast.LENGTH_SHORT).show(); // shows actual error!
                }
            });

        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            profileImageView.setImageURI(selectedImageUri); // preview
        }
    }

    private File copyUriToFile(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        String fileName = getFileName(uri);
        File tempFile = new File(getCacheDir(), fileName);

        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return tempFile;
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (idx >= 0) result = cursor.getString(idx);
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }


}
