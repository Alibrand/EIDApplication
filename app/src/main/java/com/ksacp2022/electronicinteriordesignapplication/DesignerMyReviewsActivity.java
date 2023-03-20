package com.ksacp2022.electronicinteriordesignapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022.electronicinteriordesignapplication.adapters.DesignerReviewsListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.models.Review;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DesignerMyReviewsActivity extends AppCompatActivity {

    RecyclerView recycler_view_reviews;


    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    List<Review> reviewList;
    String designer_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designer_my_reviews);
        recycler_view_reviews = findViewById(R.id.recycler_view_reviews);


        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        designer_id=firebaseAuth.getUid();


        get_reviews();


    }
    private void get_reviews() {
        progressDialog.setTitle("Loading Reviews");
        progressDialog.setMessage("Please Wait");
        progressDialog.show();

        firestore.collection("designer_profile")
                .document(designer_id)
                .collection("reviews")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        reviewList =new ArrayList<Review>();
                        for (DocumentSnapshot doc:queryDocumentSnapshots.getDocuments()
                        ) {
                            Map<String,Object> data=doc.getData();
                            Review review=new Review();
                            review.setReview(data.get("review").toString());
                            review.setName(data.get("name").toString());
                            review.setLike((boolean) data.get("like"));
                            reviewList.add(review);


                        }

                        DesignerReviewsListAdapter adapter=new DesignerReviewsListAdapter(reviewList,DesignerMyReviewsActivity.this);
                        recycler_view_reviews.setAdapter(adapter);
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DesignerMyReviewsActivity.this,"Failed to load reviews" , Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        get_reviews();
    }
}