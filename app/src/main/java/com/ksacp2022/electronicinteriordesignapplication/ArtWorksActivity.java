package com.ksacp2022.electronicinteriordesignapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022.electronicinteriordesignapplication.adapters.ArtWorkListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.adapters.DesignerArtWorkListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.models.ArtWork;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArtWorksActivity extends AppCompatActivity {

    RecyclerView recycler_view_art_works;

    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    List<ArtWork> artWorkList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_art_works);
        recycler_view_art_works = findViewById(R.id.recycler_view_art_works);

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Loading Art Works");
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        firestore=FirebaseFirestore.getInstance();

        String designer_id=getIntent().getStringExtra("designer_id");

        firestore.collection("designer_profile")
                .document(designer_id)
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
                            artWork.setDesigner_id(designer_id);
                            artWork.setId(doc.getId());
                            artWork.setDescription(data.get("description").toString());
                            artWork.setImage_url(data.get("image_url").toString());

                            artWorkList.add(artWork);


                        }

                        ArtWorkListAdapter adapter=new ArtWorkListAdapter(artWorkList,ArtWorksActivity.this);
                        recycler_view_art_works.setAdapter(adapter);
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ArtWorksActivity.this,"Failed to load works" , Toast.LENGTH_LONG).show();
                        finish();
                    }
                });


    }
}