package com.example.thess_i;

import Server.ServerAPI;
import Server.ServerExitCode;
import Server.ServerResponse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * The SignUpActivity class handles user sign-up functionality, allowing new users to register.
 */
public class SignUpActivity extends AppCompatActivity {

    // EditText fields for user input
    private EditText usernameEditText, fullnameEditText, emailEditText, passwordEditText;

    // Buttons for sign-up and switching to login
    private Button signUpButton, switchToLoginButton;

    /**
     * Registers a new user with the provided details by communicating with the server.
     *
     * @param fullName the full name of the user
     * @param username the username of the user
     * @param password the password of the user
     * @param email    the email of the user
     * @return a ServerResponse containing the result of the sign-up attempt
     */
    private ServerResponse signUp(String fullName, String username, String password, String email) {
        ServerAPI myServer = new ServerAPI();
        return myServer.addUser(fullName, username, password, email);
    }

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize EditText and Button views
        usernameEditText = findViewById(R.id.edit_text_username);
        fullnameEditText = findViewById(R.id.edit_text_fullname);
        emailEditText = findViewById(R.id.edit_text_email);
        passwordEditText = findViewById(R.id.edit_text_password);
        signUpButton = findViewById(R.id.button_sign_up);
        switchToLoginButton = findViewById(R.id.button_switch_to_login);

        // Set click listener for the sign-up button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = fullnameEditText.getText().toString();
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String email = emailEditText.getText().toString();

                // Start a new thread for sign-up
                new Thread(() -> {
                    ServerResponse response = signUp(fullName, username, password, email);

                    // Run UI updates on the main thread
                    runOnUiThread(() -> {
                        if (response.getExitCode().equals(ServerExitCode.Success)) {
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), String.valueOf(response.getExitCode()), Toast.LENGTH_SHORT).show();
                        }
                    });
                }).start();
            }
        });

        // Set click listener for the switch to login button
        switchToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
