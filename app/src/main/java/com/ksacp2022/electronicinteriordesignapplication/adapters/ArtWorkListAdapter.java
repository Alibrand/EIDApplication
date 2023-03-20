package com.ksacp2022.electronicinteriordesignapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ksacp2022.electronicinteriordesignapplication.ImageViewerActivity;
import com.ksacp2022.electronicinteriordesignapplication.R;
import com.ksacp2022.electronicinteriordesignapplication.models.ArtWork;
import com.ksacp2022.electronicinteriordesignapplication.models.GlideApp;

import java.util.List;

public class ArtWorkListAdapter extends RecyclerView.Adapter<WorkArtItem> {
    List<ArtWork> artWorkList;
    Context context;
    FirebaseStorage storage;
    FirebaseFirestore firestore;

    public ArtWorkListAdapter(List<ArtWork> artWorkList, Context context) {
        this.artWorkList = artWorkList;
        this.context = context;
        this.storage=FirebaseStorage.getInstance();
        this.firestore=FirebaseFirestore.getInstance();
    }



    @NonNull
    @Override
    public WorkArtItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.art_work_list_item,parent,false);

        return new WorkArtItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkArtItem holder, int position) {
        final int item_position=position;
        ArtWork artWork= artWorkList.get(item_position);


        StorageReference reference=storage.getReference();
        StorageReference image=reference.child("art_works/"+artWork.getImage_url());
        GlideApp.with(context)
                .load(image)
                .into(holder.image_view_avatar);

        holder.card_view_art_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ImageViewerActivity. class);
                intent.putExtra("image_url","art_works/"+artWork.getImage_url());
                intent.putExtra("description",artWork.getDescription());
                context.startActivity(intent);

            }
        });




    }

    @Override
    public int getItemCount() {
        return artWorkList.size();
    }
}


class WorkArtItem extends RecyclerView.ViewHolder{

    ImageView image_view_avatar;
    RelativeLayout card_view_art_work;

    public WorkArtItem(@NonNull View itemView) {
        super(itemView);
        image_view_avatar=itemView.findViewById(R.id.image_view_avatar);
        card_view_art_work=itemView.findViewById(R.id.card_view_art_work);


    }
}
