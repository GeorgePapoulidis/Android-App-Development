package com.example.thess_i;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import java.math.BigInteger;

import ModuleName.Platform;
import Server.ServerAPI;
import Server.ServerObjectResponse;
import Server.ServerExitCode;

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

                if (DataHolder.admin.username.equals(username) && DataHolder.admin.password.equals(password)) {
                    Intent intent = new Intent(LoginActivity.this, AddShopActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Handle failed login
                }
                /**String result= Platform.logIn(username,password);
                if(result.equals("Success")){
                    Intent intent = new Intent(LoginActivity.this, AddShopActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    System.out.println("Bale pop edw");
                }*/
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
