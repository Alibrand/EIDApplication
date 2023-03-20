package com.ksacp2022.electronicinteriordesignapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022.electronicinteriordesignapplication.adapters.RoomDesignsListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.adapters.SharedDesignsListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.models.RoomDesign;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SharedDesignsActivity extends AppCompatActivity {
    RecyclerView recycler_view_designs;
    AppCompatButton button_share_design;
    TextView text_title;

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    List<RoomDesign> roomDesignList;
    String share_with_id,share_with_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_designs);
        recycler_view_designs = findViewById(R.id.recycler_view_designs);
        button_share_design = findViewById(R.id.button_share_design);
        text_title = findViewById(R.id.text_title);


        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        share_with_id=getIntent().getStringExtra("share_with_id");
        share_with_name=getIntent().getStringExtra("share_with_name");

        text_title.setText("Shared Designs with "+share_with_name);




        load_my_designs();

        button_share_design.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SharedDesignsActivity.this,MyDesignsActivity. class);
                intent.putExtra("share_with_id",share_with_id);
                startActivity(intent);
            }
        });
    }
    private void load_my_designs() {
        progressDialog.setTitle("Loading Designs");
        progressDialog.setMessage("Please Wait");
        progressDialog.show();

        Log.d("arrrr",share_with_id);
        Log.d("arrrr",firebaseAuth.getUid());

        firestore.collection("room_designs")
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
                            roomDesign.setCreator_name(data.get("creator_name").toString());
                            roomDesign.setCreator_id(data.get("creator_id").toString());
                            roomDesign.setImages((List<String>) data.get("images"));
                            List<String> share_list= (List<String>) data.get("share_list");
                            //if the design not hared between the users
                            if(!share_list.containsAll(Arrays.asList(share_with_id,firebaseAuth.getUid())))
                                continue;
                            roomDesign.setShare_list(share_list);

                            roomDesignList.add(roomDesign);

                        }

                        SharedDesignsListAdapter adapter=new SharedDesignsListAdapter(roomDesignList,SharedDesignsActivity.this,share_with_id);
                        recycler_view_designs.setAdapter(adapter);
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(SharedDesignsActivity.this,"Failed to load design" , Toast.LENGTH_LONG).show();
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