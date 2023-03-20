package com.ksacp2022.electronicinteriordesignapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminHomeActivity extends AppCompatActivity {
    AppCompatButton button_decor_stores,button_decor_offers;

    LinearLayout logout;
    FirebaseAuth auth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        logout = findViewById(R.id.logout);
        button_decor_stores = findViewById(R.id.button_decor_stores);
        button_decor_offers = findViewById(R.id.button_decor_offers);



        auth=FirebaseAuth.getInstance();




        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent = new Intent(AdminHomeActivity.this,SignInActivity. class);
                startActivity(intent);
            }
        });


        button_decor_stores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AdminHomeActivity.this,AdminStoresActivity. class);
                startActivity(intent);
            }
        });

        button_decor_offers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this,AdminDecorOffersActivity. class);
                startActivity(intent);
            }
        });





    }
}