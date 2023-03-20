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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022.electronicinteriordesignapplication.adapters.DesignerReviewsListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.models.Review;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DesignerReviewsActivity extends AppCompatActivity {
    RecyclerView recycler_view_reviews;
    AppCompatButton button_add_review;

    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    List<Review> reviewList;
    String designer_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designer_reviews);
        recycler_view_reviews = findViewById(R.id.recycler_view_reviews);
        button_add_review = findViewById(R.id.button_add_review);

        firestore=FirebaseFirestore.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        designer_id=getIntent().getStringExtra("designer_id");


        get_reviews();

        button_add_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DesignerReviewsActivity.this,AddReviewActivity. class);
                intent.putExtra("designer_id",designer_id);
                startActivity(intent);
            }
        });
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

                        DesignerReviewsListAdapter adapter=new DesignerReviewsListAdapter(reviewList,DesignerReviewsActivity.this);
                        recycler_view_reviews.setAdapter(adapter);
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DesignerReviewsActivity.this,"Failed to load reviews" , Toast.LENGTH_LONG).show();
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