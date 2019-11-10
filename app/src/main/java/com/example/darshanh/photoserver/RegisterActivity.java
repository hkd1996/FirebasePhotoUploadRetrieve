package com.example.darshanh.photoserver;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    Button registerBtn;
    EditText email,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth = FirebaseAuth.getInstance();
        registerBtn=(Button)findViewById(R.id.btn_register);
        email=(EditText)findViewById(R.id.input_register_email);
        password=(EditText)findViewById(R.id.input_register_password);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_input=email.getText().toString().trim();
                String password_input=password.getText().toString().trim();

                auth.createUserWithEmailAndPassword(email_input,password_input).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "User Already exists!!" + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this,"Register Success. Login to continue", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
            }
        });

    }


}
