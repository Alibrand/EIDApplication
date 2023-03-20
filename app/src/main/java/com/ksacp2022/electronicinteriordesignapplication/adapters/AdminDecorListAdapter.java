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
import com.ksacp2022.electronicinteriordesignapplication.models.DecorProduct;
import com.ksacp2022.electronicinteriordesignapplication.models.GlideApp;

import java.util.List;

public class AdminDecorListAdapter extends RecyclerView.Adapter<AdminDecorItem> {
    List<DecorProduct> decors;
    String store_id;
    Context context;
    FirebaseStorage storage;
    FirebaseFirestore firestore;
    public AdminDecorListAdapter(List<DecorProduct> decors, Context context,String store_id) {
        this.decors = decors;
        this.context = context;
        this.store_id=store_id;
        storage=FirebaseStorage.getInstance();
        firestore=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public AdminDecorItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_decor_list_item,parent,false);
        return new AdminDecorItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminDecorItem holder, int position) {
        int pos=position;
        DecorProduct decorProduct=decors.get(pos);

        holder.text_name.setText(decorProduct.getName());

        StorageReference reference=storage.getReference();
        StorageReference image=reference.child("stores/decors/"+decorProduct.getImage_url());
        GlideApp.with(context)
                .load(image)
                .transform(new CenterCrop(),new RoundedCorners(25))
                .into(holder.image);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("decor_stores")
                        .document(store_id)
                        .collection("products")
                        .document(decorProduct.getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                notifyItemRemoved(pos);
                                notifyItemRangeChanged(pos,decors.size());
                                image.delete();
                                if(decorProduct.getFile_url()!=null)
                                {
                                    StorageReference file=reference.child("3d_models/"+decorProduct.getFile_url());
                                    file.delete();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return decors.size();
    }
}

class AdminDecorItem extends RecyclerView.ViewHolder{
    ImageButton delete;
    ImageView image;
    TextView text_name;

    public AdminDecorItem(@NonNull View itemView) {
        super(itemView);
        delete=itemView.findViewById(R.id.delete);
        image=itemView.findViewById(R.id.image);
        text_name=itemView.findViewById(R.id.text_name);
    }
}
