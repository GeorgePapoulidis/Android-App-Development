package com.example.thess_i;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigInteger;

import Server.ServerAPI;
import Server.ServerObjectResponse;
import Server.ServerExitCode;

/**
 * The LoginActivity class handles user login functionality, allowing the user to log in, switch to the sign-up screen,
 * or go back to the main activity.
 */
public class LoginActivity extends AppCompatActivity {

    // EditText fields for username and password input
    private EditText usernameEditText, passwordEditText;

    // Buttons for login, switching to sign-up, and going back to the main activity
    private Button loginButton, switchToSignUpButton, backButton;

    /**
     * Logs in the user with the given username and password by communicating with the server.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return a ServerObjectResponse containing the result of the login attempt
     */
    private ServerObjectResponse<BigInteger> logIn(String username, String password) {
        ServerAPI server = new ServerAPI();
        return server.logIn(username, password);
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
        setContentView(R.layout.activity_login);

        // Initialize EditText and Button views
        usernameEditText = findViewById(R.id.edit_text_username);
        passwordEditText = findViewById(R.id.edit_text_password);
        loginButton = findViewById(R.id.button_login);
        switchToSignUpButton = findViewById(R.id.button_switch_to_sign_up);
        backButton = findViewById(R.id.button_back);

        // Set click listener for the login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Start a new thread for login
                new Thread(() -> {
                    ServerObjectResponse<BigInteger> response = logIn(username, password);
                    BigInteger bigInteger = response.getData();

                    // Run UI updates on the main thread
                    runOnUiThread(() -> {
                        if (response.getExitCode().equals(ServerExitCode.Success)) {
                            Intent intent = new Intent(LoginActivity.this, AddShopActivity.class);
                            String mode = getIntent().getStringExtra("mode");
                            intent.putExtra("userID", bigInteger.toString());
                            intent.putExtra("mode", mode);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), String.valueOf(response.getExitCode()), Toast.LENGTH_SHORT).show();
                        }
                    });
                }).start();
            }
        });

        // Set click listener for the switch to sign-up button
        switchToSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Set click listener for the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
