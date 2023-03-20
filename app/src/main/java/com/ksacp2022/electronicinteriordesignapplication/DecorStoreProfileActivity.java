package com.ksacp2022.electronicinteriordesignapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ksacp2022.electronicinteriordesignapplication.adapters.DecorsListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.models.ArtWork;
import com.ksacp2022.electronicinteriordesignapplication.models.DecorProduct;
import com.ksacp2022.electronicinteriordesignapplication.models.GlideApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DecorStoreProfileActivity extends AppCompatActivity {

    ImageView image_view_avatar;
    TextView text_view_address,text_view_store_name,see_all;
    AppCompatButton button_call;
    RecyclerView recycler_view_decors ;
    String store_id;
    List<DecorProduct> decors;

    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decor_store_profile);


         store_id=getIntent().getStringExtra("store_id");
        image_view_avatar = findViewById(R.id.image_view_avatar);
        text_view_address = findViewById(R.id.text_view_address);
        text_view_store_name = findViewById(R.id.text_view_store_name);
        see_all = findViewById(R.id.see_all);
        

        button_call = findViewById(R.id.button_call);
        recycler_view_decors = findViewById(R.id.recycler_view_decors);




        firestore=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();
        StorageReference ref=storage.getReference();

        see_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DecorStoreProfileActivity.this,DecorStoreProductsActivity. class);
                intent.putExtra("store_id",store_id);
                startActivity(intent);
            }
        });


        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Getting Profile Info");
        progressDialog.setMessage("Please Wait");
        progressDialog.show();

        firestore.collection("decor_stores")
                .document(store_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressDialog.dismiss();
                        Map<String,Object> data=documentSnapshot.getData();
                        text_view_address.setText(data.get("address").toString());
                        text_view_store_name.setText(data.get("name").toString());
                        String phone=data.get("phone").toString();
                        String image_url=data.get("image_url").toString();




                        StorageReference avatar=ref.child("stores/images/"+ image_url);
                        GlideApp.with(DecorStoreProfileActivity.this)
                                .load(avatar)
                                .circleCrop()
                                .into(image_view_avatar);






                        button_call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:"+phone));
                                startActivity(intent);
                            }
                        });


                        load_products();







                    }


                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(DecorStoreProfileActivity.this,"Failed to get profile info" , Toast.LENGTH_LONG).show();
                        finish();

                    }
                });



    }
    private void load_products() {
        firestore.collection("decor_stores")
                .document(store_id)
                .collection("products")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        decors=new ArrayList<>();
                        for (DocumentSnapshot doc:queryDocumentSnapshots.getDocuments())
                        {
                            Map<String,Object> data=doc.getData();
                            DecorProduct decorProduct=new DecorProduct();
                            decorProduct.setId(doc.getId());
                            decorProduct.setName(data.get("name").toString());
                            decorProduct.setPrice(data.get("price").toString());
                            decorProduct.setImage_url(data.get("image_url").toString());
                            decorProduct.setFile_url(data.get("file_url").toString());
                            decors.add(decorProduct);

                        }
                        //load decors in recyclerview
                        DecorsListAdapter adapter=new DecorsListAdapter(decors,DecorStoreProfileActivity.this,store_id);
                        recycler_view_decors.setAdapter(adapter);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}