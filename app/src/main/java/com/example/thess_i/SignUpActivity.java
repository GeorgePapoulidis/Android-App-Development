package com.example.thess_i;

import ModuleName.Platform;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, passwordEditText,fullnameEditText;
    private Button signUpButton, switchToLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        usernameEditText = findViewById(R.id.edit_text_username);
        fullnameEditText = findViewById(R.id.edit_text_fullname);

        emailEditText = findViewById(R.id.edit_text_email);
        passwordEditText = findViewById(R.id.edit_text_password);
        signUpButton = findViewById(R.id.button_sign_up);
        switchToLoginButton = findViewById(R.id.button_switch_to_login);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName=fullnameEditText.getText().toString();
                String username=usernameEditText.getText().toString();
                String password=passwordEditText.getText().toString();
                String email=emailEditText.getText().toString();

                String result= Platform.addUser(fullName,username,password,email);
                if(result.equals("Success")){
                    Intent intent = new Intent(SignUpActivity.this, AddShopActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    System.out.println("Bale pop edw");
                }
            }
        });

        switchToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}