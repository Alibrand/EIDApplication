package com.ksacp2022.electronicinteriordesignapplication;

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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ksacp2022.electronicinteriordesignapplication.adapters.ColorsListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.adapters.RoomDesignImagesListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.models.DecorOffer;
import com.ksacp2022.electronicinteriordesignapplication.models.RoomDesign;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.flag.BubbleFlag;
import com.skydoves.colorpickerview.flag.FlagMode;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdminAddOffersActivity extends AppCompatActivity {
    EditText edit_text_title,edit_text_new_price,edit_text_original_price,edit_text_description,
    edit_text_address,edit_text_phone;
    AppCompatButton button_save;
    ImageButton button_take_pic,button_gallery;

    RecyclerView recycler_view_images;


    List<String> images=new ArrayList<>();
    List<Uri> imagesUri=new ArrayList<Uri>();

    FirebaseStorage storage;
    FirebaseFirestore firestore;
    ProgressDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_offers);
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








        firestore=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();
        dialog=new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);









        button_take_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (ContextCompat.checkSelfPermission(AdminAddOffersActivity.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED)
                {
                    ActivityCompat.requestPermissions(AdminAddOffersActivity.this, new String[] {Manifest.permission.CAMERA}, 1);
                    if (ContextCompat.checkSelfPermission(AdminAddOffersActivity.this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED)
                        startActivityForResult(takePicture, 100);
                }
                else{
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
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                // pass the constant to compare it
                // with the returned requestCode
                startActivityForResult(Intent.createChooser(intent, "Select Pictures"), 110);

            }
        });

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title= edit_text_title.getText().toString();
                String description=edit_text_description.getText().toString();
                String new_price =edit_text_new_price.getText().toString();
                String original_price =edit_text_original_price.getText().toString();
                String address=edit_text_address.getText().toString();
                String phone=edit_text_phone.getText().toString();



                if(title.isEmpty())
                {
                    edit_text_title.setError("Empty Field");
                    return;
                }
                if(description.isEmpty())
                {
                    edit_text_description.setError("Empty Field");
                    return;
                }
                if(address.isEmpty())
                {
                    edit_text_address.setError("Empty Field");
                    return;
                }

                if(new_price.isEmpty())
                {
                    edit_text_new_price.setError("Empty Field");
                    return;
                }
                if(original_price.isEmpty())
                {
                    edit_text_original_price.setError("Empty Field");
                    return;
                }

                if(phone.isEmpty())
                {
                    edit_text_phone.setError("Empty Field");
                    return;
                }



                if(imagesUri.size()==0)
                {
                    makeText(AdminAddOffersActivity.this,"You should select one image at least " , LENGTH_LONG).show();
                    return;
                }


                DecorOffer decorOffer=new DecorOffer();

                decorOffer.setTitle(title);
                decorOffer.setDescription(description);
                decorOffer.setNew_price(new_price);
                decorOffer.setOriginal_price(original_price);
                decorOffer.setAddress(address);
                decorOffer.setPhone(phone);
                dialog.setTitle("Uploading Images");
                dialog.setMessage(images.size()+"/"+imagesUri.size());
                dialog.show();

                //first upload images



                for (Uri image_uri:imagesUri
                ) {
                    //define unique image name usinf UUID generator
                    String imageName = UUID.randomUUID().toString()+".jpg";

                    // Defining the child of storageReference
                    StorageReference storageReference =storage.getReference();
                    StorageReference ref = storageReference.child("offers_images/"+imageName);
                    dialog.setTitle("Uploading Images");
                    ref.putFile(image_uri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    images.add(imageName);
                                    dialog.setMessage(images.size()+"/"+imagesUri.size());
                                    dialog.setTitle("Uploading Images");
                                    if(images.size()==imagesUri.size()){
                                        dialog.setTitle("Uploading Data");
                                        dialog.setMessage("Please Wait");
                                        decorOffer.setImages(images);
                                        decorOffer.setCreated_at(null);

                                        firestore.collection("decor_offers")
                                                .add(decorOffer)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        makeText(AdminAddOffersActivity.this,"Offer has been saved successfully" , LENGTH_LONG).show();
                                                        dialog.dismiss();
                                                        finish();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        makeText(AdminAddOffersActivity.this,"Failed to save offer" , LENGTH_LONG).show();
                                                        dialog.dismiss();
                                                    }
                                                });
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    makeText(AdminAddOffersActivity.this,"Error while uploading image" , LENGTH_LONG).show();

                                }
                            });

                }


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
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    int cout = data.getClipData().getItemCount();
                    for (int i = 0; i < cout; i++) {
                        // adding imageuri in array
                        Uri imageurl = data.getClipData().getItemAt(i).getUri();
                        imagesUri.add(imageurl);

                    }

                } else {
                    Uri imageurl = data.getData();
                    imagesUri.add(imageurl);

                }

                RoomDesignImagesListAdapter adapter=new RoomDesignImagesListAdapter(imagesUri,AdminAddOffersActivity.this);
                recycler_view_images.setAdapter(adapter);

            } else if(requestCode==100) {

                Bitmap image= (Bitmap) data.getExtras().get("data");
                //prepare image to upload
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                String path = MediaStore.Images.Media.insertImage(getContentResolver(), image, "Title", null);


                imagesUri.add(Uri.parse(path));
                RoomDesignImagesListAdapter adapter=new RoomDesignImagesListAdapter(imagesUri,AdminAddOffersActivity.this);
                recycler_view_images.setAdapter(adapter);

            }

        }


    }
}