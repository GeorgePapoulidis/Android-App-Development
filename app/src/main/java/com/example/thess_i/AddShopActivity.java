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


public class AddShopActivity extends AppCompatActivity {

    private EditText shopNameEditText, tableCountEditText;
    private Button addShopButton, backButton;

    private ServerResponse addShop(String storeName,BigInteger userID,int x, int y){
        ServerAPI myServer=new ServerAPI();
        return myServer.addStore(storeName,userID,x,y);
    }

    private void addTables(String storeName,  BigInteger userID,int tableCount){
        new Thread(() -> {
            ServerAPI myServer=new ServerAPI();
            for (int i=1;i<=tableCount;i++){
                ServerResponse response=myServer.addTable(String.valueOf(i),1,1,4,
                        StateOfTable.GREEN,storeName,userID);

            }
        }).start();
    }

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
                String shopName = shopNameEditText.getText().toString();
                int tableCount = Integer.parseInt(tableCountEditText.getText().toString());

                new Thread(() -> {
                    Intent temp=getIntent();
                    String help=getIntent().getStringExtra("userID");
                    BigInteger userID=new BigInteger(help);
                    ServerResponse response=addShop(shopName,userID,10,10);
                    runOnUiThread(()-> {
                        if(response.getExitCode().equals(ServerExitCode.Success)){
                            addTables(shopName,userID,tableCount);
                            Intent intent = new Intent(AddShopActivity.this, TableGridActivity.class);
                            String mode=getIntent().getStringExtra("mode");
                            intent.putExtra("mode",mode);
                            intent.putExtra("shopName",shopName);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(getApplicationContext(), String.valueOf(response.getExitCode()), Toast.LENGTH_SHORT).show();
                        }
                    });
                }).start();

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