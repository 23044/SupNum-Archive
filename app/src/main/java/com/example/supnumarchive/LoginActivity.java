package com.example.supnumarchive;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private EditText passwordEditText;
    private Button loginButton;
    private EditText loginEmail;
    private ImageButton togglePasswordButton;
    private TextView signupText, forgotPasswordText;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialisation des vues
        loginButton = findViewById(R.id.loginButton);
        loginEmail = findViewById(R.id.LoginEmail); // Assurez-vous que l'ID correspond à votre XML
        passwordEditText = findViewById(R.id.passwordEditText);
        togglePasswordButton = findViewById(R.id.togglePasswordButton);
        signupText = findViewById(R.id.signupText);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateEmail() || !validatePassword()) {
                    // Les messages d'erreur sont déjà gérés dans les méthodes validate
                } else {
                    checkUser();
                }
            }
        });

        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        togglePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    // Masquer le mot de passe
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    togglePasswordButton.setImageResource(R.drawable.baseline_visibility_off_24);
                } else {
                    // Afficher le mot de passe
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    togglePasswordButton.setImageResource(R.drawable.baseline_visibility_off_24);
                }
                isPasswordVisible = !isPasswordVisible;
                passwordEditText.setSelection(passwordEditText.getText().length());
            }
        });
    }

    private Boolean validateEmail() {
        String val = loginEmail.getText().toString();
        if (val.isEmpty()) {
            loginEmail.setError("Email ne peut pas être vide");
            return false;
        } else {
            loginEmail.setError(null);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = passwordEditText.getText().toString();
        if (val.isEmpty()) {
            passwordEditText.setError("Le mot de passe ne peut pas être vide");
            return false;
        } else {
            passwordEditText.setError(null);
            return true;
        }
    }

    private void checkUser() {
        String userEmail = loginEmail.getText().toString().trim();
        String userPassword = passwordEditText.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("email").equalTo(userEmail);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Trouver l'utilisateur avec cet email
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String passwordFromDB = userSnapshot.child("password").getValue(String.class);

                        if (Objects.equals(passwordFromDB, userPassword)) {
                            // Mot de passe correct
                            loginEmail.setError(null);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Fermer l'activité de login
                        } else {
                            // Mot de passe incorrect
                            passwordEditText.setError("Mot de passe incorrect");
                            passwordEditText.requestFocus();
                        }
                        return; // Sortir après avoir trouvé l'utilisateur
                    }
                } else {
                    // Utilisateur non trouvé
                    loginEmail.setError("L'utilisateur n'existe pas !");
                    loginEmail.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Erreur de base de données: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}