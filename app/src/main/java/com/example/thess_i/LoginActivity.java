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

                /*
                otidipote einai call sto database, prepei na trexei kapos etsi upoxreotika.
                Mporo na kaleso ena method, kai na kano handle to result tou mesa ston ActionListener tou koumpiou.
                Autos o kodikas bazei ena method na trexei se background thread.
                Alla me to future.get() kano block to main thread gia na perimeno to result.
                Einai kaki lusi alla it works for now.
                Uparxoun antistoixes diafaneies sto elearning.
                */
                /*
                autos o kodikas tha empaine edo mesa, sto onClick

                Future<String> future = executorService.submit(() -> w());
                new Thread(() -> {
                    try {
                        String result = future.get(); // This will block until the result is available
                        System.out.println(result);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
                */

                /*
                to method pou ektelesa gia testing einai to w()
                auti mporei na einai mai opoiadipote methodos, px tou Interconnection, i akomi kai tou LoginActivity
                private String w() {
                    try {
                        Thread.sleep(10000); // Simulate 10 seconds delay
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return "Hello. I finished";
                }
                 */




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
