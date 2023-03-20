package com.ksacp2022.electronicinteriordesignapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ksacp2022.electronicinteriordesignapplication.models.DesignerProfile;
import com.ksacp2022.electronicinteriordesignapplication.models.GlideApp;
import com.ksacp2022.electronicinteriordesignapplication.models.UserProfile;

import java.util.Map;

public class DesignerMenuActivity extends AppCompatActivity {

    ImageView image_view_avatar;
    TextView text_view_designer_name;
    AppCompatButton button_art_works,button_edit_profile,button_reviews
            ,button_inbox;

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designer_menu);
        image_view_avatar = findViewById(R.id.image_view_avatar);
        text_view_designer_name = findViewById(R.id.text_view_designer_name);
        button_art_works = findViewById(R.id.button_art_works);
        button_edit_profile = findViewById(R.id.button_edit_profile);
        button_reviews = findViewById(R.id.button_reviews);
        button_inbox = findViewById(R.id.button_inbox);







        auth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();

        dialog=new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);


        dialog.setTitle("Getting Designer info");
        dialog.setMessage("Please Wait");
        dialog.show();

        String uid=auth.getUid();

        firestore.collection("designer_profile")
                .document(uid)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String,Object> data=documentSnapshot.getData();

                        DesignerProfile profile=new DesignerProfile();
                        profile.setFull_name(data.get("full_name").toString());
                        profile.setAvatar_url(data.get("avatar_url").toString());


                        text_view_designer_name.setText(profile.getFull_name());
                        StorageReference ref=storage.getReference();
                        StorageReference avatar=ref.child("avatars/"+profile.getAvatar_url());


                        GlideApp.with(DesignerMenuActivity.this)
                                .load(avatar)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.pic_circle)
                                .apply(RequestOptions.circleCropTransform())
                                .into(image_view_avatar);
                        dialog.dismiss();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DesignerMenuActivity.this,"Failed to get info" , Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        finish();

                    }
                });

        button_art_works.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DesignerMenuActivity.this,DesignerArtWorksActivity. class);
                startActivity(intent);
            }
        });

        button_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DesignerMenuActivity.this,EditProfileActivity. class);
                startActivity(intent);
            }
        });

        button_reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DesignerMenuActivity.this,DesignerMyReviewsActivity. class);
                startActivity(intent);
            }
        });

        button_inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DesignerMenuActivity.this,InboxActivity. class);
                startActivity(intent);
            }
        });





    }
}