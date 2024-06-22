package com.example.thess_i;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

import ModuleName.StateOfTable;
import Server.ServerAPI;
import Server.ServerExitCode;
import Server.ServerObjectResponse;
import Server.ServerResponse;

/**
 * The AddShopActivity class provides functionality to add a new shop and tables to the shop.
 */
public class AddShopActivity extends AppCompatActivity {

    // UI elements for shop name input, table count input, and buttons
    private EditText shopNameEditText, tableCountEditText;
    private Button addShopButton, backButton;

    /**
     * Adds a new shop to the server.
     *
     * @param storeName the name of the store
     * @param userID the ID of the user adding the store
     * @param x the x-coordinate of the store
     * @param y the y-coordinate of the store
     * @return a ServerResponse indicating the result of the add operation
     */
    private ServerResponse addShop(String storeName, BigInteger userID, int x, int y) {
        ServerAPI myServer = new ServerAPI();
        return myServer.addStore(storeName, userID, x, y);
    }

    /**
     * Adds tables to a specified store.
     *
     * @param storeName the name of the store
     * @param userID the ID of the user adding the tables
     * @param tableCount the number of tables to add
     */
    private void addTables(String storeName, BigInteger userID, int tableCount) {
        new Thread(() -> {
            ServerAPI myServer = new ServerAPI();
            for (int i = 1; i <= tableCount; i++) {
                myServer.addTable(String.valueOf(i), 1, 1, 4,
                        StateOfTable.GREEN, storeName, userID);
            }
        }).start();
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
        setContentView(R.layout.activity_add_shop);

        // Initialize UI elements
        shopNameEditText = findViewById(R.id.edit_text_shop_name);
        tableCountEditText = findViewById(R.id.edit_text_table_count);
        addShopButton = findViewById(R.id.button_add_shop);
        backButton = findViewById(R.id.button_back);

        // Set click listener for the add shop button
        addShopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shopName = shopNameEditText.getText().toString();
                int tableCount = Integer.parseInt(tableCountEditText.getText().toString());

                new Thread(() -> {
                    String userIDStr = getIntent().getStringExtra("userID");
                    BigInteger userID = new BigInteger(userIDStr);
                    ServerResponse response = addShop(shopName, userID, 10, 10);
                    runOnUiThread(() -> {
                        if (response.getExitCode().equals(ServerExitCode.Success)) {
                            addTables(shopName, userID, tableCount);
                            Toast.makeText(getApplicationContext(), "Shop Added Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), String.valueOf(response.getExitCode()), Toast.LENGTH_SHORT).show();
                        }
                    });
                }).start();
            }
        });

        // Set click listener for the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddShopActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
