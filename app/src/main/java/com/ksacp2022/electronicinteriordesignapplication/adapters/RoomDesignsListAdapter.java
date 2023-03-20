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
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ksacp2022.electronicinteriordesignapplication.R;
import com.ksacp2022.electronicinteriordesignapplication.ViewRoomDesignActivity;
import com.ksacp2022.electronicinteriordesignapplication.models.GlideApp;
import com.ksacp2022.electronicinteriordesignapplication.models.RoomDesign;

import java.util.List;

public class RoomDesignsListAdapter extends RecyclerView.Adapter<RoomDesignItem> {
    List<RoomDesign> designs;
    Context context;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    public RoomDesignsListAdapter(List<RoomDesign> designs, Context context) {
        this.designs = designs;
        this.context = context;
        firestore=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public RoomDesignItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_room_design_list_item,parent,false);
        return new RoomDesignItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomDesignItem holder, int position) {
        int pos=position;
        RoomDesign roomDesign= designs.get(pos);

        holder.text_room_name.setText(roomDesign.getName());

        holder.design_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewRoomDesignActivity.class);
                intent.putExtra("design_id",roomDesign.getId());
                context.startActivity(intent);
            }
        });

        holder.image_button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                designs.remove(roomDesign);
                firestore.collection("room_designs")
                        .document(roomDesign.getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                //also delete images files
                                StorageReference ref=storage.getReference();
                                for (String image: roomDesign.getImages()
                                     ) {
                                    ref.child("room_images/"+image)
                                            .delete();
                                }


                                notifyItemRemoved(pos);
                                notifyItemRangeChanged(pos,designs.size());
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return designs.size();
    }
}

class RoomDesignItem extends RecyclerView.ViewHolder{
    ImageButton image_button_delete;
    TextView text_room_name;
    LinearLayoutCompat design_item;


    public RoomDesignItem(@NonNull View itemView) {
        super(itemView);
        image_button_delete=itemView.findViewById(R.id.image_button_delete);
        text_room_name=itemView.findViewById(R.id.text_room_name);
        design_item=itemView.findViewById(R.id.design_item);
    }
}
