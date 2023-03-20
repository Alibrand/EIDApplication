package com.ksacp2022.electronicinteriordesignapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ksacp2022.electronicinteriordesignapplication.DesignerProfileActivity;
import com.ksacp2022.electronicinteriordesignapplication.R;
import com.ksacp2022.electronicinteriordesignapplication.models.DesignerProfile;
import com.ksacp2022.electronicinteriordesignapplication.models.GlideApp;

import java.util.List;

public class DesignersListAdapter extends RecyclerView.Adapter<DesignerListItem> {
    List<DesignerProfile> profileList;
    Context context;
    FirebaseStorage storage;

    public DesignersListAdapter(List<DesignerProfile> profileList, Context context) {
        this.profileList = profileList;
        this.context = context;
        storage=FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public DesignerListItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView
                = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.designer_list_item,
                        parent,
                        false);
        return new DesignerListItem(itemView) ;
    }

    @Override
    public void onBindViewHolder(@NonNull DesignerListItem holder, int position) {
        DesignerProfile profile=profileList.get(position);
        holder.text_view_designer_name.setText(profile.getFull_name());

        StorageReference ref=storage.getReference();
        StorageReference avatar=ref.child("avatars/"+profile.getAvatar_url());
        GlideApp.with(context)
                .load(avatar)
                .into(holder.image_view_avatar);

        holder.text_likes_count.setText(String.valueOf(profile.getLikes().size()));

        holder.designer_list_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DesignerProfileActivity.class);
                intent.putExtra("designer_id",profile.getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }
}

class DesignerListItem extends RecyclerView.ViewHolder
{

    LinearLayoutCompat designer_list_item;
    ImageView image_view_avatar;
    TextView text_view_designer_name,text_likes_count;
    public DesignerListItem(@NonNull View itemView) {
        super(itemView);
        image_view_avatar = itemView.findViewById(R.id.image_view_avatar);
        text_view_designer_name = itemView.findViewById(R.id.text_view_designer_name);
        designer_list_item = itemView.findViewById(R.id.designer_list_item);
        text_likes_count = itemView.findViewById(R.id.text_likes_count);
    }
}