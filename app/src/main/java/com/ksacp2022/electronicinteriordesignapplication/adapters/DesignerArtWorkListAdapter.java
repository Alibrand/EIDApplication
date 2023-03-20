package com.ksacp2022.electronicinteriordesignapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ksacp2022.electronicinteriordesignapplication.R;
import com.ksacp2022.electronicinteriordesignapplication.models.ArtWork;
import com.ksacp2022.electronicinteriordesignapplication.models.GlideApp;

import java.util.List;

public class DesignerArtWorkListAdapter extends RecyclerView.Adapter<DesignerWorkArtItem> {
    List<ArtWork> artWorkList;
    Context context;
    FirebaseStorage storage;
    FirebaseFirestore firestore;

    public DesignerArtWorkListAdapter(List<ArtWork> artWorkList, Context context) {
        this.artWorkList = artWorkList;
        this.context = context;
        this.storage=FirebaseStorage.getInstance();
        this.firestore=FirebaseFirestore.getInstance();
    }



    @NonNull
    @Override
    public DesignerWorkArtItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.designer_artw_work_list_item,parent,false);

        return new DesignerWorkArtItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DesignerWorkArtItem holder, int position) {
        final int item_position=position;
        ArtWork artWork= artWorkList.get(item_position);


        holder.text_view_description.setText(artWork.getDescription());

        StorageReference reference=storage.getReference();
        StorageReference image=reference.child("art_works/"+artWork.getImage_url());
        GlideApp.with(context)
                .load(image)
                .into(holder.image_view_avatar);

        holder.image_button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("designer_profile")
                        .document(artWork.getDesigner_id())
                        .collection("art_works")
                        .document(artWork.getId())
                        .delete();


                artWorkList.remove(artWork);
                notifyItemRemoved(item_position);
                notifyItemRangeChanged(item_position, artWorkList.size());

            }
        });


    }

    @Override
    public int getItemCount() {
        return artWorkList.size();
    }
}


class DesignerWorkArtItem extends RecyclerView.ViewHolder{

    ImageView image_view_avatar;
    TextView text_view_description;
    RelativeLayout card_view_art_work;
    ImageButton image_button_delete;

    public DesignerWorkArtItem(@NonNull View itemView) {
        super(itemView);
        image_view_avatar=itemView.findViewById(R.id.image_view_avatar);
        text_view_description=itemView.findViewById(R.id.text_view_description);
        card_view_art_work=itemView.findViewById(R.id.card_view_art_work);
        image_button_delete=itemView.findViewById(R.id.image_button_delete);


    }
}
