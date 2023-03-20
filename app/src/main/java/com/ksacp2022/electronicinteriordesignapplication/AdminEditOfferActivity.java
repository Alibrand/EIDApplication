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
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ksacp2022.electronicinteriordesignapplication.adapters.AdminDecorOffersListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.adapters.AdminOfferImagesListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.adapters.OfferImagesListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.adapters.RoomDesignImagesListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.models.DecorOffer;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdminEditOfferActivity extends AppCompatActivity {
    EditText edit_text_title, edit_text_new_price, edit_text_original_price, edit_text_description,
            edit_text_address, edit_text_phone;
    AppCompatButton button_save;
    ImageButton button_take_pic, button_gallery;

    RecyclerView recycler_view_images;


    List<String> images = new ArrayList<>();
    List<Uri> imagesUri = new ArrayList<Uri>();

    String offer_id;

    FirebaseStorage storage;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_offer);
        edit_text_title = findViewById(R.id.edit_text_title);
        edit_text_description = findViewById(R.id.edit_text_description);
        button_take_pic = findViewById(R.id.button_take_pic);
        button_gallery = findViewById(R.id.button_gallery);
        recycler_view_images = findViewById(R.id.recycler_view_images);
        button_save = findViewById(R.id.button_save);
        edit_text_new_price = findViewById(R.id.edit_text_new_price);
        edit_text_original_price = findViewById(R.id.edit_text_original_price);
        edit_text_address = findViewById(R.id.edit_text_address);
        edit_text_phone = findViewById(R.id.edit_text_phone);


        offer_id = getIntent().getStringExtra("offer_id");


        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.show();
        progressDialog.setTitle("Loading Offer info");
        progressDialog.setMessage("Please Wait");

        firestore.collection("decor_offers")
                .document(offer_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressDialog.dismiss();
                        Map<String, Object> data = documentSnapshot.getData();
                        edit_text_title.setText(data.get("title").toString());
                        edit_text_address.setText(data.get("address").toString());
                        edit_text_description.setText(data.get("description").toString());
                        edit_text_new_price.setText(data.get("new_price").toString());
                        edit_text_original_price.setText(data.get("original_price").toString());
                        edit_text_phone.setText(data.get("phone").toString());
                        images = (List<String>) data.get("images");


                        AdminOfferImagesListAdapter adapter = new AdminOfferImagesListAdapter(images, AdminEditOfferActivity.this);
                        recycler_view_images.setAdapter(adapter);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeText(AdminEditOfferActivity.this, "Failed to load info", LENGTH_LONG).show();

                        progressDialog.dismiss();
                        finish();
                    }
                });


        button_take_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (ContextCompat.checkSelfPermission(AdminEditOfferActivity.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(AdminEditOfferActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                    if (ContextCompat.checkSelfPermission(AdminEditOfferActivity.this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED)
                        startActivityForResult(takePicture, 100);
                } else {
                    startActivityForResult(takePicture, 100);

                }
            }
        });

        button_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create an instance of the
                // intent of the type image
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                // pass the constant to compare it
                // with the returned requestCode
                startActivityForResult(Intent.createChooser(intent, "Select Pictures"), 110);

            }
        });

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = edit_text_title.getText().toString();
                String description = edit_text_description.getText().toString();
                String new_price = edit_text_new_price.getText().toString();
                String original_price = edit_text_original_price.getText().toString();
                String address = edit_text_address.getText().toString();
                String phone = edit_text_phone.getText().toString();


                if (title.isEmpty()) {
                    edit_text_title.setError("Empty Field");
                    return;
                }
                if (description.isEmpty()) {
                    edit_text_description.setError("Empty Field");
                    return;
                }
                if (address.isEmpty()) {
                    edit_text_address.setError("Empty Field");
                    return;
                }

                if (new_price.isEmpty()) {
                    edit_text_new_price.setError("Empty Field");
                    return;
                }
                if (original_price.isEmpty()) {
                    edit_text_original_price.setError("Empty Field");
                    return;
                }

                if (phone.isEmpty()) {
                    edit_text_phone.setError("Empty Field");
                    return;
                }


                if (images.size() == 0) {
                    makeText(AdminEditOfferActivity.this, "You should select one image at least for the room", LENGTH_LONG).show();
                    return;
                }


                Map<String,Object> new_data=new HashMap<>();

                new_data.put("title",title);
                new_data.put("description",description);
                new_data.put("new_price",new_price);
                new_data.put("original_price",original_price);
                new_data.put("address",address);
                new_data.put("phone",phone);
                new_data.put("images",images);

                progressDialog.setTitle("Uploading Data");
                progressDialog.setMessage("Please Wait");
                progressDialog.show();

                firestore.collection("decor_offers")
                        .document(offer_id)
                        .update(new_data)
                        .addOnSuccessListener(new  OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                makeText(AdminEditOfferActivity.this, "Offer has been saved successfully", LENGTH_LONG).show();
                                progressDialog.dismiss();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                makeText(AdminEditOfferActivity.this, "Failed to save offer", LENGTH_LONG).show();
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
                Uri imageurl = data.getData();
                //define unique image name usinf UUID generator
                String imageName = UUID.randomUUID().toString() + ".jpg";

                // Defining the child of storageReference
                StorageReference storageReference = storage.getReference();
                StorageReference ref = storageReference.child("offers_images/" + imageName);
                progressDialog.setTitle("Uploading Images");
                progressDialog.setMessage("Please Wait");
                progressDialog.show();
                progressDialog.setTitle("Uploading Images");
                ref.putFile(imageurl)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                imagesUri.add(imageurl);
                                images.add(imageName);
                                AdminOfferImagesListAdapter adapter = new AdminOfferImagesListAdapter(images, AdminEditOfferActivity.this);
                                recycler_view_images.setAdapter(adapter);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                makeText(AdminEditOfferActivity.this, "Failed to upload image", LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });


            } else if (requestCode == 100) {

                Bitmap image = (Bitmap) data.getExtras().get("data");
                //prepare image to upload
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                String path = MediaStore.Images.Media.insertImage(getContentResolver(), image, "Title", null);

                //define unique image name usinf UUID generator
                String imageName = UUID.randomUUID().toString() + ".jpg";

                // Defining the child of storageReference
                StorageReference storageReference = storage.getReference();
                StorageReference ref = storageReference.child("offers_images/" + imageName);
                progressDialog.setTitle("Uploading Images");
                progressDialog.setMessage("Please Wait");
                progressDialog.show();
                progressDialog.setTitle("Uploading Images");
                ref.putFile(Uri.parse(path))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                imagesUri.add(Uri.parse(path));
                                images.add(imageName);
                                AdminOfferImagesListAdapter adapter = new AdminOfferImagesListAdapter(images, AdminEditOfferActivity.this);
                                recycler_view_images.setAdapter(adapter);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                makeText(AdminEditOfferActivity.this, "Failed to upload image", LENGTH_LONG).show();
                            }
                        });


            }

        }


    }
}