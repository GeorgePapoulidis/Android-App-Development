package com.example.thess_i;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;


public class AddShopActivity extends AppCompatActivity {

    private EditText shopNameEditText, tableCountEditText;
    private Button addShopButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop);

        shopNameEditText = findViewById(R.id.edit_text_shop_name);
        tableCountEditText = findViewById(R.id.edit_text_table_count);
        addShopButton = findViewById(R.id.button_add_shop);
        backButton = findViewById(R.id.button_back);

        addShopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**String shopName = shopNameEditText.getText().toString();
                int tableCount = Integer.parseInt(tableCountEditText.getText().toString());*/

                /**DataHolder.Shop newShop = new DataHolder.Shop(shopName);
                for (int i = 1; i <= tableCount; i++) {
                    newShop.tables.add(new DataHolder.Table(i, 4, true)); // Default capacity to 4 and available
                }
                DataHolder.shops.add(newShop);*/

                // Start TableGridActivity
                Intent intent = new Intent(AddShopActivity.this, TableGridActivity.class);
                /**intent.putExtra("shopName", shopName);
                intent.putExtra("isAdmin", true);*/
                startActivity(intent);
                finish();

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddShopActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}


