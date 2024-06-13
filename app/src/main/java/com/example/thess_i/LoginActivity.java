package com.example.thess_i;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigInteger;

import Server.*;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton, switchToSignUpButton, backButton;

    private ServerObjectResponse<BigInteger> logIn(String username, String password) {
        ServerAPI server = new ServerAPI();
        return server.logIn(username, password);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.edit_text_username);
        passwordEditText = findViewById(R.id.edit_text_password);
        loginButton = findViewById(R.id.button_login);
        switchToSignUpButton = findViewById(R.id.button_switch_to_sign_up);
        backButton = findViewById(R.id.button_back);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();


                ServerObjectResponse<BigInteger> response = logIn(username, password);


                if (response.getExitCode() == ServerExitCode.Success) {
                    BigInteger userID = response.getData();
                    Intent intent = new Intent(LoginActivity.this,AddShopActivity.class);
                    intent.putExtra("userID", userID.toString()); // Μεταφορά του userID στο MainActivity
                    startActivity(intent);
                    finish();
                } else {
                    // Αποτυχία σύνδεσης, εμφάνιση κατάλληλου μηνύματος στον χρήστη
                    // Παράδειγμα: εμφάνιση μηνύματος λάθους
                    // Εδώ μπορείτε να προσθέσετε οποιαδήποτε άλλη λειτουργία απότυχε
                }
            }
        });

        switchToSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}