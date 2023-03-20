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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ksacp2022.electronicinteriordesignapplication.R;
import com.ksacp2022.electronicinteriordesignapplication.models.GlideApp;

import java.util.List;

public class RoomDesignImagesListAdapter extends RecyclerView.Adapter<RoomImageItem> {
    List<Uri> images;

    Context context;

    public RoomDesignImagesListAdapter(List<Uri> images,Context context) {
        this.images = images;
        this.context = context;

    }

    @NonNull
    @Override
    public RoomImageItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.room_image_list_item,parent,false);
        return new RoomImageItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomImageItem holder, int position) {
        int pos=position;
        Uri image= images.get(pos);


        GlideApp.with(context)
                .load(image)
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

class RoomImageItem extends RecyclerView.ViewHolder{
    ImageButton delete;
    ImageView image;

    public RoomImageItem(@NonNull View itemView) {
        super(itemView);
        delete=itemView.findViewById(R.id.delete);
        image=itemView.findViewById(R.id.image);
    }
}
