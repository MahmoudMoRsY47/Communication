package com.example.communication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {
    EditText edt_email_signup,edt_pass_signup,edt_pass_again_signup;
    Button btn;
     FirebaseAuth mAuth  = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        edt_email_signup=findViewById(R.id.edt_email_signup);
        edt_pass_signup=findViewById(R.id.edt_pass_signup);
        edt_pass_again_signup=findViewById(R.id.edt_pass2_signup);
        btn=findViewById(R.id.btn_signup_);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }

            private void signUp() {
                String email_signup=edt_email_signup.getText().toString();
                String pass_signup=edt_pass_signup.getText().toString();
                String pass_again=edt_pass_again_signup.getText().toString();
                if (email_signup.isEmpty() || pass_signup.isEmpty() || pass_again.isEmpty())
                {
                    Toast.makeText(SignUp.this, "Please fill all data", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!pass_signup.equals(pass_again))
                {
                    Toast.makeText(SignUp.this, "Please write matching password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pass_signup.length()<6)
                {
                    Toast.makeText(SignUp.this, "Please write long password", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email_signup, pass_signup).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUp.this, "User created Successfully", Toast.LENGTH_SHORT).show();
                            finish();

                        } else {
                            Toast.makeText(SignUp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }
        });



    }
}