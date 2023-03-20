package com.ksacp2022.electronicinteriordesignapplication;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.ksacp2022.electronicinteriordesignapplication.adapters.AdminDecorListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.adapters.DecorsListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.models.DecorProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DecorStoreProductsActivity extends AppCompatActivity {


    RecyclerView recycler_view_decors;

    FirebaseFirestore firestore;
    FirebaseStorage storage;
    ProgressDialog progressDialog;


    List<DecorProduct> decor_products;

    String store_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decor_store_products);
        recycler_view_decors = findViewById(R.id.recycler_view_decors);

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        firestore=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();


        store_id=getIntent().getStringExtra("store_id");

        //load decors images for the store
        load_decors();







    }

    private void load_decors() {
        progressDialog.setTitle("Loading Decors");
        progressDialog.setMessage("Please Wait");
        progressDialog.show();

        firestore.collection("decor_stores")
                .document(store_id)
                .collection("products")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressDialog.dismiss();
                        decor_products=new ArrayList<>();
                        for(DocumentSnapshot doc:queryDocumentSnapshots.getDocuments())
                        {
                            Map<String,Object> data=doc.getData();
                            DecorProduct decorProduct=new DecorProduct();
                            decorProduct.setId(doc.getId());
                            decorProduct.setPrice(data.get("price").toString());
                            decorProduct.setName(data.get("name").toString());
                            decorProduct.setImage_url(data.get("image_url").toString());
                            decorProduct.setFile_url(data.get("file_url").toString());
                            decor_products.add(decorProduct);
                        }
                        DecorsListAdapter adapter=new DecorsListAdapter(decor_products,DecorStoreProductsActivity.this,store_id);
                        recycler_view_decors.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        makeText(DecorStoreProductsActivity.this,"Failed to load decors " , LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        load_decors();
    }
}