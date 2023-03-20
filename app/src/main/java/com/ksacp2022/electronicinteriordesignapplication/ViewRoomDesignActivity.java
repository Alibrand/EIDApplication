package com.ksacp2022.electronicinteriordesignapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022.electronicinteriordesignapplication.adapters.ColorsListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.adapters.DesignImagesListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewRoomDesignActivity extends AppCompatActivity {

    TextView text_view_creator,text_view_name,text_view_description;
    RecyclerView recycler_view_images,recycler_view_colors;

    FirebaseFirestore firestore;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_room_desing);
        text_view_creator = findViewById(R.id.text_view_creator);
        text_view_name = findViewById(R.id.text_view_name);
        text_view_description = findViewById(R.id.text_view_description);
        recycler_view_images = findViewById(R.id.recycler_view_images);
        recycler_view_colors = findViewById(R.id.recycler_view_colors);

        String design_id=getIntent().getStringExtra("design_id");

        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Loading Design Info");
        progressDialog.setMessage("Please Wait");
        progressDialog.show();


        firestore.collection("room_designs")
                .document(design_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String,Object> data=documentSnapshot.getData();
                        text_view_creator.setText(data.get("creator_name").toString());
                        text_view_name.setText(data.get("name").toString());
                        text_view_description.setText(data.get("description").toString());
                        List<Long> long_colors= (List<Long>) data.get("colors");
                        List<Integer> colors=new ArrayList<>();
                        for (Long color:long_colors
                             ) {
                            colors.add(color.intValue());
                        }

                        ColorsListAdapter adapter=new ColorsListAdapter(colors);
                        recycler_view_colors.setAdapter(adapter);
                        List<String> images= (List<String>) data.get("images");
                        DesignImagesListAdapter adapter1=new DesignImagesListAdapter(images,ViewRoomDesignActivity.this);
                        recycler_view_images.setAdapter(adapter1);
                        progressDialog.dismiss();



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ViewRoomDesignActivity.this,"Failed to get info" , Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        finish();

                    }
                });





    }
}