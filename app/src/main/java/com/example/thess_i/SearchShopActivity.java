package com.example.thess_i;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigInteger;
import java.util.HashMap;

import ModuleName.Store;
import ModuleName.Table;
import Server.ServerAPI;
import Server.ServerArrayResponse;
import Server.ServerExitCode;
import Server.ServerObjectResponse;

public class SearchShopActivity extends AppCompatActivity {

    private EditText searchEditText;
    private Button searchButton, backButton;
    private TextView resultTextView;

    private ServerArrayResponse<Store> getStores(BigInteger userID, HashMap<String,Boolean> options){
        ServerAPI myServer=new ServerAPI();
        return myServer.getStores(userID,options);
    }

    private ServerArrayResponse<Table> getTables(String storeName, BigInteger userID, HashMap<String,Boolean> options){
        ServerAPI myServer=new ServerAPI();
        return myServer.getTables(storeName, userID, options);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_shop);

        searchEditText = findViewById(R.id.edit_text_search);
        searchButton = findViewById(R.id.button_search);
        resultTextView = findViewById(R.id.text_view_result);
        backButton = findViewById(R.id.button_back);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shopName = searchEditText.getText().toString();
                /**for (DataHolder.Shop shop : DataHolder.shops) {
                    if (shop.name.equalsIgnoreCase(query)) {
                        Intent intent = new Intent(SearchShopActivity.this, TableGridActivity.class);
                        intent.putExtra("shopName", shop.name);
                        intent.putExtra("tableCount", shop.tables.size());
                        intent.putExtra("isAdmin", false);
                        startActivity(intent);
                        return query;
                    }
                }
                resultTextView.setText("Shop not found");

                return query;*/

                new Thread(() -> {

                    runOnUiThread(()-> {

                    });
                }).start();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SearchShopActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}

