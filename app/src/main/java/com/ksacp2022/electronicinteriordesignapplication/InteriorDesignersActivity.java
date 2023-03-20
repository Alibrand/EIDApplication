package com.ksacp2022.electronicinteriordesignapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022.electronicinteriordesignapplication.adapters.DesignersListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.models.DesignerProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InteriorDesignersActivity extends AppCompatActivity {

    RecyclerView recycler_view_designers;
    ImageButton button_search;
    EditText edit_text_search;
    List<DesignerProfile> profileList;
    List<DesignerProfile> filteredList;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interior_designers);
        recycler_view_designers = findViewById(R.id.recycler_view_designers);
        button_search = findViewById(R.id.button_search);
        edit_text_search = findViewById(R.id.edit_text_search);


        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        dialog=new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setTitle("Getting Designers");
        dialog.setMessage("Please Wait");
        dialog.show();

        profileList=new ArrayList<DesignerProfile>();

        load_all();

        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search_text=edit_text_search.getText().toString();
                if(search_text.isEmpty())
                {
                    DesignersListAdapter adapter=new DesignersListAdapter(profileList,InteriorDesignersActivity.this);
                    recycler_view_designers.setAdapter(adapter);
                }else{
                    filteredList=new ArrayList<>();
                    for (DesignerProfile profile:profileList
                         ) {
                        if(profile.getFull_name().toLowerCase(Locale.ROOT).contains(search_text.toLowerCase(Locale.ROOT)))
                            filteredList.add(profile);
                    }
                    DesignersListAdapter adapter=new DesignersListAdapter(filteredList,InteriorDesignersActivity.this);
                    recycler_view_designers.setAdapter(adapter);
                }


            }
        });




    }

    private void load_all() {
        firestore.collection("designer_profile")
                .whereNotEqualTo(FieldPath.documentId(),firebaseAuth.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot doc:queryDocumentSnapshots.getDocuments()
                        ) {
                            Map<String,Object> data=doc.getData();
                            DesignerProfile profile=new DesignerProfile();
                            profile.setId(doc.getId());
                            profile.setFull_name(data.get("full_name").toString());
                            profile.setAvatar_url(data.get("avatar_url").toString());
                            List<String> likes=new ArrayList<>();
                            if(data.get("likes")!=null)
                                likes= (List<String>) data.get("likes");
                            profile.setLikes(likes);
                            profileList.add(profile) ;
                        }

                        DesignersListAdapter adapter=new DesignersListAdapter(profileList,InteriorDesignersActivity.this);
                        recycler_view_designers.setAdapter(adapter);
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(InteriorDesignersActivity.this, "Error while getting data", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }


}