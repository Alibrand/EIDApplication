package com.ksacp2022.electronicinteriordesignapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022.electronicinteriordesignapplication.adapters.ChatsListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.models.Chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class InboxActivity extends AppCompatActivity {

    RecyclerView recycler_chats;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    List<Chat> chats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        recycler_chats = findViewById(R.id.recycler_chats);



        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();

        String uid=firebaseAuth.getUid();


        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading Chats");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        firestore.collection("chats")
                .orderBy("participants."+uid, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressDialog.dismiss();
                        chats =new ArrayList<>();
                         for (DocumentSnapshot doc:queryDocumentSnapshots.getDocuments())
                        {
                            Chat chat=doc.toObject(Chat.class);
                            chat.setId(doc.getId());
                            chats.add(chat);
                        }
                        chats.sort((ch1,ch2)-> ch2.getLast_update().compareTo(ch1.getLast_update()));
                         ChatsListAdapter adapter=new ChatsListAdapter(chats,InboxActivity.this);
                        recycler_chats.setAdapter(adapter);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InboxActivity.this,"Failed to load chats" , Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        finish();

                    }
                });




    }
}