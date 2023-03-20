package com.ksacp2022.electronicinteriordesignapplication;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    AppCompatButton button_interior_designers,button_decor_stores,
    button_design_your_room,button_decor_offers,button_inbox;

    LinearLayout logout,profile;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logout = findViewById(R.id.logout);
        profile = findViewById(R.id.profile);
        button_interior_designers = findViewById(R.id.button_interior_designers);
        button_decor_stores = findViewById(R.id.button_decor_stores);
        button_design_your_room = findViewById(R.id.button_design_your_room);
        button_decor_offers = findViewById(R.id.button_decor_offers);
        button_inbox = findViewById(R.id.button_inbox);



        auth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();

        dialog=new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);


        //check user details
        checkUserType();


        button_interior_designers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,InteriorDesignersActivity. class);
                startActivity(intent);
            }
        });

        button_decor_stores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,DecorStoresActivity. class);
                startActivity(intent);
            }
        });

        button_design_your_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RoomDesignsActivity. class);
                startActivity(intent);
            }
        });

        button_decor_offers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,DecorOffersActivity. class);
                startActivity(intent);
            }
        });




        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent = new Intent(MainActivity.this,SignInActivity. class);
                startActivity(intent);
                finish();
            }
        });

        button_inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,InboxActivity. class);
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if designer has created a profile
                //show waiting dialog
                dialog.setTitle("Checking Profile");
                dialog.setMessage("Please Wait");
                dialog.show();

                String uid=auth.getUid();

                firestore.collection("designer_profile")
                        .document(uid)
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                //if the designer has not created a profile
                                //move to new profile form
                                if(documentSnapshot.exists())
                                {

                                    Intent intent = new Intent(MainActivity.this,DesignerMenuActivity. class);
                                    startActivity(intent);


                                }
                                //if the profile exists
                                //move to designer menu
                                else
                                {
                                    Intent intent = new Intent(MainActivity.this,CreateProfileActivity. class);
                                    startActivity(intent);

                                }
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                makeText(MainActivity.this,"Failed to check profile :"+e.getMessage() , LENGTH_LONG).show();
                            }
                        });
            }
        });


    }

    private void checkUserType() {
        //show waiting dialog
        dialog.setTitle("Checking User");
        dialog.setMessage("Please Wait");
        dialog.show();

        String uid=auth.getUid();

        firestore.collection("profiles")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String,Object> data=documentSnapshot.getData();
                        String type=data.get("user_type").toString();
                        if(type.equals("Designer"))
                        {
                            profile.setVisibility(View.VISIBLE);
                            button_inbox.setVisibility(View.GONE);
                        }
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeText(MainActivity.this,"Couldn't check User Type" , LENGTH_LONG).show();
                    dialog.dismiss();
                    }
                });
    }
}