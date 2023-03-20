package com.ksacp2022.electronicinteriordesignapplication.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.ksacp2022.electronicinteriordesignapplication.ARActivity;
import com.ksacp2022.electronicinteriordesignapplication.ImageViewerActivity;
import com.ksacp2022.electronicinteriordesignapplication.R;
import com.ksacp2022.electronicinteriordesignapplication.models.DecorProduct;
import com.ksacp2022.electronicinteriordesignapplication.models.GlideApp;

import java.util.List;

public class DecorsListAdapter extends RecyclerView.Adapter<DecorItem> {
    List<DecorProduct> decors;
    String store_id;
    Context context;
    FirebaseStorage storage;
    FirebaseFirestore firestore;
    public DecorsListAdapter(List<DecorProduct> decors, Context context,String store_id) {
        this.decors = decors;
        this.context = context;
        this.store_id=store_id;
        storage=FirebaseStorage.getInstance();
        firestore=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public DecorItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.decor_list_item,parent,false);
        return new DecorItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DecorItem holder, int position) {
        int pos=position;
        DecorProduct decor_pro=decors.get(pos);

        holder.text_price.setText(decor_pro.getPrice());
        holder.text_name.setText(decor_pro.getName());

        StorageReference reference=storage.getReference();
        StorageReference image=reference.child("stores/decors/"+decor_pro.getImage_url());
        GlideApp.with(context)
                .load(image)
                .transform(new CenterCrop(),new RoundedCorners(25))
                .into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ImageViewerActivity. class);
                intent.putExtra("image_url","stores/decors/"+decor_pro.getImage_url());
                intent.putExtra("description","Price :"+decor_pro.getPrice());
                context.startActivity(intent);

            }
        });

        if(decor_pro.getFile_url().isEmpty())
            holder.button_ar.setVisibility(View.GONE);

        holder.button_ar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ARActivity. class);
                intent.putExtra("model_name",decor_pro.getFile_url());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return decors.size();
    }
}

class DecorItem extends RecyclerView.ViewHolder{

    ImageView image;
    TextView text_price,text_name;
    ImageButton button_ar;

    public DecorItem(@NonNull View itemView) {
        super(itemView);
        image=itemView.findViewById(R.id.image);
        text_price=itemView.findViewById(R.id.text_price);
        button_ar=itemView.findViewById(R.id.button_ar);
        text_name=itemView.findViewById(R.id.text_name);
    }
}
