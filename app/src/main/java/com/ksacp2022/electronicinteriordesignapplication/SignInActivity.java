package com.ksacp2022.electronicinteriordesignapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022.electronicinteriordesignapplication.models.Chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {

    EditText edit_text_email,edit_text_pasword;
    TextView text_goto_signup,text_forgot_password;
    AppCompatButton button_sign_in;
    FirebaseAuth auth;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        // linking
        edit_text_email=findViewById(R.id.edit_text_email);
        edit_text_pasword=findViewById(R.id.edit_text_password);
        text_forgot_password=findViewById(R.id.text_forgot_password);
        text_goto_signup=findViewById(R.id.text_goto_signup);
        button_sign_in=findViewById(R.id.button_sign_in);




        //initiate firesbase auth
        auth=FirebaseAuth.getInstance();


        //check if a user is already logged in
        if(auth.getCurrentUser()!=null)
        {
            //jump to home page directly
            if(auth.getCurrentUser().getEmail().equals("admin@eida.com"))
            {
                Intent intent=new Intent(SignInActivity.this,AdminHomeActivity.class);

                startActivity(intent);
            }
            else{
                Intent intent=new Intent(SignInActivity.this,MainActivity.class);

                startActivity(intent);
            }
            finish();
        }

        //initiate progress dialog

        dialog=new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);


        text_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SignInActivity.this,ResetPasswordActivity.class);
                startActivity(intent);
            }
        });


        text_goto_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });

        button_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=edit_text_email.getText().toString();
                String password=edit_text_pasword.getText().toString();


                //CHECK FOR ERRORS

                if(email.isEmpty())
                {
                    edit_text_email.setError("This field is required");
                    edit_text_email.requestFocus();
                    return;
                }
                if(!isValidEmail(email)){
                    edit_text_email.setError("Invalid Email format.Email should be like example@mail.com");
                    edit_text_email.requestFocus();
                    return;
                }
                if(password.isEmpty())
                {
                    edit_text_pasword.setError("This field is required");
                    edit_text_pasword.requestFocus();
                    return;
                }

                //show dialog

                dialog.setTitle("Signing in");
                dialog.setMessage("Please Wait..");
                dialog.show();
                //sign in

                auth.signInWithEmailAndPassword(email,password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(SignInActivity.this,"Welcome",Toast.LENGTH_LONG).show();
                                if(email.equals("admin@eida.com"))
                                {
                                    Intent intent=new Intent(SignInActivity.this,AdminHomeActivity.class);

                                    startActivity(intent);
                                }
                                else{
                                    Intent intent=new Intent(SignInActivity.this,MainActivity.class);

                                    startActivity(intent);
                                }

                                finish();
                                dialog.dismiss();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignInActivity.this,"Sign in error :"+e.getMessage(),Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        });





            }
        });



    }

    public  boolean isValidEmail(final String email) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,6}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(email);

        return matcher.matches();

    }
}