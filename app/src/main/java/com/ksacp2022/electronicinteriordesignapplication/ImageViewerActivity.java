package com.ksacp2022.electronicinteriordesignapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ksacp2022.electronicinteriordesignapplication.models.GlideApp;

public class ImageViewerActivity extends AppCompatActivity {

    ImageView image_viewer;
    TextView text_view_description;

    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        image_viewer = findViewById(R.id.image_viewer);
        text_view_description = findViewById(R.id.text_view_description);

        String image_url=getIntent().getStringExtra("image_url");
        String description=getIntent().getStringExtra("description");
        text_view_description.setText(description);

        storage=FirebaseStorage.getInstance();
        StorageReference ref= storage.getReference();
        StorageReference image=ref.child(image_url);

        GlideApp.with(this)
                .load(image)
                .into(image_viewer);







    }
}