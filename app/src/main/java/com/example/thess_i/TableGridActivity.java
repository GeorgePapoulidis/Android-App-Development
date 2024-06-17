package com.example.thess_i;


import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class TableGridActivity extends AppCompatActivity {

    private Button backButton;
    private GridLayout gridLayout;
    private boolean isAdmin;
    //private DataHolder.Shop currentShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_grid);

        backButton = findViewById(R.id.button_back);
        gridLayout = findViewById(R.id.gridLayout);

        String shopName = getIntent().getStringExtra("shopName");
        //int tableNum=getIntent().getIntExtra("tableCount",0);
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        // Find the shop by name
        /**for (DataHolder.Shop shop : DataHolder.shops) {
            if (shop.name.equals(shopName)) {
                currentShop = shop;
                break;
            }
        }*/

       /**if (shopName == null) {
            Toast.makeText(this, "Shop not found", Toast.LENGTH_SHORT).show();
            finish();
       }*/

        /**setTitle("Tables for " + shopName);*/

        /**for (DataHolder.Table table : currentShop.tables) {
            final int tableNumber = table.number;
            Button button = new Button(this);
            updateTableButton(button, table);
            button.setOnClickListener(v -> {
                if (isAdmin) {
                    showTableDialog(table, button);
                } else {
                    Toast.makeText(TableGridActivity.this, "Τραπέζι " + tableNumber + " clicked!", Toast.LENGTH_SHORT).show();
                }
            });
            gridLayout.addView(button);

        }*/


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

    /**private void updateTableButton(Button button, DataHolder.Table table) {
        String availabilityText = table.isAvailable ? "Διαθέσιμο" : "Μη Διαθέσιμο";
        button.setText("Τραπέζι " + table.number + "\n" + table.capacity + " άτομα\n" + availabilityText);
    }

    private void showTableDialog(DataHolder.Table table, Button tableButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_table_settings, null);
        builder.setView(dialogView);

        EditText capacityEditText = dialogView.findViewById(R.id.edit_text_capacity);
        Switch availabilitySwitch = dialogView.findViewById(R.id.switch_availability);

        capacityEditText.setText(String.valueOf(table.capacity));
        availabilitySwitch.setChecked(table.isAvailable);

        builder.setTitle("Ρυθμίσεις για Τραπέζι " + table.number)
                .setPositiveButton("Αποθήκευση", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        table.capacity = Integer.parseInt(capacityEditText.getText().toString());
                        table.isAvailable = availabilitySwitch.isChecked();
                        updateTableButton(tableButton, table);
                        Toast.makeText(TableGridActivity.this, "Οι ρυθμίσεις αποθηκεύτηκαν για το Τραπέζι " + table.number, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Ακύρωση", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }*/
}