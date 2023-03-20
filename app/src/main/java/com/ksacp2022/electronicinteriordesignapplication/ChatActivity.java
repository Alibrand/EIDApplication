package com.ksacp2022.electronicinteriordesignapplication;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ksacp2022.electronicinteriordesignapplication.adapters.MessagesListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.adapters.RoomDesignImagesListAdapter;
import com.ksacp2022.electronicinteriordesignapplication.models.Chat;
import com.ksacp2022.electronicinteriordesignapplication.models.ChatMessage;
import com.ksacp2022.electronicinteriordesignapplication.models.Participant;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    ImageView image_view_avatar;
    ImageButton button_shared_designs, button_send, button_gallery, button_take_pic;
    RecyclerView recycler_messages;
    EditText edit_text_message;
    TextView text_view_user_name;


    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    ProgressBar progress_sending, progress_uploading;

    ListenerRegistration messagengerListener;

    List<ChatMessage> chatMessages;
    String chat_id, sender_id, receiver_id, receiver_name, receiver_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        image_view_avatar = findViewById(R.id.image_view_avatar);
        button_shared_designs = findViewById(R.id.button_shared_designs);
        button_send = findViewById(R.id.button_send);
        button_gallery = findViewById(R.id.button_gallery);
        button_take_pic = findViewById(R.id.button_take_pic);
        recycler_messages = findViewById(R.id.recycler_messages);
        edit_text_message = findViewById(R.id.edit_text_message);
        text_view_user_name = findViewById(R.id.text_user_name);
        progress_sending = findViewById(R.id.progress_sending);
        progress_uploading = findViewById(R.id.progress_uploading);


        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        //current user id
        sender_id = firebaseAuth.getUid();
        receiver_name = getIntent().getStringExtra("receiver_name");
        receiver_id = getIntent().getStringExtra("receiver_id");


        //if this is a new chat with this user
        //create a unique id for this chat
        if (getIntent().getStringExtra("chat_id") == null)
            chat_id = sender_id + "_" + receiver_id;
        else
            chat_id = getIntent().getStringExtra("chat_id");

        //change all messages status sent to receiver to seen
        set_seen();


        text_view_user_name.setText(receiver_name);




        //listening to every change in  chat's messages
        //adding a snapshot listener
        messagengerListener = firestore.collection("chats")
                .document(chat_id)
                .collection("messages")
                .orderBy("sent_at", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        chatMessages = new ArrayList<>();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Map<String, Object> data = doc.getData();

                            ChatMessage chatMessage = new ChatMessage();
                            String sender = doc.toObject(ChatMessage.class).getFrom();
                            chatMessage.setFrom(sender);
                            String receiver = doc.toObject(ChatMessage.class).getTo();
                            chatMessage.setTo(receiver);
                            // Timestamp sent= (Timestamp) data.get("sent_at");
                            chatMessage.setSent_at(doc.toObject(ChatMessage.class).getSent_at());
                            chatMessage.setText(data.get("text").toString());
                            chatMessage.setImage_url(data.get("image_url").toString());
                            chatMessage.setType(data.get("type").toString());
                            chatMessage.setId(doc.getId());
                            chatMessages.add(chatMessage);
                        }

                        set_seen();

                        MessagesListAdapter adapter = new MessagesListAdapter(chatMessages, ChatActivity.this);
                        recycler_messages.setAdapter(adapter);
                        recycler_messages.scrollToPosition(chatMessages.size() - 1);


                    }
                });

        button_shared_designs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this,SharedDesignsActivity. class);
                intent.putExtra("share_with_id",receiver_id);
                intent.putExtra("share_with_name",receiver_name);
                startActivity(intent);
            }
        });


        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message_text = edit_text_message.getText().toString();
                if (message_text.isEmpty())
                    return;
                edit_text_message.setText("");

                progress_sending.setVisibility(View.VISIBLE);
                button_send.setVisibility(View.GONE);
                //if this is the first message ever
                //create a chat document
                if (chatMessages.size() == 0) {
                    //get current user name (sender name)
                    firestore.collection("profiles")
                            .document(sender_id)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String sender_name = documentSnapshot.getData().get("full_name").toString();
                                    Chat new_chat = new Chat();
                                    new_chat.setLast_update(null);
                                    Map<String, String> participants = new HashMap<>();
                                    participants.put(sender_id, sender_name);
                                    participants.put(receiver_id, receiver_name);
                                    new_chat.setParticipants(participants);


                                    firestore.collection("chats")
                                            .document(chat_id)
                                            .set(new_chat)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    send(message_text);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    makeText(ChatActivity.this, "Failed to create new  chat", LENGTH_LONG).show();
                                                    progress_sending.setVisibility(View.GONE);
                                                    button_send.setVisibility(View.VISIBLE);
                                                }
                                            });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    makeText(ChatActivity.this, "Failed to get user info", LENGTH_LONG).show();
                                    progress_sending.setVisibility(View.GONE);
                                    button_send.setVisibility(View.VISIBLE);
                                }
                            });


                }
                //if all operations done successfully so time to send the message
                else
                    send(message_text);
            }
        });

        button_take_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED ||
                        ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED)
                        startActivityForResult(takePicture, 100);
                } else {
                    startActivityForResult(takePicture, 100);

                }
            }
        });

        button_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create an instance of the
                // intent of the type image
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // pass the constant to compare it
                // with the returned requestCode
                startActivityForResult(Intent.createChooser(intent, "Select Pictures"), 110);

            }
        });


    }

    private void send(String text) {

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setText(text);
        chatMessage.setFrom(sender_id);
        chatMessage.setTo(receiver_id);


        firestore.collection("chats")
                .document(chat_id)
                .collection("messages")
                .add(chatMessage)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progress_sending.setVisibility(View.GONE);
                        button_send.setVisibility(View.VISIBLE);
                        //update chat last_update
                        firestore.collection("chats")
                                .document(chat_id)
                                .update("last_update", FieldValue.serverTimestamp());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeText(ChatActivity.this, "Failed to send message", LENGTH_LONG).show();
                        progress_sending.setVisibility(View.GONE);
                        button_send.setVisibility(View.VISIBLE);
                    }
                });

    }

    private void set_seen() {
        firestore.collection("chats")
                .document(chat_id)
                .collection("messages")
                .whereEqualTo("to", sender_id)
                .whereEqualTo("status", "unseen")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            //update messages status
                            firestore.collection("chats")
                                    .document(chat_id)
                                    .collection("messages")
                                    .document(doc.getId())
                                    .update("status", "seen");
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        messagengerListener.remove();
        super.onDestroy();

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == 110) {

                Uri image_uri = data.getData();

                //define unique image name usinf UUID generator
                String imageName = UUID.randomUUID().toString() + ".jpg";

                // Defining the child of storageReference
                StorageReference storageReference = firebaseStorage.getReference();
                StorageReference ref = storageReference.child("chats_images/" + imageName);
                progress_uploading.setVisibility(View.VISIBLE);
                button_gallery.setVisibility(View.GONE);

                ref.putFile(image_uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                ChatMessage chatMessage = new ChatMessage();
                                chatMessage.setImage_url(imageName);
                                chatMessage.setType("image");
                                chatMessage.setFrom(sender_id);
                                chatMessage.setTo(receiver_id);


                                firestore.collection("chats")
                                        .document(chat_id)
                                        .collection("messages")
                                        .add(chatMessage)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                progress_uploading.setVisibility(View.GONE);
                                                button_gallery.setVisibility(View.VISIBLE);
                                                //update chat last_update
                                                firestore.collection("chats")
                                                        .document(chat_id)
                                                        .update("last_update", FieldValue.serverTimestamp());
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                makeText(ChatActivity.this, "Failed to send message", LENGTH_LONG).show();
                                                progress_uploading.setVisibility(View.GONE);
                                                button_gallery.setVisibility(View.VISIBLE);
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                makeText(ChatActivity.this, "Failed to upload image", LENGTH_LONG).show();
                                progress_uploading.setVisibility(View.GONE);
                                button_gallery.setVisibility(View.VISIBLE);
                            }
                        });


            } else if (requestCode == 100) {

                Bitmap image = (Bitmap) data.getExtras().get("data");
                //prepare image to upload
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                String path = MediaStore.Images.Media.insertImage(getContentResolver(), image, "Title", null);


                Uri image_uri = Uri.parse(path);
                //define unique image name usinf UUID generator
                String imageName = UUID.randomUUID().toString() + ".jpg";

                // Defining the child of storageReference
                StorageReference storageReference = firebaseStorage.getReference();
                StorageReference ref = storageReference.child("chats_images/" + imageName);
                progress_uploading.setVisibility(View.VISIBLE);
                button_take_pic.setVisibility(View.GONE);

                ref.putFile(image_uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                ChatMessage chatMessage = new ChatMessage();
                                chatMessage.setImage_url(imageName);
                                chatMessage.setType("image");
                                chatMessage.setFrom(sender_id);
                                chatMessage.setTo(receiver_id);


                                firestore.collection("chats")
                                        .document(chat_id)
                                        .collection("messages")
                                        .add(chatMessage)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                progress_uploading.setVisibility(View.GONE);
                                                button_take_pic.setVisibility(View.VISIBLE);
                                                //update chat last_update
                                                firestore.collection("chats")
                                                        .document(chat_id)
                                                        .update("last_update", FieldValue.serverTimestamp());
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                makeText(ChatActivity.this, "Failed to send message", LENGTH_LONG).show();
                                                progress_uploading.setVisibility(View.GONE);
                                                button_take_pic.setVisibility(View.VISIBLE);
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                makeText(ChatActivity.this, "Failed to upload image", LENGTH_LONG).show();
                                progress_uploading.setVisibility(View.GONE);
                                button_take_pic.setVisibility(View.VISIBLE);
                            }
                        });


            }
        }

    }
}