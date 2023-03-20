package com.ksacp2022.electronicinteriordesignapplication;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ksacp2022.electronicinteriordesignapplication.models.ArtWork;
import com.ksacp2022.electronicinteriordesignapplication.models.DesignerProfile;

import java.util.UUID;

public class AddWorkArtActivity extends AppCompatActivity {

    AppCompatButton button_save_art_work;
    ImageView image_view_avatar,image_view_take_pic;
    EditText edit_text_description;

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseStorage storage;
    ProgressDialog progressDialog;

    Uri selectedImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_work_art);
        button_save_art_work = findViewById(R.id.button_save_art_work);
        image_view_avatar = findViewById(R.id.image_view_avatar);
        image_view_take_pic = findViewById(R.id.image_view_take_pic);
        edit_text_description = findViewById(R.id.edit_text_description);

        firestore=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this) ;
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);



        image_view_take_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create an instance of the
                // intent of the type image
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                // pass the constant to compare it
                // with the returned requestCode
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 110);

            }
        });

        button_save_art_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String description=edit_text_description.getText().toString();


                if(description.isEmpty())
                {
                    edit_text_description.setError("Can not be empty");
                    return;
                }
                if(selectedImageUri==null)
                {
                    Toast.makeText(AddWorkArtActivity.this,"You should select an image" , Toast.LENGTH_LONG).show();
                 return;
                }


                progressDialog.setTitle("Saving Art Work");
                progressDialog.setMessage("Please Wait");
                progressDialog.show();


                //define unique image name usinf UUID generator
                String imageName = UUID.randomUUID().toString()+".jpg";

                // Defining the child of storageReference
                StorageReference storageReference =storage.getReference();
                StorageReference ref = storageReference.child("art_works/"+imageName);



                // adding listeners on upload
                // or failure of image
                ref.putFile(selectedImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                String uid=firebaseAuth.getUid();
                                ArtWork artWork=new ArtWork();
                                artWork.setDescription(description);
                                artWork.setImage_url(imageName);
                                long timestamp=System.currentTimeMillis();
                                artWork.setTimestamp(timestamp);
                                artWork.setDesigner_id(uid);


                                //save data to firestore
                                firestore.collection("designer_profile")
                                        .document(uid)
                                        .collection("art_works")
                                        .add(artWork)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                progressDialog.dismiss();
                                                makeText(AddWorkArtActivity.this,"Work Art Added successfully " , LENGTH_LONG).show();

                                                finish();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                makeText(AddWorkArtActivity.this,"Failed to save art work " , LENGTH_LONG).show();
                                                progressDialog.dismiss();
                                            }
                                        });




                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                makeText(AddWorkArtActivity.this,"Failed to upload Image " , LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });



            }
        });










    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == 110) {
                // Get the url of the image from data
                 selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                  //selected_image = (Bitmap)data.getExtras().get("data");
                    Glide.with(this)
                            .load(selectedImageUri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(image_view_avatar);

                }
            }
        }
    }
}