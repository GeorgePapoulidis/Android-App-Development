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

/**
 * The SearchShopActivity class provides functionality to search for shops and display the results in a list.
 */
public class SearchShopActivity extends AppCompatActivity {

    // UI elements for search input, buttons, and results list
    private EditText searchEditText;
    private Button searchButton, backButton;
    private ListView resultListView;

    /**
     * Adapter class for displaying store information in a ListView.
     */
    class StoreAdapter extends ArrayAdapter<Store> {

        /**
         * Constructor for StoreAdapter.
         *
         * @param context the context in which the adapter is used
         * @param stores the list of stores to display
         */
        public StoreAdapter(Context context, List<Store> stores) {
            super(context, 0, stores);
        }

        /**
         * Gets the view for each item in the list.
         *
         * @param position the position of the item within the adapter's data set
         * @param convertView the old view to reuse, if possible
         * @param parent the parent that this view will eventually be attached to
         * @return a view corresponding to the data at the specified position
         */
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

    /**
     * Retrieves a list of stores from the server based on the specified options.
     *
     * @param options a HashMap containing the options for the server request
     * @return a ServerArrayResponse containing the list of stores
     */
    private ServerArrayResponse<Store> getStores(HashMap<String, Boolean> options) {
        ServerAPI myServer = new ServerAPI();
        return myServer.getStores(null, options);
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
        setContentView(R.layout.activity_search_shop);

        // Initialize UI elements
        searchEditText = findViewById(R.id.edit_text_search);
        searchButton = findViewById(R.id.button_search);
        resultListView = findViewById(R.id.list_view_results);
        backButton = findViewById(R.id.button_back);

        // Set click listener for the search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shopName = searchEditText.getText().toString();
                searchShops(shopName);
            }
        });

        // Set click listener for the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchShopActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Set item click listener for the results list
        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Store selectedStore = (Store) parent.getItemAtPosition(position);
                openTableGrid(selectedStore);
            }
        });
    }

    /**
     * Searches for shops matching the specified name.
     *
     * @param shopName the name of the shop to search for
     */
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

    /**
     * Updates the ListView with the search results.
     *
     * @param stores the list of stores to display
     */
    private void updateResults(List<Store> stores) {
        StoreAdapter adapter = new StoreAdapter(this, stores);
        resultListView.setAdapter(adapter);
    }

    /**
     * Opens the table grid activity for the selected store.
     *
     * @param store the store selected by the user
     */
    private void openTableGrid(Store store) {
        Intent intent = new Intent(SearchShopActivity.this, TableGridActivity.class);
        String mode = getIntent().getStringExtra("mode");
        intent.putExtra("shopName", store.getName());
        intent.putExtra("mode", mode);
        startActivity(intent);
        finish();
    }
}
