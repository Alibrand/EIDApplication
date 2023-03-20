package com.ksacp2022.electronicinteriordesignapplication;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ksacp2022.electronicinteriordesignapplication.models.ArtWork;
import com.ksacp2022.electronicinteriordesignapplication.models.DecorStore;

import java.util.UUID;

public class AdminAddStoreActivity extends AppCompatActivity {

    AppCompatButton button_save;
    ImageView image_view_pic, image_view_take_pic;
    EditText edit_text_name, edit_text_address, edit_text_phone;

    FirebaseFirestore firestore;
    FirebaseStorage storage;
    ProgressDialog progressDialog;

    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_store);
        button_save = findViewById(R.id.button_save);
        image_view_pic = findViewById(R.id.image_view_pic);
        image_view_take_pic = findViewById(R.id.image_view_take_pic);
        edit_text_name = findViewById(R.id.edit_text_name);
        edit_text_phone = findViewById(R.id.edit_text_phone);

        edit_text_address = findViewById(R.id.edit_text_address);


        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(this);
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

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edit_text_name.getText().toString();
                String address = edit_text_address.getText().toString();
                String phone = edit_text_phone.getText().toString();


                if (name.isEmpty()) {
                    edit_text_name.setError("Can not be empty");
                    return;
                }

                if (address.isEmpty()) {
                    edit_text_address.setError("Can not be empty");
                    return;
                }
                if (phone.isEmpty()) {
                    edit_text_phone.setError("Can not be empty");
                    return;
                }
                if (selectedImageUri == null) {
                    Toast.makeText(AdminAddStoreActivity.this, "You should select an image", Toast.LENGTH_LONG).show();
                    return;
                }


                progressDialog.setTitle("Saving");
                progressDialog.setMessage("Please Wait");
                progressDialog.show();


                //define unique image name usinf UUID generator
                String imageName = UUID.randomUUID().toString() + ".jpg";

                // Defining the child of storageReference
                StorageReference storageReference = storage.getReference();
                StorageReference ref = storageReference.child("stores/images/" + imageName);


                // adding listeners on upload
                // or failure of image
                ref.putFile(selectedImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                DecorStore decorStore = new DecorStore();
                                decorStore.setName(name);
                                decorStore.setImage_url(imageName);
                                decorStore.setAddress(address);
                                decorStore.setCreated_at(null);
                                decorStore.setPhone(phone);
                                //save data to firestore
                                firestore.collection("decor_stores")
                                        .add(decorStore)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                progressDialog.dismiss();
                                                makeText(AdminAddStoreActivity.this, "Store Added successfully ", LENGTH_LONG).show();
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                makeText(AdminAddStoreActivity.this, "Failed to save store ", LENGTH_LONG).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                makeText(AdminAddStoreActivity.this, "Failed to upload Image ", LENGTH_LONG).show();
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
                            .into(image_view_pic);

                }
            }
        }
    }
}