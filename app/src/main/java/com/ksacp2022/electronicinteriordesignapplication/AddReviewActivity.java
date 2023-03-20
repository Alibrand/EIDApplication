package com.ksacp2022.electronicinteriordesignapplication;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022.electronicinteriordesignapplication.models.Review;

import java.util.Map;

public class AddReviewActivity extends AppCompatActivity {

    TextView text_name;
    AppCompatButton button_send_review;
    EditText edit_text_review;
    ImageView button_like,button_dislike;

    boolean liked=true;


    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    String designer_id;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        text_name = findViewById(R.id.text_name);
        button_send_review = findViewById(R.id.button_send_review);
        edit_text_review = findViewById(R.id.edit_text_review);
        button_like = findViewById(R.id.button_like);
        button_dislike = findViewById(R.id.button_dislike);

        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();

        designer_id=getIntent().getStringExtra("designer_id");

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        progressDialog.setTitle("Loading User info");
        progressDialog.setMessage("Please Wait");
        progressDialog.show();

        firestore.collection("profiles")
                .document(firebaseAuth.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressDialog.dismiss();
                        Map<String,Object> data=documentSnapshot.getData();
                        text_name.setText(data.get("full_name").toString());

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeText(AddReviewActivity.this,"Failed to load user info" , LENGTH_LONG).show();
                        finish();
                    }
                });

        button_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                liked=true;
                button_like.setImageResource(R.drawable.ic_baseline_thumb_up_filled_24);
                button_dislike.setImageResource(R.drawable.ic_outline_thumb_down_24);
            }
        });

        button_dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                liked=false;
                button_like.setImageResource(R.drawable.ic_outline_thumb_up_24);
                button_dislike.setImageResource(R.drawable.ic_baseline_thumb_down_24);
            }
        });


        button_send_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String  review_text=edit_text_review.getText().toString();

                if(review_text.isEmpty())
                {
                    edit_text_review.setError("Please leave a review");
                    return;
                }

                Review review=new Review();
                review.setName(text_name.getText().toString());
                review.setReview(review_text);
                review.setLike(liked);
                review.setCreated_at(null);


                progressDialog.setTitle("Sending Review");
                progressDialog.show();

                firestore.collection("designer_profile")
                        .document(designer_id)
                        .collection("reviews")
                        .add(review)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                progressDialog.dismiss();
                                makeText(AddReviewActivity.this,"Thanks for sending a review" , LENGTH_LONG).show();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                makeText(AddReviewActivity.this,"Failed to send your review" , LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });


            }
        });





    }
}