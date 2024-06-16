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
import Server.ServerResponse;

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
                new Thread(() -> {
                    ServerObjectResponse<BigInteger> response=logIn(username,password);
                    BigInteger bigInteger=response.getData();
                    runOnUiThread(()-> {
                        if(response.getExitCode().equals(ServerExitCode.Success)){
                            Intent intent = new Intent(LoginActivity.this, AddShopActivity.class);
                            intent.putExtra("userID",bigInteger.toString());
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(getApplicationContext(), String.valueOf(response.getExitCode()), Toast.LENGTH_SHORT).show();
                        }
                    });
                }).start();
            }
        });

        switchToSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
