package com.ksacp2022.electronicinteriordesignapplication;

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
import com.ksacp2022.electronicinteriordesignapplication.adapters.AdminStoresAdapter;
import com.ksacp2022.electronicinteriordesignapplication.adapters.DecorStoresListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.models.DecorStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminStoresActivity extends AppCompatActivity {

    List<DecorStore> decorStoreList;
    RecyclerView recycler_view_decor_stores;

    AppCompatButton button_add_store;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_stores);
        recycler_view_decor_stores = findViewById(R.id.recycler_view_decor_stores);
        button_add_store = findViewById(R.id.button_add_store);

        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        load_stores();


        button_add_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminStoresActivity.this,AdminAddStoreActivity. class);
                startActivity(intent);
            }
        });










    }

    private void load_stores() {
        progressDialog.setMessage("Loading");
        progressDialog.show();


        firestore.collection("decor_stores")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        decorStoreList=new ArrayList<DecorStore>();
                        progressDialog.dismiss();
                        for (DocumentSnapshot doc:queryDocumentSnapshots.getDocuments()
                             ) {
                            Map<String,Object> data=doc.getData();
                            DecorStore decorStore=new DecorStore();
                            decorStore.setImage_url(data.get("image_url").toString());
                            decorStore.setName(data.get("name").toString());
                            decorStore.setId(doc.getId());
                            decorStoreList.add(decorStore);

                        }

                        AdminStoresAdapter adapter=new AdminStoresAdapter(decorStoreList,AdminStoresActivity.this);
                        recycler_view_decor_stores.setAdapter(adapter);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminStoresActivity.this,"Failed to load stores" , Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        load_stores();
    }
}