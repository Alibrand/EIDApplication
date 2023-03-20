package com.ksacp2022.electronicinteriordesignapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.GravityInt;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ksacp2022.electronicinteriordesignapplication.DecorStoreProfileActivity;
import com.ksacp2022.electronicinteriordesignapplication.ImageViewerActivity;
import com.ksacp2022.electronicinteriordesignapplication.R;
import com.ksacp2022.electronicinteriordesignapplication.models.ChatMessage;
import com.ksacp2022.electronicinteriordesignapplication.models.DecorStore;
import com.ksacp2022.electronicinteriordesignapplication.models.GlideApp;
import com.ksacp2022.electronicinteriordesignapplication.models.Participant;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MessagesListAdapter extends RecyclerView.Adapter<MessageItem> {
    List<ChatMessage> chatMessageList;
    Context context;
    FirebaseStorage storage;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    public MessagesListAdapter(List<ChatMessage> chatMessageList, Context context) {
        this.chatMessageList = chatMessageList;
        this.context = context;
        storage=FirebaseStorage.getInstance();
        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public MessageItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message_list_item,parent,false);
        return new MessageItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageItem holder, int position) {
        int pos=position;
        ChatMessage message= chatMessageList.get(pos);

        String current_user_id=firebaseAuth.getUid();

        String receiver= message.getTo();



        //check if the current user is the receiver (incoming message)
        //so change the background color and the direction of the message
        if(receiver.equals(current_user_id))
        {
            holder.layout_message_card.setGravity(Gravity.LEFT);
            holder.card_text.setCardBackgroundColor(Color.parseColor("#DEE6CC"));
            holder.card_image.setCardBackgroundColor(Color.parseColor("#DEE6CC"));
        }

        //check message type
        //if the message is just a simple text
        if(message.getType().equals("text"))
        {
            //hide image card
            holder.card_image.setVisibility(View.GONE);
            //set the text
            holder.text_view_text.setText(message.getText());
            //set time
            SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
            if(message.getSent_at()==null)
            {
                Calendar calendar=Calendar.getInstance();
                holder.text_view_time.setText(sdf.format(calendar.getTime()));
            }else
            holder.text_view_time.setText(sdf.format(message.getSent_at()));

        }
        else{
            //if message is an image
            //hide the text card
            holder.card_text.setVisibility(View.GONE);
            StorageReference ref=storage.getReference();
            StorageReference message_image=ref.child("chats_images/"+message.getImage_url());

            GlideApp.with(context)
                    .load(message_image)
                    .transform(new RoundedCorners(15))
                    .into(holder.image_view_message_image);
            holder.card_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ImageViewerActivity. class);
                    intent.putExtra("image_url","chats_images/"+message.getImage_url());
                    intent.putExtra("description","");

                    context.startActivity(intent);
                }
            });
        }





    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }
}

class MessageItem extends RecyclerView.ViewHolder{
    CardView card_text,card_image;
    TextView text_view_text,text_view_time;
    ImageView image_view_message_image;
    LinearLayoutCompat layout_message_card;

    public MessageItem(@NonNull View itemView) {
        super(itemView);
        card_text=itemView.findViewById(R.id.card_text);
        card_image=itemView.findViewById(R.id.card_image);
        text_view_text=itemView.findViewById(R.id.text_view_text);
        text_view_time=itemView.findViewById(R.id.text_view_time);
        image_view_message_image=itemView.findViewById(R.id.image_view_message_image);
        layout_message_card=itemView.findViewById(R.id.layout_message_card);
    }
}
