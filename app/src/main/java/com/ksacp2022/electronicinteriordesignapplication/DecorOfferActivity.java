package com.ksacp2022.electronicinteriordesignapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.ksacp2022.electronicinteriordesignapplication.adapters.OfferImagesListAdapter;

import java.util.List;
import java.util.Map;

public class DecorOfferActivity extends AppCompatActivity {
    RecyclerView recycler_images;
    TextView text_view_name,text_view_desc,text_new_price,text_original_price,text_view_address;
    AppCompatButton button_call;
    ProgressDialog progressDialog;
    FirebaseFirestore firestore;
    FirebaseStorage storage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decor_offer);
        recycler_images = findViewById(R.id.recycler_images);
        text_view_name = findViewById(R.id.text_view_name);
        text_view_desc = findViewById(R.id.text_view_desc);
        text_new_price = findViewById(R.id.text_new_price);
        text_view_address = findViewById(R.id.text_view_address);
        button_call = findViewById(R.id.button_call);
        text_original_price = findViewById(R.id.text_original_price);


        String offer_id=getIntent().getStringExtra("offer_id");

        firestore=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        progressDialog.setTitle("Loading Offer info");
        progressDialog.setMessage("Please Wait");

        firestore.collection("decor_offers")
                .document(offer_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressDialog.dismiss();
                        Map<String,Object> data=documentSnapshot.getData();
                        text_view_name.setText(data.get("title").toString());
                        text_view_address.setText(data.get("address").toString());
                        text_view_desc.setText(data.get("description").toString());
                        text_new_price.setText(data.get("new_price").toString());
                        text_original_price.setText(data.get("original_price").toString());
                        text_original_price.setPaintFlags(text_original_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        String phone=data.get("phone").toString();
                        List<String> images= (List<String>) data.get("images");
                        OfferImagesListAdapter adapter=new OfferImagesListAdapter(images,DecorOfferActivity.this);
                        recycler_images.setAdapter(adapter);

                        button_call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent=new Intent(Intent.ACTION_DIAL);
                                intent.setData( Uri.parse("tel:"+phone));
                                startActivity(intent);
                            }
                        });


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DecorOfferActivity.this,"Failed to load info" , Toast.LENGTH_LONG).show();

                        progressDialog.dismiss();
                        finish();
                    }
                });





    }
}