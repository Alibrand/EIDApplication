package com.ksacp2022.electronicinteriordesignapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ksacp2022.electronicinteriordesignapplication.AdminEditDecorStoreActivity;
import com.ksacp2022.electronicinteriordesignapplication.DecorStoreProfileActivity;
import com.ksacp2022.electronicinteriordesignapplication.R;
import com.ksacp2022.electronicinteriordesignapplication.models.DecorStore;
import com.ksacp2022.electronicinteriordesignapplication.models.GlideApp;

import java.util.List;

public class DecorStoresListAdapter extends RecyclerView.Adapter<DecorStoreItem> {
    List<DecorStore> decorStoreList;
    Context context;
    FirebaseStorage storage;
    FirebaseFirestore firestore;

    public DecorStoresListAdapter(List<DecorStore> decorStoreList, Context context) {
        this.decorStoreList = decorStoreList;
        this.context = context;
        storage=FirebaseStorage.getInstance();
        firestore=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public DecorStoreItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.decor_store_list_item,parent,false);
        return new DecorStoreItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DecorStoreItem holder, int position) {
        int pos=position;
        DecorStore decorStore=decorStoreList.get(pos);

        holder.text_view_store_name.setText(decorStore.getName());

        StorageReference ref=storage.getReference();
        StorageReference avatar=ref.child("stores/images/"+decorStore.getImage_url());
        GlideApp.with(context)
                .load(avatar)
                .into(holder.image_view_pic);

        holder.store_list_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DecorStoreProfileActivity.class);
                intent.putExtra("store_id",decorStore.getId());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return decorStoreList.size();
    }
}

class DecorStoreItem extends RecyclerView.ViewHolder{
    TextView text_view_store_name;
    ImageView image_view_pic;
    LinearLayoutCompat store_list_item;

    public DecorStoreItem(@NonNull View itemView) {
        super(itemView);
        text_view_store_name=itemView.findViewById(R.id.text_view_store_name);
        image_view_pic=itemView.findViewById(R.id.image_view_pic);
        store_list_item=itemView.findViewById(R.id.store_list_item);
    }
}
