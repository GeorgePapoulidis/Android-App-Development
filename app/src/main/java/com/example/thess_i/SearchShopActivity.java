package com.example.thess_i;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ModuleName.Store;
import Server.ServerAPI;
import Server.ServerArrayResponse;
import Server.ServerExitCode;

public class SearchShopActivity extends AppCompatActivity {

    private EditText searchEditText;
    private Button searchButton, backButton;
    private ListView resultListView;

    class StoreAdapter extends ArrayAdapter<Store> {

        public StoreAdapter(Context context, List<Store> stores) {
            super(context, 0, stores);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Store store = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            }

            TextView textView = convertView.findViewById(android.R.id.text1);
            textView.setText(store.getName());

            return convertView;
        }
    }

    private ServerArrayResponse<Store> getStores(HashMap<String, Boolean> options) {
        ServerAPI myServer = new ServerAPI();
        return myServer.getStores(null, options);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_shop);

        searchEditText = findViewById(R.id.edit_text_search);
        searchButton = findViewById(R.id.button_search);
        resultListView = findViewById(R.id.list_view_results);
        backButton = findViewById(R.id.button_back);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shopName = searchEditText.getText().toString();
                searchShops(shopName);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchShopActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Store selectedStore = (Store) parent.getItemAtPosition(position);
                openTableGrid(selectedStore);
            }
        });
    }

    private void searchShops(String shopName) {
        new Thread(() -> {
            HashMap<String, Boolean> options = new HashMap<>();
            options.put("id", true);
            options.put("name", true);
            options.put("owner", false);
            options.put("grid_x", false);
            options.put("grid_y", false);
            ServerArrayResponse<Store> response = getStores(options);
            runOnUiThread(() -> {
                if (response.getExitCode().equals(ServerExitCode.Success)) {
                    ArrayList<Store> stores = new ArrayList<>(response.getData());
                    ArrayList<Store> filteredStores = new ArrayList<>();
                    for (Store store : stores) {
                        if (store.getName().toLowerCase().contains(shopName.toLowerCase())) {
                            filteredStores.add(store);
                        }
                    }
                    updateResults(filteredStores);
                } else {
                    Toast.makeText(getApplicationContext(), String.valueOf(response.getExitCode()), Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void updateResults(List<Store> stores) {
        StoreAdapter adapter = new StoreAdapter(this, stores);
        resultListView.setAdapter(adapter);
    }

    private void openTableGrid(Store store) {
        Intent intent = new Intent(SearchShopActivity.this, TableGridActivity.class);
        String mode = getIntent().getStringExtra("mode");
        intent.putExtra("mode", mode);
        startActivity(intent);
        finish();
    }
}
