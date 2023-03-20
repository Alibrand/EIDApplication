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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022.electronicinteriordesignapplication.adapters.MyDesignsListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.adapters.RoomDesignsListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.models.RoomDesign;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyDesignsActivity extends AppCompatActivity {

    RecyclerView recycler_view_designs;


    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    List<RoomDesign> roomDesignList;
    String share_with_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_designs);
        recycler_view_designs = findViewById(R.id.recycler_view_designs);


        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        share_with_id=getIntent().getStringExtra("share_with_id");




        load_my_designs();


    }
    private void load_my_designs() {
        progressDialog.setTitle("Loading Designs");
        progressDialog.setMessage("Please Wait");
        progressDialog.show();

        firestore.collection("room_designs")
                .whereEqualTo("creator_id",firebaseAuth.getUid())
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        roomDesignList =new ArrayList<RoomDesign>();
                        for (DocumentSnapshot doc:queryDocumentSnapshots.getDocuments()
                        ) {
                            Map<String,Object> data=doc.getData();
                            RoomDesign roomDesign=new RoomDesign();
                            roomDesign.setId(doc.getId());
                            roomDesign.setName(data.get("name").toString());
                            roomDesign.setImages((List<String>) data.get("images"));
                            roomDesign.setShare_list((List<String>) data.get("share_list"));

                            roomDesignList.add(roomDesign);

                        }

                        MyDesignsListAdapter adapter=new MyDesignsListAdapter(roomDesignList,MyDesignsActivity.this,share_with_id);
                        recycler_view_designs.setAdapter(adapter);
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MyDesignsActivity.this,"Failed to load design" , Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        load_my_designs();
    }
}