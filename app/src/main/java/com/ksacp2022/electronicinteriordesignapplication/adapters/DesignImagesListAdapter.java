package com.ksacp2022.electronicinteriordesignapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ksacp2022.electronicinteriordesignapplication.ImageViewerActivity;
import com.ksacp2022.electronicinteriordesignapplication.R;
import com.ksacp2022.electronicinteriordesignapplication.models.GlideApp;

import java.util.List;

public class DesignImagesListAdapter extends RecyclerView.Adapter<DesignImageItem> {
    List<String> imagesList;
    Context context;
    FirebaseStorage storage;
    public DesignImagesListAdapter(List<String> imagesList, Context context) {
        this.imagesList = imagesList;
        this.context = context;
        storage=FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public DesignImageItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.design_image_item_list,parent,false);
        return new DesignImageItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DesignImageItem holder, int position) {
        int pos=position;
        String image_url=imagesList.get(pos);

        StorageReference reference=storage.getReference();
        StorageReference image=reference.child("room_images/"+image_url);
        GlideApp.with(context)
                .load(image)
                .transform(new CenterCrop(),new RoundedCorners(25))
                .into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ImageViewerActivity. class);
                intent.putExtra("image_url","room_images/"+image_url);
                intent.putExtra("description","");
                context.startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }
}

class DesignImageItem extends RecyclerView.ViewHolder{

    ImageView image;

    public DesignImageItem(@NonNull View itemView) {
        super(itemView);
        image=itemView.findViewById(R.id.image);
    }
}
