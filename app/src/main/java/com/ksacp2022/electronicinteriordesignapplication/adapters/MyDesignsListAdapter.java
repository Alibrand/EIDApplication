package com.ksacp2022.electronicinteriordesignapplication.adapters;

import android.app.Activity;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ksacp2022.electronicinteriordesignapplication.R;
import com.ksacp2022.electronicinteriordesignapplication.ViewRoomDesignActivity;
import com.ksacp2022.electronicinteriordesignapplication.models.RoomDesign;

import java.util.List;

public class MyDesignsListAdapter extends RecyclerView.Adapter<MyDesignItem> {
    List<RoomDesign> designs;
    Context context;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    String designer_id;
    ProgressDialog progressDialog;
    public MyDesignsListAdapter(List<RoomDesign> designs, Context context,String designer_id) {
        this.designs = designs;
        this.context = context;
        firestore=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();
        this.designer_id=designer_id;
        progressDialog=new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public MyDesignItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_room_share_design_list_item,parent,false);
        return new MyDesignItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyDesignItem holder, int position) {
        int pos=position;
        RoomDesign roomDesign= designs.get(pos);

        holder.text_room_name.setText(roomDesign.getName());

        List<String> share_list=roomDesign.getShare_list();


        holder.design_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share_list.add(designer_id);
                progressDialog.setTitle("Sharing");
                progressDialog.setMessage("Please wait..");
                progressDialog.show();
                firestore.collection("room_designs")
                        .document(roomDesign.getId())
                        .update("share_list",share_list)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context,"Shared with success" , Toast.LENGTH_LONG).show();
                                ((Activity)context).finish();
                                progressDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(context,"Failed to share design" , Toast.LENGTH_LONG).show();
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

class MyDesignItem extends RecyclerView.ViewHolder{

    TextView text_room_name;
    LinearLayoutCompat design_item;


    public MyDesignItem(@NonNull View itemView) {
        super(itemView);
        text_room_name=itemView.findViewById(R.id.text_room_name);
        design_item=itemView.findViewById(R.id.design_item);
    }
}
