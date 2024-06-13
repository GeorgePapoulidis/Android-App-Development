package com.example.thess_i;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SearchShopActivity extends AppCompatActivity {

    private EditText searchEditText;
    private Button searchButton, backButton;
    private TextView resultTextView;

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
                String query = searchEditText.getText().toString();
                /*for (DataHolder.Shop shop : DataHolder.shops) {
                    if (shop.name.equalsIgnoreCase(query)) {
                        Intent intent = new Intent(SearchShopActivity.this, TableGridActivity.class);
                        intent.putExtra("shopName", shop.name);
                        intent.putExtra("tableCount", shop.tables.size());
                        intent.putExtra("isAdmin", false);
                        startActivity(intent);
                        return;
                    }
                }
                resultTextView.setText("Shop not found");*/

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

