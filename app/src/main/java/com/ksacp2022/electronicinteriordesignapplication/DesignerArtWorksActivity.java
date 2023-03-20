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
import com.ksacp2022.electronicinteriordesignapplication.adapters.DesignerArtWorkListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.models.ArtWork;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DesignerArtWorksActivity extends AppCompatActivity {

    RecyclerView recycler_view_art_works;
    AppCompatButton button_add_art_work;

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    List<ArtWork> artWorkList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designer_art_works);
        recycler_view_art_works = findViewById(R.id.recycler_view_art_works);
        button_add_art_work = findViewById(R.id.button_add_art_work);

        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();





        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        get_art_works();

        button_add_art_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DesignerArtWorksActivity.this,AddWorkArtActivity. class);
                startActivity(intent);
            }
        });


    }

    private void get_art_works() {
        progressDialog.setTitle("Loading Works");
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        String uid=firebaseAuth.getUid();

        firestore.collection("designer_profile")
                .document(uid)
                .collection("art_works")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        artWorkList=new ArrayList<ArtWork>();
                        for (DocumentSnapshot doc:queryDocumentSnapshots.getDocuments()
                        ) {
                            Map<String,Object> data=doc.getData();
                            ArtWork artWork=new ArtWork();
                            artWork.setDesigner_id(uid);
                            artWork.setId(doc.getId());
                            artWork.setDescription(data.get("description").toString());
                            artWork.setImage_url(data.get("image_url").toString());

                            artWorkList.add(artWork);


                        }

                        DesignerArtWorkListAdapter adapter=new DesignerArtWorkListAdapter(artWorkList,DesignerArtWorksActivity.this);
                        recycler_view_art_works.setAdapter(adapter);
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DesignerArtWorksActivity.this,"Failed to load works" , Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        get_art_works();
    }
}