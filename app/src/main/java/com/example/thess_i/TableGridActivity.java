package com.example.thess_i;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

import ModuleName.StateOfTable;
import ModuleName.Table;
import Server.ServerAPI;
import Server.ServerArrayResponse;
import Server.ServerExitCode;

/**
 * The TableGridActivity class displays the grid of tables for a selected shop.
 */
public class TableGridActivity extends AppCompatActivity {

    // UI elements
    private Button backButton;
    private GridLayout gridLayout;
    private TextView totalTablesTextView;
    private TextView freeTablesTextView;

    /**
     * Retrieves tables from the server based on shop name and options.
     *
     * @param shopName the name of the shop
     * @param options  additional options for retrieving tables
     * @return a ServerArrayResponse containing tables retrieved from the server
     */
    private ServerArrayResponse<Table> getTables(String shopName, HashMap<String, Boolean> options) {
        ServerAPI myServer = new ServerAPI();
        return myServer.getTables(shopName, null, options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_grid);

        // Initialize UI elements
        backButton = findViewById(R.id.button_back);
        gridLayout = findViewById(R.id.gridLayout);
        totalTablesTextView = findViewById(R.id.totalTablesTextView);
        freeTablesTextView = findViewById(R.id.freeTablesTextView);

        // Retrieve shop name and mode (user or admin)
        String shopName = getIntent().getStringExtra("shopName");
        String mode = getIntent().getStringExtra("mode");

        // Handle case where shop name is not provided
        if (shopName == null) {
            Toast.makeText(this, "Shop not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Retrieve tables information from the server
        new Thread(() -> {
            HashMap<String, Boolean> options = new HashMap<>();
            options.put("id", true);
            options.put("store", false);
            options.put("name", true);
            options.put("state", true);
            options.put("position_x", false);
            options.put("position_y", false);
            options.put("people", false);

            ServerArrayResponse<Table> response = getTables(shopName, options);

            runOnUiThread(() -> {
                if (response.getExitCode().equals(ServerExitCode.Success)) {
                    ArrayList<Table> tables = new ArrayList<>(response.getData());
                    int freeTablesCount = 0;
                    for (Table table : tables) {
                        if (table.getState().equals(StateOfTable.GREEN.toString())) {
                            freeTablesCount++;
                        }
                    }
                    totalTablesTextView.setText("Total Tables: " + tables.size());
                    freeTablesTextView.setText("Free Tables: " + freeTablesCount);
                } else {
                    Toast.makeText(getApplicationContext(), String.valueOf(response.getExitCode()), Toast.LENGTH_SHORT).show();
                }
            });
        }).start();

        // Set click listener for the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Determine the appropriate activity to return to based on the mode
                Intent intent;
                if (mode != null && mode.equals("user")) {
                    intent = new Intent(TableGridActivity.this, SearchShopActivity.class);
                } else {
                    intent = new Intent(TableGridActivity.this, AddShopActivity.class);
                }
                startActivity(intent);
                finish();
            }
        });
    }
}
