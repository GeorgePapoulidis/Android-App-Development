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

import java.math.BigInteger;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameEditText, fullnameEditText, emailEditText, passwordEditText;
    private Button signUpButton, switchToLoginButton;

    private ServerResponse signUp(String fullName,String username,String password,String email){
        ServerAPI myServer=new ServerAPI();
        return myServer.addUser(fullName, username, password, email);
    }

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

                /**
                 new Thread(() -> {
                    ServerResponse response=signUp(fullName,username,password,email);
                    runOnUiThread(() -> {
                        if (response.getExitCode() == ServerExitCode.Success) {
                            //BigInteger userID = response.getData();
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            //intent.putExtra("userID", userID.toString());
                            startActivity(intent);
                            finish();
                        } else {
                            //Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }).start();


                /**String result= Platform.addUser(fullName,username,password,email);
                if(result.equals("Success")){
                    Intent intent = new Intent(SignUpActivity.this, AddShopActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    System.out.println("Bale pop edw");
                }
                return fullName;*/

              /**ServerResponse response=signUp(fullName,username,password,email);
              if(response.getExitCode().equals(ServerExitCode.Success)){
                  Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                  startActivity(intent);
                  finish();
              }else {
                  Toast.makeText(getApplicationContext(),String.valueOf(response.getExitCode()),Toast.LENGTH_SHORT).show();
              }*/

              new Thread(() -> {
                  ServerResponse response=signUp(fullName,username,password,email);
                  runOnUiThread(()-> {
                      if(response.getExitCode().equals(ServerExitCode.Success)){
                          Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                          startActivity(intent);
                          finish();
                      }else {
                          Toast.makeText(getApplicationContext(), String.valueOf(response.getExitCode()), Toast.LENGTH_SHORT).show();
                      }
                  });
              }).start();

            }
        });

        switchToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }






}
