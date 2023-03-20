package com.ksacp2022.electronicinteriordesignapplication;

import static android.widget.Toast.*;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ksacp2022.electronicinteriordesignapplication.models.DecorProduct;
import com.ksacp2022.electronicinteriordesignapplication.models.DecorStore;

import java.util.UUID;

public class AdminAddDecorProductActivity extends AppCompatActivity {
    AppCompatButton button_save,upload_model;
    TextView file_status;
    ImageView image_view_pic,image_view_take_pic;
    EditText edit_text_name,edit_text_price;

    FirebaseFirestore firestore;
    FirebaseStorage storage;
    ProgressDialog progressDialog;

    Uri selectedImageUri,modelFileUri;
    String store_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_decor_product);
        button_save = findViewById(R.id.button_save);
        image_view_pic = findViewById(R.id.image_view_pic);
        image_view_take_pic = findViewById(R.id.image_view_take_pic);
        edit_text_name = findViewById(R.id.edit_text_name);
        file_status = findViewById(R.id.file_status);
        edit_text_price = findViewById(R.id.edit_text_price);
        upload_model = findViewById(R.id.upload_model);


        firestore=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        store_id=getIntent().getStringExtra("store_id");

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

        upload_model.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create an instance of the
                // intent of the type image
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                // pass the constant to compare it
                // with the returned requestCode
                startActivityForResult(Intent.createChooser(intent, "Select Model File"), 120);

            }
        });

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=edit_text_name.getText().toString();
                String price=edit_text_price.getText().toString();




                if(name.isEmpty())
                {
                    edit_text_name.setError("Can not be empty");
                    return;
                }

                if(price.isEmpty())
                {
                    edit_text_price.setError("Can not be empty");
                    return;
                }

                if(selectedImageUri==null)
                {
                    makeText(AdminAddDecorProductActivity.this,"You should select an image" , LENGTH_LONG).show();
                    return;
                }


                progressDialog.setTitle("Uploading Image");
                progressDialog.setMessage("Please Wait");
                progressDialog.show();


                //define unique image name usinf UUID generator
                String imageName = UUID.randomUUID().toString()+".jpg";

                // Defining the child of storageReference
                StorageReference storageReference =storage.getReference();
                StorageReference ref = storageReference.child("stores/decors/"+imageName);



                // adding listeners on upload
                // or failure of image
                ref.putFile(selectedImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                DecorProduct decorProduct=new DecorProduct();
                                decorProduct.setName(name);
                                decorProduct.setImage_url(imageName);
                                decorProduct.setPrice(price);
                                decorProduct.setCreated_at(null);
                                decorProduct.setFile_url(null);


                                if(modelFileUri!=null)
                                {
                                    progressDialog.setTitle("Saving Model File");
                                    progressDialog.setMessage("Please Wait");



                                    //define unique image name usinf UUID generator
                                    String fileName = UUID.randomUUID().toString()+".glb";


                                    StorageReference ref = storageReference.child("3d_models/"+fileName);

                                    ref.putFile(modelFileUri)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    decorProduct.setFile_url(fileName);
                                                    save_product(decorProduct);


                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    makeText(AdminAddDecorProductActivity.this,"Failed to save product" , LENGTH_LONG).show();
                                                }
                                            })
                                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                                    double progress=100*snapshot.getBytesTransferred()/snapshot.getTotalByteCount();
                                                    progressDialog.setMessage("Uploaded "+(int)progress+"%");
                                                }
                                            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onPaused(@NonNull UploadTask.TaskSnapshot snapshot) {
                                                    progressDialog.setMessage("Uploading Paused");
                                                }
                                            });
                                }
                                else
                                    save_product(decorProduct);












                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                makeText(AdminAddDecorProductActivity.this,"Failed to upload Image " , LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });



            }
        });



    }

    private void save_product(DecorProduct decorProduct){
        progressDialog.setTitle("Saving Info");
        progressDialog.setMessage("Please Wait");



//save data to firestore
        firestore.collection("decor_stores")
                .document(store_id)
                .collection("products")
                .add(decorProduct)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressDialog.dismiss();
                        makeText(AdminAddDecorProductActivity.this,"Product Added successfully " , LENGTH_LONG).show();
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeText(AdminAddDecorProductActivity.this,"Failed to save product " , LENGTH_LONG).show();
                        progressDialog.dismiss();
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
                            .transform(new CenterCrop(),new RoundedCorners(15))
                            .into(image_view_pic);

                }
            }
            else if(requestCode==120)
            {
                modelFileUri=data.getData();
                if(modelFileUri.getPath().contains(".glb"))
                file_status.setText("Model File :"+modelFileUri.getLastPathSegment());
                else {
                    modelFileUri=null;
                    file_status.setText("Only GLB files are allowed");
                    makeText(AdminAddDecorProductActivity.this,"Error : Only GLB files are allowed" , LENGTH_LONG).show();
                }

            }
        }
    }
}