package com.ksacp2022.electronicinteriordesignapplication.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.ksacp2022.electronicinteriordesignapplication.R;
import com.ksacp2022.electronicinteriordesignapplication.ViewRoomDesignActivity;
import com.ksacp2022.electronicinteriordesignapplication.models.RoomDesign;

import java.util.List;

public class SharedDesignsListAdapter extends RecyclerView.Adapter<SharedDesignItem> {
    List<RoomDesign> designs;
    Context context;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    String share_with_id;
    public SharedDesignsListAdapter(List<RoomDesign> designs, Context context,String share_with_id) {
        this.designs = designs;
        this.context = context;
        firestore=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();
        this.share_with_id =share_with_id;
        progressDialog=new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth=FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public SharedDesignItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_room_design_list_item,parent,false);
        return new SharedDesignItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SharedDesignItem holder, int position) {
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
        List<String> share_list=roomDesign.getShare_list();

        if(!firebaseAuth.getUid().equals(roomDesign.getCreator_id()))
            holder.image_button_delete.setVisibility(View.GONE);

        holder.image_button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share_list.remove(share_with_id);
                progressDialog.setTitle("Undo Sharing");
                progressDialog.setMessage("Please wait..");
                progressDialog.show();
                firestore.collection("room_designs")
                        .document(roomDesign.getId())
                        .update("share_list",share_list)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                designs.remove(roomDesign);
                                SharedDesignsListAdapter.this.notifyDataSetChanged();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(context,"Failed to undo sharing " , Toast.LENGTH_LONG).show();
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

class SharedDesignItem extends RecyclerView.ViewHolder{
    ImageButton image_button_delete;
    TextView text_room_name;
    LinearLayoutCompat design_item;


    public SharedDesignItem(@NonNull View itemView) {
        super(itemView);
        image_button_delete=itemView.findViewById(R.id.image_button_delete);
        text_room_name=itemView.findViewById(R.id.text_room_name);
        design_item=itemView.findViewById(R.id.design_item);
    }
}
