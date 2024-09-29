package com.example.prj;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SignIn extends AppCompatActivity {

    EditText signinUsername, signinPassord;
    Button signinButton;
    TextView switchtosignupText;
    FirebaseDatabase database;
    DatabaseReference reference;

    public Boolean validateUsername(){
        String val = signinUsername.getText().toString();
        if (val.isEmpty()){
            signinUsername.setError("Username can't be empty");
            return false;
        }
        else {
            signinUsername.setError(null);
            return true;
        }
    }

    public Boolean validatePassword(){
        String val = signinPassord.getText().toString();
        if (val.isEmpty()){
            signinPassord.setError("Username can't be empty");
            return false;
        }
        else {
            signinPassord.setError(null);
            return true;
        }
    }

    public void checkUser(){
        String userUsername = signinUsername.getText().toString().trim();
        String userPassword = signinPassord.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    signinUsername.setError(null);
                    String passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);

                    if (Objects.equals(passwordFromDB, userPassword)){
                        signinUsername.setError(null);
                        Intent intent = new Intent(SignIn.this, MainPage.class);
                        startActivity(intent);
                    }
                    else {
                        signinPassord.setError("Invalid Credentials");
                        signinPassord.requestFocus();
                    }
                }
                else {
                    signinUsername.setError("Username didn't exist");
                    signinUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        signinUsername = findViewById(R.id.signin_username);
        signinPassord = findViewById(R.id.signin_password);

        signinButton = findViewById(R.id.signin_button);
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUsername() | !validatePassword()){
                }
                else {
                    checkUser();
                }
            }
        });

        switchtosignupText = findViewById(R.id.signin_text3);
        switchtosignupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SignUp.class);
                view.getContext().startActivity(intent);
            }
        });
    }
}