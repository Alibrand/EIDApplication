package com.ksacp2022.electronicinteriordesignapplication;

import static android.widget.Toast.*;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.ar.core.Anchor;
//import com.google.ar.sceneform.AnchorNode;
import com.google.ar.core.HitResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
//import com.google.ar.sceneform.ux.ArFragment;
//import com.google.ar.sceneform.ux.TransformableNode;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dev.romainguy.kotlin.math.Float3;
import io.github.sceneview.ar.ArSceneView;
import io.github.sceneview.ar.node.ArModelNode;
import io.github.sceneview.ar.node.PlacementMode;
import io.github.sceneview.node.Node;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class ARActivity extends AppCompatActivity {
   // private ArFragment arFragment;
    private ArSceneView arSceneView;


    private ImageButton exit_button,capture;
    ArModelNode modelNode;
    FirebaseStorage firebaseStorage;

    ProgressDialog progressDialog;
    File model_local_path;
    String model_name="sofa_2.glb";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_aractivity);
        //arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        exit_button = findViewById(R.id.exit);
        capture = findViewById(R.id.capture);

        arSceneView = findViewById(R.id.arFragment);
        firebaseStorage=FirebaseStorage.getInstance();

        model_name=getIntent().getStringExtra("model_name");

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            capturePicture();

            }
        });


        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);



        model_local_path = new File(Environment.getExternalStorageDirectory(), "/eida/models/"+model_name);

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 100);
        checkPermission(Manifest.permission.CAMERA, 101);

        if(!model_local_path.exists())
        {
           Log.d("TAG","Not existed");
            if(!model_local_path.getParentFile().exists()) {
            model_local_path.getParentFile().mkdirs();
                Log.d("TAG","making folder");
        }
            progressDialog.setTitle("Loading model");
            progressDialog.setMessage("");
            progressDialog.show();

            StorageReference ref=firebaseStorage.getReference();
            StorageReference file=ref.child("3d_models/"+model_name);

            file.getFile(model_local_path)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            makeText(ARActivity.this,"Download complete" , LENGTH_LONG).show();
                            new AlertDialog.Builder(ARActivity.this)
                                    .setTitle("Tips")
                                    .setMessage("-Move the phone until the app finds a ground plane \n " +
                                            "-Aim the Camera to the place where you want to put the model in \n " +
                                            "-Tap on the center of the dotted area \n " +
                                            "-When the model takes its right place you can rotate and change the size by using pinch gestures \n " +
                                            "-Use Camera button to take a shot \n" +
                                            "-Close using the button in the upper right corner"
                                    )

                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                    .setPositiveButton("OK", null)
                                    .setIcon(android.R.drawable.ic_dialog_info)
                                    .show();
                            initiate_arview();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Log.d("TAG", model_name);
                            e.printStackTrace();
                            makeText(ARActivity.this,"Failed to load file :"+e.getMessage(), LENGTH_LONG).show();
                            finish();
                        }
                    }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull FileDownloadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                            progressDialog.setMessage("Downloaded  "+(int)progress+"%..");
                        }
                    });
        }
        else{
            initiate_arview();
        }

















        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for(Node node:arSceneView.getAllChildren()) {
                 node.detachFromScene(arSceneView);

             }
                  finish();
            }
        });


    }

    @Override
    protected void onDestroy() {
        for(Node node:arSceneView.getAllChildren()) {
            node.detachFromScene(arSceneView);

        }

        super.onDestroy();

    }
    private boolean checkWriteExternalPermission()
    {
        String permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }



    private  void initiate_arview(){


        arSceneView.setOnTapAr(new Function2<HitResult, MotionEvent, Unit>() {
            @Override
            public Unit invoke(HitResult hitResult, MotionEvent motionEvent) {



                Anchor anchor= hitResult.createAnchor();


                modelNode=new ArModelNode(ARActivity.this,null,"file://" +model_local_path.getPath()
                        , false, 0.7f, new Float3(0.0f,0.0f,0.0f), null, null);


                modelNode.setPlacementMode(PlacementMode.INSTANT);
                //modelNode.setPositionEditable(false);
                modelNode.setAnchor(anchor);
                arSceneView.getPlaneRenderer().setVisible(false);
                arSceneView.addChild(modelNode);
                modelNode.setSelected(true);


                return null;
            }
        });
    }







    private void saveScreen(Bitmap bmp) {
        Date now = new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/eida/screen_shots/" + sdf.format(now) + ".jpg";

            // create bitmap screen capture
          //  View v1 = arSceneView.getRootView();
           // v1.setDrawingCacheEnabled(true);
            //Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            Bitmap bitmap=bmp;
           // v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            if(!imageFile.getParentFile().exists())
                imageFile.getParentFile().mkdirs();

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            makeText(ARActivity.this,"screen saved to "+imageFile.getPath() , LENGTH_LONG).show();
           // openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    private void capturePicture() {
        Bitmap bmp = Bitmap.createBitmap(arSceneView.getWidth(), arSceneView.getHeight(), Bitmap.Config.ARGB_8888);
        PixelCopy.request(arSceneView, bmp, i -> {
            saveScreen(bmp); //"iv_Result" is the image view
        }, new Handler(Looper.getMainLooper()));
    }

    public void checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(ARActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(ARActivity.this, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(ARActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {

            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Showing the toast message
                Toast.makeText(ARActivity.this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(ARActivity.this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == 101) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(ARActivity.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(ARActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }




}