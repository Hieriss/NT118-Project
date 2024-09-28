package com.example.prj;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ThemedSpinnerAdapter;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    Button signupButton;
    TextView switchtosigninText;
    EditText signupUsername, signupPassword, signupConfirmpassword, signupEmail, signupPhone;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        signupConfirmpassword = findViewById(R.id.signup_confirmpassword);
        signupEmail = findViewById(R.id.signup_email);
        signupPhone = findViewById(R.id.signup_phone);

        signupButton = findViewById(R.id.signup_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("user");

                String username = signupUsername.getText().toString();
                String password = signupPassword.getText().toString();
                String confirmpassword = signupConfirmpassword.getText().toString();
                String email = signupEmail.getText().toString();
                String phone = signupPhone.getText().toString();

                if(TextUtils.isEmpty(username)){
                    Toast.makeText(SignUp.this, "Username can't be empty", Toast.LENGTH_SHORT).show();
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(SignUp.this, "Password can't be empty", Toast.LENGTH_SHORT).show();
                }
                if(TextUtils.isEmpty(confirmpassword)){
                    Toast.makeText(SignUp.this, "Confirm password can't be empty", Toast.LENGTH_SHORT).show();
                }
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(SignUp.this, "Email can't be empty", Toast.LENGTH_SHORT).show();
                }
                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(SignUp.this, "Phone Number can't be empty", Toast.LENGTH_SHORT).show();
                }

                if (password.equals(confirmpassword)){
                    HelperClass helperClass = new HelperClass(username, password, email, phone);
                    reference.child(username).setValue(helperClass);

                    Toast.makeText(SignUp.this, "Signed up successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(view.getContext(), SignIn.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(SignUp.this, "Password didn't match", Toast.LENGTH_SHORT).show();
                }
            }
        });

        switchtosigninText = findViewById(R.id.signup_text3);
        switchtosigninText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SignIn.class);
                view.getContext().startActivity(intent);
            }
        });
    }
}