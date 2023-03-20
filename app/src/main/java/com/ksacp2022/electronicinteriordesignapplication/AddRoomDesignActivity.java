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
import android.widget.TextView;
import android.widget.Toast;

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
import com.ksacp2022.electronicinteriordesignapplication.models.RoomDesign;
import com.skydoves.colorpickerview.ActionMode;
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

public class AddRoomDesignActivity extends AppCompatActivity {


    EditText edit_text_design_name,edit_text_description;
    AppCompatButton button_add_color,button_save_design;
    ImageButton button_take_pic,button_gallery;

    RecyclerView recycler_view_colors,recycler_view_images;

    List<Integer> design_colors=new ArrayList<>();
    List<String> images=new ArrayList<>();
    List<Uri> imagesUri=new ArrayList<Uri>();

    FirebaseAuth firebaseAuth;
    FirebaseStorage storage;
    FirebaseFirestore firestore;
    ProgressDialog dialog;

    String user_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room_design);

        edit_text_design_name = findViewById(R.id.edit_text_design_name);
        edit_text_description = findViewById(R.id.edit_text_description);
        button_add_color = findViewById(R.id.button_add_color);
        recycler_view_colors = findViewById(R.id.recycler_view_colors);
        button_take_pic = findViewById(R.id.button_take_pic);
        button_gallery = findViewById(R.id.button_gallery);
        recycler_view_images = findViewById(R.id.recycler_view_images);
        button_save_design = findViewById(R.id.button_save_design);



        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();
        dialog=new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("Loading user info");
        dialog.setMessage("Please Wait");
        dialog.show();


        firestore.collection("profiles")
                        .document(firebaseAuth.getUid())
                                .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                Map<String,Object> data=documentSnapshot.getData();
                                                user_name=data.get("full_name").toString();
                                                dialog.dismiss();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeText(AddRoomDesignActivity.this,"Failed to load user info" , LENGTH_LONG).show();
                        dialog.dismiss();
                        finish();
                    }
                });





        button_add_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ColorPickerDialog.Builder dialog=  new ColorPickerDialog.Builder(AddRoomDesignActivity.this,R.style.AlertDialogTheme)
                        .setTitle("ColorPicker Dialog")
                        .setPositiveButton("Select",
                                new ColorEnvelopeListener() {
                                    @Override
                                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                        design_colors.add(envelope.getColor());

                                        ColorsListAdapter adapter=new ColorsListAdapter(design_colors);
                                        recycler_view_colors.setAdapter(adapter);


                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })

                         .attachAlphaSlideBar(true) // the default value is true.
                        .attachBrightnessSlideBar(true)  // the default value is true.
                           ;

                ColorPickerView colorPickerView = dialog.getColorPickerView();
                BubbleFlag bubbleFlag = new BubbleFlag(AddRoomDesignActivity.this);
                bubbleFlag.setFlagMode(FlagMode.ALWAYS);
                colorPickerView.setFlagView(bubbleFlag);// sets a custom flagView
                dialog.show();
            }
        });


        button_take_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (ContextCompat.checkSelfPermission(AddRoomDesignActivity.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED)
                {
                    ActivityCompat.requestPermissions(AddRoomDesignActivity.this, new String[] {Manifest.permission.CAMERA}, 1);
                    if (ContextCompat.checkSelfPermission(AddRoomDesignActivity.this, Manifest.permission.CAMERA)
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

        button_save_design.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=edit_text_design_name.getText().toString();
                String description=edit_text_description.getText().toString();

                if(name.isEmpty())
                {
                    edit_text_design_name.setError("Empty Field");
                    return;
                }
                if(description.isEmpty())
                {
                    edit_text_description.setError("Empty Field");
                    return;
                }
                if(design_colors.size()==0)
                {
                    makeText(AddRoomDesignActivity.this,"You should select one color at least for the design" , LENGTH_LONG).show();
                    return;
                }
                if(imagesUri.size()==0)
                {
                    makeText(AddRoomDesignActivity.this,"You should select one image at least for the room" , LENGTH_LONG).show();
                    return;
                }


                RoomDesign roomDesign=new RoomDesign();

                roomDesign.setName(name);
                roomDesign.setDescription(description);
                roomDesign.setColors(design_colors);
                roomDesign.setCreator_id(firebaseAuth.getUid());
                roomDesign.setCreator_name(user_name);
                List<String> share_list=new ArrayList<>();
                share_list.add(firebaseAuth.getUid());
                roomDesign.setShare_list(share_list);
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
                    StorageReference ref = storageReference.child("room_images/"+imageName);

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
                                        roomDesign.setImages(images);
                                        roomDesign.setCreated_at(null);

                                        firestore.collection("room_designs")
                                                .add(roomDesign)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        makeText(AddRoomDesignActivity.this,"You Design has been saved successfully" , LENGTH_LONG).show();
                                                    dialog.dismiss();
                                                    finish();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        makeText(AddRoomDesignActivity.this,"Failed to save design" , LENGTH_LONG).show();
                                                        dialog.dismiss();
                                                    }
                                                });
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    makeText(AddRoomDesignActivity.this,"Error while uploading image" , LENGTH_LONG).show();

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

                RoomDesignImagesListAdapter adapter=new RoomDesignImagesListAdapter(imagesUri,AddRoomDesignActivity.this);
                recycler_view_images.setAdapter(adapter);

    } else if(requestCode==100) {

                Bitmap image= (Bitmap) data.getExtras().get("data");
                //prepare image to upload
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                String path = MediaStore.Images.Media.insertImage(getContentResolver(), image, "Title", null);


                imagesUri.add(Uri.parse(path));
                RoomDesignImagesListAdapter adapter=new RoomDesignImagesListAdapter(imagesUri,AddRoomDesignActivity.this);
                recycler_view_images.setAdapter(adapter);

    }

    }


}
}