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
import android.location.Location;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ksacp2022.electronicinteriordesignapplication.models.DecorStore;
import com.ksacp2022.electronicinteriordesignapplication.models.GlideApp;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AdminEditDecorStoreActivity extends AppCompatActivity {

    AppCompatButton button_save,button_decors;
    ImageView image_view_pic,image_view_take_pic;
    EditText edit_text_name,edit_text_address,edit_text_phone;

    FirebaseFirestore firestore;
    FirebaseStorage storage;
    ProgressDialog progressDialog;

    Uri selectedImageUri;
    String store_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_decor_store);
        button_save = findViewById(R.id.button_save);
        image_view_pic = findViewById(R.id.image_view_pic);
        image_view_take_pic = findViewById(R.id.image_view_take_pic);
        edit_text_name = findViewById(R.id.edit_text_name);
        edit_text_phone = findViewById(R.id.edit_text_phone);
        button_decors = findViewById(R.id.button_decors);
        edit_text_address = findViewById(R.id.edit_text_address);


         store_id=getIntent().getStringExtra("store_id");




        firestore=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);



        //load info
        get_store_info();


        button_decors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminEditDecorStoreActivity.this,AdminStoreDecorsActivity. class);
                intent.putExtra("store_id",store_id);
                startActivity(intent);
            }
        });





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
                String name=edit_text_name.getText().toString();
                String address=edit_text_address.getText().toString();
                String phone =edit_text_phone.getText().toString();



                if(name.isEmpty())
                {
                    edit_text_name.setError("Can not be empty");
                    return;
                }

                if(address.isEmpty())
                {
                    edit_text_address.setError("Can not be empty");
                    return;
                }
                if(phone.isEmpty())
                {
                    edit_text_phone.setError("Can not be empty");
                    return;
                }

                Map<String,Object> new_Data=new HashMap<>();
                new_Data.put("name",name);
                new_Data.put("address",address);
                new_Data.put("phone",phone);





                progressDialog.setTitle("Saving");
                progressDialog.setMessage("Please Wait");
                progressDialog.show();


                //if user have not changed his avatar
                if(selectedImageUri==null) {
                    update_profile_info(new_Data);
                    return;
                }


                //define unique image name usinf UUID generator
                String imageName = UUID.randomUUID().toString()+".jpg";

                // Defining the child of storageReference
                StorageReference storageReference =storage.getReference();
                StorageReference ref = storageReference.child("stores/images/"+imageName);



                // adding listeners on upload
                // or failure of image
                ref.putFile(selectedImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                new_Data.put("image_url",imageName);
                                update_profile_info(new_Data);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                makeText(AdminEditDecorStoreActivity.this,"Failed to upload Image " , LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });



            }
        });



    }

    private void update_profile_info(Map<String,Object> new_Data) {
        //save data to firestore
        firestore.collection("decor_stores")
                .document(store_id)
                .update(new_Data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        makeText(AdminEditDecorStoreActivity.this,"Changes saved successfully" , LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeText(AdminEditDecorStoreActivity.this,"Failed to save changes" , LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });
    }

    private void get_store_info() {
        progressDialog.setTitle("Loading Store Info");
        progressDialog.setMessage("Please Wait");
        progressDialog.show();



        firestore.collection("decor_stores")
                .document(store_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String,Object> data=documentSnapshot.getData();
                        edit_text_name.setText(data.get("name").toString());
                        edit_text_address.setText(data.get("address").toString());
                        edit_text_phone.setText(data.get("phone").toString());
                        String image_url=data.get("image_url").toString();
                        StorageReference reference=storage.getReference();
                        StorageReference image=reference.child("stores/images/"+image_url);
                        GlideApp.with(AdminEditDecorStoreActivity.this)
                                .load(image)
                                .circleCrop()
                                .into(image_view_pic);

                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeText(AdminEditDecorStoreActivity.this,"Couldn't get store info" , LENGTH_LONG).show();
                        finish();
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