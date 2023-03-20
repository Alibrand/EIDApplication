package com.ksacp2022.electronicinteriordesignapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText edit_text_email;
    AppCompatButton button_reset_password;
    ProgressDialog dialog;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        edit_text_email=findViewById(R.id.edit_text_email);
        button_reset_password=findViewById(R.id.button_reset_password);

        auth=FirebaseAuth.getInstance();

        //initiate progress dialog

        dialog=new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);


        button_reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=edit_text_email.getText().toString();

                if(email.isEmpty())
                {
                    edit_text_email.setError("This field is required");
                    return;
                }
                if(!isValidEmail(email)){
                    edit_text_email.setError("Invalid Email format.Email should be like example@mail.com");
                    edit_text_email.requestFocus();
                    return;
                }


                dialog.setTitle("Sending Link");
                dialog.setMessage("Please Wait...");
                dialog.show();

                auth.sendPasswordResetEmail(email)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ResetPasswordActivity.this,"Check your inbox and follow the link to reset your password",Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ResetPasswordActivity.this,"Error :"+e.getMessage(),Toast.LENGTH_LONG).show();
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
