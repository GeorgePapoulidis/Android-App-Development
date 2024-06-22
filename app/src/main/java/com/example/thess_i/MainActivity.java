package com.example.thess_i;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

/**
 * The MainActivity class represents the main screen of the application where the user can choose
 * to either log in as a user or an admin.
 */
public class MainActivity extends AppCompatActivity {

    // Buttons for user and admin options
    private Button userButton, adminButton;

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
        setContentView(R.layout.activity_main);

        // Initialize buttons
        userButton = findViewById(R.id.button_user);
        adminButton = findViewById(R.id.button_admin);

        // Set click listener for user button
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start SearchShopActivity with user mode
                Intent intent = new Intent(MainActivity.this, SearchShopActivity.class);
                intent.putExtra("mode", "user");
                startActivity(intent);
                finish();
            }
        });

        // Set click listener for admin button
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start LoginActivity with admin mode
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("mode", "admin");
                startActivity(intent);
                finish();
            }
        });
    }
}
