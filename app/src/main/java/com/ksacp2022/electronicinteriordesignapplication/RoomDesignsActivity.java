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
import com.ksacp2022.electronicinteriordesignapplication.adapters.RoomDesignsListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.models.RoomDesign;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoomDesignsActivity extends AppCompatActivity {
    RecyclerView recycler_view_designs;
    AppCompatButton button_add_room_design;

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    List<RoomDesign> roomDesignList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_designs);
        recycler_view_designs = findViewById(R.id.recycler_view_designs);
        button_add_room_design = findViewById(R.id.button_add_room_design);

        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);




        load_my_designs();

        button_add_room_design.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RoomDesignsActivity.this,AddRoomDesignActivity. class);

                startActivity(intent);
            }
        });
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

                            roomDesignList.add(roomDesign);

                        }

                        RoomDesignsListAdapter adapter=new RoomDesignsListAdapter(roomDesignList,RoomDesignsActivity.this);
                        recycler_view_designs.setAdapter(adapter);
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RoomDesignsActivity.this,"Failed to load design" , Toast.LENGTH_LONG).show();
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