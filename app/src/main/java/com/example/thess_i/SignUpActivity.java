package com.example.thess_i;


import ModuleName.Platform;
import Server.ServerAPI;
import Server.ServerResponse;
import Server.ServerExitCode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameEditText, fullnameEditText, emailEditText, passwordEditText;
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


    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle("Επιτυχής Εγγραφή");
        builder.setMessage("Η εγγραφή σας ολοκληρώθηκε επιτυχώς!");

        builder.setPositiveButton("ΟΚ", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void showErrorDialog(ServerExitCode exitCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle("Αποτυχία Εγγραφής");
        builder.setMessage(getErrorMessage(exitCode));

        builder.setPositiveButton("ΟΚ", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private String getErrorMessage(ServerExitCode exitCode) {
        switch (exitCode) {
            case NullUserName:
                return "Το όνομα χρήστη δεν μπορεί να είναι κενό.";
            case NullPassword:
                return "Ο κωδικός πρόσβασης δεν μπορεί να είναι κενός.";
            case NullFullName:
                return "Το ονοματεπώνυμο δεν μπορεί να είναι κενό.";
            case NullEmail:
                return "Το email δεν μπορεί να είναι κενό.";
            case UserNameExists:
                return "Το όνομα χρήστη υπάρχει ήδη. Παρακαλώ επιλέξτε άλλο όνομα.";
            case EmailExists:
                return "Το email χρησιμοποιείται ήδη για άλλο λογαριασμό. Παρακαλώ χρησιμοποιήστε διαφορετικό email.";
            default:
                return "Παρουσιάστηκε σφάλμα κατά την εγγραφή.";
        }
    }


    private ServerResponse signUp(String fullName, String username, String password, String email) {
        ServerAPI myServer=new ServerAPI();
        return myServer.addUser(fullName,username,password,email);
        /*if (username.isEmpty()) {
            return new ServerResponse(ServerExitCode.NullUserName);
        } else if (password.isEmpty()) {
            return new ServerResponse(ServerExitCode.NullPassword);
        } else if (fullName.isEmpty()) {
            return new ServerResponse(ServerExitCode.NullFullName);
        } else if (email.isEmpty()) {
            return new ServerResponse(ServerExitCode.NullEmail);
        } else {
            return new ServerResponse(ServerExitCode.Success);
        }*/
    }
}
