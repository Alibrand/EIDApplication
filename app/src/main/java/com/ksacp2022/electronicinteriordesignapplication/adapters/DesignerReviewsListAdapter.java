package com.ksacp2022.electronicinteriordesignapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ksacp2022.electronicinteriordesignapplication.R;
import com.ksacp2022.electronicinteriordesignapplication.models.GlideApp;
import com.ksacp2022.electronicinteriordesignapplication.models.Review;

import java.util.List;

public class DesignerReviewsListAdapter extends RecyclerView.Adapter<ReviewListItem> {
    List<Review> reviewList;
    Context context;
    FirebaseStorage storage;
    FirebaseFirestore firestore;
    public DesignerReviewsListAdapter(List<Review>reviewList, Context context) {
        this.reviewList = reviewList;
        this.context = context;
        storage=FirebaseStorage.getInstance();
        firestore=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ReviewListItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_list_item,parent,false);
        return new ReviewListItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewListItem holder, int position) {
        Review review=reviewList.get(position);
        holder.text_name.setText(review.getName());
        holder.text_review.setText(review.getReview());
        if(review.isLike())
        holder.review_status.setImageResource(R.drawable.ic_baseline_thumb_up_filled_24);
        else
            holder.review_status.setImageResource(R.drawable.ic_baseline_thumb_down_24);

    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }
}

class ReviewListItem extends RecyclerView.ViewHolder{
    TextView text_name,text_review;
    ImageView review_status;

    public ReviewListItem(@NonNull View itemView) {
        super(itemView);
        text_name=itemView.findViewById(R.id.text_name);
        text_review=itemView.findViewById(R.id.text_review);
        review_status=itemView.findViewById(R.id.review_status);
    }
}
