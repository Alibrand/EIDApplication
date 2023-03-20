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

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ksacp2022.electronicinteriordesignapplication.ChatActivity;
import com.ksacp2022.electronicinteriordesignapplication.R;
import com.ksacp2022.electronicinteriordesignapplication.models.Chat;
import com.ksacp2022.electronicinteriordesignapplication.models.ChatMessage;
import com.ksacp2022.electronicinteriordesignapplication.models.GlideApp;
import com.ksacp2022.electronicinteriordesignapplication.models.Participant;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatsListAdapter extends RecyclerView.Adapter<ChatItem> {
    List<Chat> chatList;
    Context context;
    FirebaseStorage storage;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    public ChatsListAdapter(List<Chat> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
        storage=FirebaseStorage.getInstance();
        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ChatItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_list_item,parent,false);
        return new ChatItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatItem holder, int position) {
        int pos=position;
        Chat chat= chatList.get(pos);

        String current_user_id=firebaseAuth.getUid();
        String receiver_name="",receiver_id="";


        Map<String,String>participants=chat.getParticipants();

        //find the receiver user
        for (Map.Entry<String,String> participant:participants.entrySet()
             ) {
            if(!participant.getKey().equals(current_user_id))
            {
                receiver_id=participant.getKey();
                receiver_name=participant.getValue();
                break;
            }
        }



        holder.text_user_name.setText(receiver_name);
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
        holder.text_date.setText(sdf.format(chat.getLast_update()));


        String finalReceiver_name = receiver_name;
        String finalReceiver_id = receiver_id;
        holder.card_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity. class);
                intent.putExtra("receiver_name", finalReceiver_name);
                intent.putExtra("receiver_id", finalReceiver_id);
                intent.putExtra("chat_id",chat.getId());
                context.startActivity(intent);
            }
        });










    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}

class ChatItem extends RecyclerView.ViewHolder{
    CardView card_chat;
    TextView text_user_name,text_date;
    ImageView image_view_avatar;

    public ChatItem(@NonNull View itemView) {
        super(itemView);
        card_chat=itemView.findViewById(R.id.card_chat);
        text_user_name=itemView.findViewById(R.id.text_user_name);
        text_date=itemView.findViewById(R.id.text_date);
        image_view_avatar=itemView.findViewById(R.id.image_view_avatar);

    }
}
