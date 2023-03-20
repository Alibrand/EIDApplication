package com.ksacp2022.electronicinteriordesignapplication.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ksacp2022.electronicinteriordesignapplication.R;
import com.ksacp2022.electronicinteriordesignapplication.models.GlideApp;

import java.util.List;

public class AdminOfferImagesListAdapter extends RecyclerView.Adapter<AdminOfferImageItem> {
    List<String> images;
    FirebaseStorage storage;
    Context context;

    public AdminOfferImagesListAdapter(List<String> images,Context context) {
        this.images = images;
        this.context = context;
        storage=FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public AdminOfferImageItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.room_image_list_item,parent,false);
        return new AdminOfferImageItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminOfferImageItem holder, int position) {
        int pos=position;
        String image= images.get(pos);

        StorageReference ref= storage.getReference();
        StorageReference image_uri=ref.child("offers_images/"+image);

        GlideApp.with(context)
                .load(image_uri)
                .transform(new CenterCrop(),new RoundedCorners(25))
                .into(holder.image);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                images.remove(image);

                                notifyItemRemoved(pos);
                                notifyItemRangeChanged(pos, images.size());

            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }
}

class AdminOfferImageItem extends RecyclerView.ViewHolder{
    ImageButton delete;
    ImageView image;

    public AdminOfferImageItem(@NonNull View itemView) {
        super(itemView);
        delete=itemView.findViewById(R.id.delete);
        image=itemView.findViewById(R.id.image);
    }
}
