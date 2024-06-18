package com.example.thess_i;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

import ModuleName.StateOfTable;
import ModuleName.Store;
import ModuleName.Table;
import Server.ServerAPI;
import Server.ServerArrayResponse;
import Server.ServerExitCode;

public class TableGridActivity extends AppCompatActivity {

    private Button backButton;
    private GridLayout gridLayout;

    private TextView totalTablesTextView;
    private TextView freeTablesTextView;
    private boolean isAdmin;
    //private DataHolder.Shop currentShop;

    private ServerArrayResponse<Table> getTables(String shopName,HashMap<String,Boolean>options){
        ServerAPI myServer=new ServerAPI();
        return myServer.getTables(shopName,null,options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_grid);

        backButton = findViewById(R.id.button_back);
        gridLayout = findViewById(R.id.gridLayout);
        totalTablesTextView = findViewById(R.id.totalTablesTextView);
        freeTablesTextView = findViewById(R.id.freeTablesTextView);


        String shopName = getIntent().getStringExtra("shopName");
        //int tableNum=getIntent().getIntExtra("tableCount",0);
        String isAdmin = getIntent().getStringExtra("mode");

        if (shopName == null) {
            Toast.makeText(this, "Shop not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        new Thread(() -> {
            HashMap<String, Boolean> options = new HashMap<>();
            options.put("id", true);
            options.put("store", false);
            options.put("name", true);
            options.put("state",true);
            options.put("position_x", false);
            options.put("position_y", false);
            options.put("people",false);

            ServerArrayResponse<Table> response = getTables(shopName,options);

            runOnUiThread(() -> {

                if (response.getExitCode().equals(ServerExitCode.Success)) {
                    ArrayList<Table> helpTables=new ArrayList<>(response.getData());
                    int freeTablesCount=0;
                    for (Table table:helpTables){
                        if (table.getState().equals("GREEN")){
                            freeTablesCount++;
                        }
                    }
                    totalTablesTextView.setText("Total Tables: " + helpTables.size());
                    freeTablesTextView.setText("Free Tables: " + freeTablesCount);
                } else {
                    Toast.makeText(getApplicationContext(), String.valueOf(response.getExitCode()), Toast.LENGTH_SHORT).show();
                }
            });
        }).start();


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent helpIntent=getIntent();
                String mode=helpIntent.getStringExtra("mode");
                assert mode != null;
                Intent intent;
                if(mode.equals("user")){
                    intent=new Intent(TableGridActivity.this, SearchShopActivity.class);
                }else {
                    intent=new Intent(TableGridActivity.this, AddShopActivity.class);
                }

                startActivity(intent);
                finish();
            }
        });
    }
}