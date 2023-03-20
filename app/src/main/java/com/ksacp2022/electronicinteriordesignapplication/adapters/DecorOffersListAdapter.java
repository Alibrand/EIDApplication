package com.ksacp2022.electronicinteriordesignapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
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
import com.ksacp2022.electronicinteriordesignapplication.DecorOfferActivity;
import com.ksacp2022.electronicinteriordesignapplication.R;
import com.ksacp2022.electronicinteriordesignapplication.models.DecorOffer;
import com.ksacp2022.electronicinteriordesignapplication.models.GlideApp;

import java.util.List;

public class DecorOffersListAdapter extends RecyclerView.Adapter<DecorOfferItem> {
    List<DecorOffer> offers;
    Context context;
    FirebaseStorage storage;
    FirebaseFirestore firestore;
    public DecorOffersListAdapter(List<DecorOffer> offers, Context context) {
        this.offers = offers;
        this.context = context;
        storage=FirebaseStorage.getInstance();
        firestore=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public DecorOfferItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.offer_list_item,parent,false);
        return new DecorOfferItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DecorOfferItem holder, int position) {
        int pos=position;
        DecorOffer offer=offers.get(pos);
        //get first image as main image
        String image_url=offer.getImages().get(0);


        holder.text_new_price.setText(offer.getNew_price());
        holder.text_original_price.setText(offer.getOriginal_price());
        holder.text_original_price.setPaintFlags(holder.text_original_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.text_title.setText(offer.getTitle());

        StorageReference reference=storage.getReference();
        StorageReference image=reference.child("offers_images/"+image_url);
        GlideApp.with(context)
                .load(image)
                .transform(new CenterCrop(),new RoundedCorners(25))
                .into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DecorOfferActivity. class);
                intent.putExtra("offer_id",offer.getId());
                context.startActivity(intent);
            }
        });




    }

    @Override
    public int getItemCount() {
        return offers.size();
    }
}

class DecorOfferItem extends RecyclerView.ViewHolder{

    ImageView image;
    TextView text_new_price,text_original_price,text_title;

    public DecorOfferItem(@NonNull View itemView) {
        super(itemView);
        image=itemView.findViewById(R.id.image);
        text_new_price=itemView.findViewById(R.id.text_new_price);
        text_original_price=itemView.findViewById(R.id.text_original_price);
        text_title=itemView.findViewById(R.id.text_title);
    }
}
