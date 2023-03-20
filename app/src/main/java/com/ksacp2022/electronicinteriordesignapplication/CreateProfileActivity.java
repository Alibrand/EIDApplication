package com.ksacp2022.electronicinteriordesignapplication;

import static android.widget.Toast.*;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ksacp2022.electronicinteriordesignapplication.models.DesignerProfile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class CreateProfileActivity extends AppCompatActivity {

    ImageView image_view_avatar,image_view_take_pic;
    TextView text_view_location;
    AppCompatButton button_set_location,button_create_profile;
    EditText edit_text_full_name,edit_text_work_center,edit_text_bio,edit_text_phone;

    Bitmap selected_image;

    Location user_location;
    FusedLocationProviderClient fusedLocationClient;

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseStorage storage;

    ProgressDialog dialog;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        image_view_avatar = findViewById(R.id.image_view_avatar);
        image_view_take_pic = findViewById(R.id.image_view_take_pic);
        text_view_location = findViewById(R.id.text_view_location);
        button_set_location = findViewById(R.id.button_set_location);
        button_create_profile = findViewById(R.id.button_create_profile);
        edit_text_full_name = findViewById(R.id.edit_text_full_name);
        edit_text_work_center = findViewById(R.id.edit_text_work_center);
        edit_text_bio = findViewById(R.id.edit_text_bio);
        edit_text_phone = findViewById(R.id.edit_text_phone);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);



        auth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();

        uid=auth.getUid();

        dialog=new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);


        image_view_take_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (ContextCompat.checkSelfPermission(CreateProfileActivity.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED)
                {
                    ActivityCompat.requestPermissions(CreateProfileActivity.this, new String[] {Manifest.permission.CAMERA}, 1);
                    if (ContextCompat.checkSelfPermission(CreateProfileActivity.this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED)
                        startActivityForResult(takePicture, 0);
                }
                else{
                    startActivityForResult(takePicture, 0);

                }
            }
        });

        button_set_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(CreateProfileActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_DENIED &&
                        ContextCompat.checkSelfPermission(CreateProfileActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_DENIED
                )
                {
                    ActivityCompat.requestPermissions(CreateProfileActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

                    if (ContextCompat.checkSelfPermission(CreateProfileActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(CreateProfileActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED
                    )
                    {
                        set_location();
                    }
                }
                else{
                    set_location();
                }


            }
        });

        button_create_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 String designer_name=edit_text_full_name.getText().toString();
                 String designer_work_center=edit_text_work_center.getText().toString();
                 String designer_bio=edit_text_bio.getText().toString();
                 String designer_phone =edit_text_phone.getText().toString();



                 if(designer_name.isEmpty())
                 {
                     edit_text_full_name.setError("Required Field");
                     return;
                 }
                 if(designer_work_center.isEmpty())
                 {
                     edit_text_work_center.setError("Required Field");
                     return;
                 }
                if(designer_phone.isEmpty())
                {
                    edit_text_phone.setError("Required Field");
                    return;
                }

                if(designer_bio.isEmpty())
                {
                    edit_text_bio.setError("Required Field");
                    return;
                }
                if(user_location==null)
                {
                    makeText(CreateProfileActivity.this,"You should set the location" , LENGTH_LONG).show();
                return;
                }

                if(selected_image==null)
                {
                    makeText(CreateProfileActivity.this,"You should select a profile picture" , LENGTH_LONG).show();
                    return;
                }


                //prepare image to upload
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selected_image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                //define unique image name usinf UUID generator
                String imageName = UUID.randomUUID().toString()+".jpg";

                // Defining the child of storageReference
                StorageReference storageReference =storage.getReference();
                StorageReference ref = storageReference.child("avatars/"+imageName);

                //show waiting
                dialog.setTitle("Uploading Avatar Image");
                dialog.setMessage("Please Wait");
                dialog.show();

                // adding listeners on upload
                // or failure of image
                ref.putBytes(data)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                dialog.setTitle("Uploading Profile Data");

                                                //prepare profile data
                                                DesignerProfile new_profile=new DesignerProfile();
                                                new_profile.setFull_name(designer_name);
                                                new_profile.setWork_center(designer_work_center);
                                                new_profile.setBio(designer_bio);
                                                new_profile.setPhone(designer_phone);
                                                GeoPoint point=new GeoPoint(user_location.getLatitude(),user_location.getLongitude());
                                                new_profile.setLocation(point);
                                                new_profile.setAvatar_url(imageName);

                                                //save data to firestore
                                                firestore.collection("designer_profile")
                                                        .document(uid)
                                                        .set(new_profile)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                dialog.dismiss();
                                                                makeText(CreateProfileActivity.this,"Designer Profile Created Successfully " , LENGTH_LONG).show();
                                                                Intent intent = new Intent(CreateProfileActivity.this,DesignerMenuActivity.
                                                                class);
                                                                startActivity(intent);
                                                                finish();


                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                makeText(CreateProfileActivity.this,"Failed to save profile " , LENGTH_LONG).show();
                                                                dialog.dismiss();
                                                            }
                                                        });




                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                makeText(CreateProfileActivity.this,"Failed to upload Image " , LENGTH_LONG).show();
                            dialog.dismiss();
                            }
                        });




            }
        });






    }

    private void set_location() {
        makeText(CreateProfileActivity.this,"Finding Location....." , LENGTH_LONG).show();
        if (ContextCompat.checkSelfPermission(CreateProfileActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(CreateProfileActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    user_location = location;
                    text_view_location.setText("Location Provided Successfully");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK && data != null
                         ){
                    selected_image = (Bitmap)data.getExtras().get("data");
                        Glide.with(this)
                                .load(selected_image)
                                .apply(RequestOptions.circleCropTransform())
                                .into(image_view_avatar);


                }

                break;

        }
    }
}