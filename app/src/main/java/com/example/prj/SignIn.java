package com.example.prj;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.UUID;

import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class SignIn extends AppCompatActivity {

    // Sign in definition
    EditText signinUsername, signinPassord;
    Button signinButton;
    TextView switchtosignupText;

    // Validate username and password
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
            signinPassord.setError("Password can't be empty");
            return false;
        }
        else {
            signinPassord.setError(null);
            return true;
        }
    }

    // Check if the user username and password from database
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

    // QR Code Generation Function
    public Bitmap generateQRCode(String sessionId) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(sessionId, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
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

        //QR Code Generation (displayed on the screen)
        ImageView qrCodeImage = findViewById(R.id.qrCodeImageView);
        String sessionId = UUID.randomUUID().toString();  // Generate a unique session ID
        Bitmap qrCodeBitmap = generateQRCode(sessionId);
        qrCodeImage.setImageBitmap(qrCodeBitmap);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference qrCodeRef = database.getReference("qrCodes");

        qrCodeRef.child(sessionId).setValue("waiting_for_login");

        // Sign in
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

        // Switch to sign up page
        switchtosignupText = findViewById(R.id.signin_text3);
        switchtosignupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SignUp.class);
                view.getContext().startActivity(intent);
            }
        });

        TextView usernameForm = findViewById(R.id.signin_text5);
        TextView qrcodeForm = findViewById(R.id.signin_text4);

        qrcodeForm = findViewById(R.id.signin_text4);
        qrcodeForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrCodeImage.setVisibility(View.VISIBLE);
                signinUsername.setVisibility(View.GONE);
                signinPassord.setVisibility(View.GONE);
                signinButton.setVisibility(View.GONE);
            }
        });

        usernameForm = findViewById(R.id.signin_text5);
        usernameForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrCodeImage.setVisibility(View.GONE);
                signinUsername.setVisibility(View.VISIBLE);
                signinPassord.setVisibility(View.VISIBLE);
                signinButton.setVisibility(View.VISIBLE);
            }
        });
    }
}