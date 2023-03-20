package com.ksacp2022.electronicinteriordesignapplication;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ksacp2022.electronicinteriordesignapplication.adapters.ArtWorkListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.adapters.DesignerArtWorkListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.models.ArtWork;
import com.ksacp2022.electronicinteriordesignapplication.models.DesignerProfile;
import com.ksacp2022.electronicinteriordesignapplication.models.GlideApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DesignerProfileActivity extends AppCompatActivity {

    ImageView image_view_avatar;
    TextView text_view_bio,text_view_designer_name,text_view_see_all,
    text_reviews_count,text_likes_count;
    AppCompatButton button_call,button_location,button_chat;
    RecyclerView recycler_view_art_works;
    ImageButton button_reviews,button_like;


    List<ArtWork> artWorkList;

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    FirebaseStorage storage;

    List<String> likes=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designer_profile);
        image_view_avatar=findViewById(R.id.image_view_avatar);
        text_view_bio=findViewById(R.id.text_view_bio);
        button_call=findViewById(R.id.button_call);
        button_location=findViewById(R.id.button_location);
        button_chat=findViewById(R.id.button_chat);
        text_view_see_all = findViewById(R.id.text_view_see_all);
        text_view_designer_name = findViewById(R.id.text_view_designer_name);
        recycler_view_art_works = findViewById(R.id.recycler_view_art_works);
        text_reviews_count = findViewById(R.id.text_reviews_count);
        text_likes_count = findViewById(R.id.text_likes_count);
        button_reviews = findViewById(R.id.button_reviews);
        button_like = findViewById(R.id.button_like);






        Intent intent=getIntent();
        String designer_id=intent.getStringExtra("designer_id");



        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        StorageReference ref=storage.getReference();


        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Getting Profile Info");
        progressDialog.setMessage("Please Wait");
        progressDialog.show();

        firestore.collection("designer_profile")
                .document(designer_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressDialog.dismiss();
                        Map<String,Object> data=documentSnapshot.getData();
                        String full_name=data.get("full_name").toString();
                        String bio=data.get("bio").toString();
                        String phone=data.get("phone").toString();
                        GeoPoint location= (GeoPoint) data.get("location");
                        String avatar_image=data.get("avatar_url").toString();

                        if(data.get("likes")!=null)
                            likes= (List<String>) data.get("likes");

                        text_likes_count.setText(String.valueOf(likes.size()));

                        //check if this user liked this profile before
                        String uid=firebaseAuth.getUid();
                        if(likes.indexOf(uid)>-1)
                            button_like.setImageResource(R.drawable.ic_baseline_thumb_up_filled_24);





                        StorageReference avatar=ref.child("avatars/"+ avatar_image);
                        GlideApp.with(DesignerProfileActivity.this)
                                .load(avatar)
                                .circleCrop()
                                .into(image_view_avatar);

                        text_view_designer_name.setText(full_name);
                        text_view_bio.setText(bio);

                        button_call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:"+phone));
                                startActivity(intent);
                            }
                        });

                        button_location.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String uri = String.format(Locale.ENGLISH, "geo:0,0?q=%f,%f("+full_name+")",location.getLatitude(),location.getLongitude());
                               // String uri = String.format(Locale.ENGLISH, "geo:%f,%f", location.getLatitude(), location.getLongitude());
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                startActivity(intent);
                            }
                        });

                        button_chat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(DesignerProfileActivity.this, ChatActivity. class);
                                intent.putExtra("receiver_name",full_name);
                                intent.putExtra("receiver_id",designer_id);
                                startActivity(intent);
                            }
                        });





                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        makeText(DesignerProfileActivity.this,"Failed to get profile info" , LENGTH_LONG).show();
                        finish();

                    }
                });




        firestore.collection("designer_profile")
                .document(designer_id)
                .collection("art_works")
                .limit(5)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        artWorkList=new ArrayList<ArtWork>();
                        for (DocumentSnapshot doc:queryDocumentSnapshots.getDocuments()
                        ) {
                            Map<String,Object> data=doc.getData();
                            ArtWork artWork=new ArtWork();
                            artWork.setDesigner_id(designer_id);
                            artWork.setId(doc.getId());
                            artWork.setDescription(data.get("description").toString());
                            artWork.setImage_url(data.get("image_url").toString());

                            artWorkList.add(artWork);


                        }

                        ArtWorkListAdapter adapter=new ArtWorkListAdapter(artWorkList,DesignerProfileActivity.this);
                        recycler_view_art_works.setAdapter(adapter);
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeText(DesignerProfileActivity.this,"Failed to load works" , LENGTH_LONG).show();
                        finish();
                    }
                });

        //get reviews count
        firestore.collection("designer_profile")
                .document(designer_id)
                .collection("reviews")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int reviews_count=queryDocumentSnapshots.getDocuments().size();
                        text_reviews_count.setText(String.valueOf(reviews_count));

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeText(DesignerProfileActivity.this,"Failed to get reviews count" , LENGTH_LONG).show();
                    }
                });






        text_view_see_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DesignerProfileActivity.this,ArtWorksActivity. class);
                intent.putExtra("designer_id",designer_id);
                startActivity(intent);
            }
        });


        button_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid=firebaseAuth.getUid();
                //if the user is already has liked this profile
                if(likes.indexOf(uid)>-1)
                {
                    //remove the user from likes list
                    likes.remove(uid);
                    button_like.setImageResource(R.drawable.ic_outline_thumb_up_24);
                }
                //else if the uer yet liked this profile before
                else
                {
                    //add him to likes
                    likes.add(uid);
                    button_like.setImageResource(R.drawable.ic_baseline_thumb_up_filled_24);
                }
                text_likes_count.setText(String.valueOf(likes.size()));
                //update profile likes
                firestore.collection("designer_profile")
                        .document(designer_id)
                        .update("likes",likes)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                makeText(DesignerProfileActivity.this,"Thanks for you thump up" , LENGTH_LONG).show();
                            }
                        });
            }
        });





        button_reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DesignerProfileActivity.this,DesignerReviewsActivity. class);
                intent.putExtra("designer_id",designer_id);
                startActivity(intent);
            }
        });










    }
}