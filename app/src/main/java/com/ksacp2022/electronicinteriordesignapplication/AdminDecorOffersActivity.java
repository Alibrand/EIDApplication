package com.ksacp2022.electronicinteriordesignapplication;

import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022.electronicinteriordesignapplication.adapters.AdminDecorListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.adapters.AdminDecorOffersListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.adapters.AdminStoresAdapter;
import com.ksacp2022.electronicinteriordesignapplication.models.DecorOffer;
import com.ksacp2022.electronicinteriordesignapplication.models.DecorStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminDecorOffersActivity extends AppCompatActivity {
    List<DecorOffer> decorOffersList;
    RecyclerView recycler_view_decor_offers;

    AppCompatButton button_add_offer;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_decor_offers);
        recycler_view_decor_offers = findViewById(R.id.recycler_view_decor_offers);
        button_add_offer = findViewById(R.id.button_add_offer);

        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        load_offers();


        button_add_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDecorOffersActivity.this,AdminAddOffersActivity. class);
                startActivity(intent);
            }
        });










    }

    private void load_offers() {
        progressDialog.setMessage("Loading");
        progressDialog.show();


        firestore.collection("decor_offers")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        decorOffersList =new ArrayList<DecorOffer>();
                        progressDialog.dismiss();
                        for (DocumentSnapshot doc:queryDocumentSnapshots.getDocuments()
                        ) {
                            Map<String,Object> data=doc.getData();
                            DecorOffer decorOffer=new DecorOffer();
                            decorOffer.setTitle(data.get("title").toString());
                            decorOffer.setOriginal_price(data.get("original_price").toString());
                            decorOffer.setNew_price(data.get("new_price").toString());
                            List<String> images= (List<String>) data.get("images");
                            decorOffer.setImages(images);
                            decorOffer.setId(doc.getId());
                            decorOffersList.add(decorOffer);

                        }

                        AdminDecorOffersListAdapter adapter=new AdminDecorOffersListAdapter(decorOffersList,AdminDecorOffersActivity.this);
                        recycler_view_decor_offers.setAdapter(adapter);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminDecorOffersActivity.this,"Failed to load offers" , Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        load_offers();
    }
}