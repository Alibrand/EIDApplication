package com.ksacp2022.electronicinteriordesignapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022.electronicinteriordesignapplication.models.UserProfile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    EditText edit_text_email, edit_text_pasword, edit_text_confirm_pasword, edit_text_full_name;
    TextView text_goto_signin;
    RadioGroup radio_group_user_type;
    AppCompatSpinner spinner_locations;
    AppCompatButton button_sign_up;
    String[] locations = {"Al-Dammam", "Riyadh",
            "Jeddah", "Mekka",
            "Abha", "Al Taef", "Bisha", "Hael", "Najran"};
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edit_text_email = findViewById(R.id.edit_text_email);
        edit_text_pasword = findViewById(R.id.edit_text_password);
        edit_text_confirm_pasword = findViewById(R.id.edit_text_confirm_password);
        edit_text_email = findViewById(R.id.edit_text_email);
        edit_text_full_name = findViewById(R.id.edit_text_full_name);
        spinner_locations = findViewById(R.id.spinner_location);
        text_goto_signin = findViewById(R.id.text_goto_signin);
        radio_group_user_type = findViewById(R.id.radio_group_user_type);
        button_sign_up = findViewById(R.id.button_sign_up);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, locations);
        spinner_locations.setAdapter(arrayAdapter);

        //initiate firebase services
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        //initiate progress dialog

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);


        button_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String full_name = edit_text_full_name.getText().toString();
                String email = edit_text_email.getText().toString();
                String password = edit_text_pasword.getText().toString();
                String confirm_password = edit_text_confirm_pasword.getText().toString();
                String region = spinner_locations.getSelectedItem().toString();
                RadioButton selected = findViewById(radio_group_user_type.getCheckedRadioButtonId());
                String user_type = selected.getText().toString();

                //check errors

                if (full_name.isEmpty()) {
                    edit_text_full_name.setError("This field is required");
                    edit_text_full_name.requestFocus();
                    return;
                }

                if (email.isEmpty()) {
                    edit_text_email.setError("This field is required");
                    edit_text_email.requestFocus();
                    return;
                }
                if (!isValidEmail(email)) {
                    edit_text_email.setError("Invalid Email format.Email should be like example@mail.com");
                    edit_text_email.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    edit_text_pasword.setError("This field is required");
                    edit_text_pasword.requestFocus();
                    return;
                }
                if (confirm_password.isEmpty()) {
                    edit_text_confirm_pasword.setError("This field is required");
                    edit_text_confirm_pasword.requestFocus();
                    return;
                }

                if (!isValidPassword(password)) {
                    edit_text_pasword.setError("Password should be at least 8 , contains mix of upper and lower case letters with special symbols");
                    edit_text_pasword.requestFocus();
                    return;
                }

                if (!password.equals(confirm_password)) {
                    edit_text_confirm_pasword.setError("Passwords don't match");
                    edit_text_confirm_pasword.requestFocus();
                    return;
                }

                //no errors
                //create user and save profile to firestore

                UserProfile userProfile = new UserProfile();
                userProfile.setFull_name(full_name);
                userProfile.setUser_type(user_type);
                userProfile.setRegion(region);


                //show waiting dialog
                dialog.setTitle("Signing up");
                dialog.setMessage("Creating User...");
                dialog.show();

                //first register user in firebase auth

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener(SignUpActivity.this, new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                //if all is Ok
                                //get user id
                                String uid = authResult.getUser().getUid();
                                //save user profile to firestore
                                dialog.setMessage("Saving User Profile...");
                                firestore.collection("profiles")
                                        .document(uid).set(userProfile)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(SignUpActivity.this, "User Profile Created successfully", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                dialog.dismiss();
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(SignUpActivity.this, "Failed to save user profile :" + e.getMessage(), Toast.LENGTH_LONG).show();
                                                dialog.dismiss();
                                            }
                                        });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignUpActivity.this, "Failed to create user :" + e.getMessage(), Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        });

//


            }
        });


        text_goto_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z]).{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    public boolean isValidEmail(final String email) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,6}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(email);

        return matcher.matches();

    }

}