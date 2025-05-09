package com.example.apiconecta1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> loginUser());
        // Botón "Olvidé mi contraseña"
        Button btnForgotPassword = findViewById(R.id.btnForgotPassword);
        btnForgotPassword.setOnClickListener(v -> resetPassword());
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        // Iniciar sesión con Firebase Auth
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Redirigir a CatalogoActivity
                        startActivity(new Intent(this, CatalogoActivity.class));
                        finish(); // Cerrar LoginActivity para evitar volver atrás
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
    private void resetPassword() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Ingresa tu correo electrónico sin la contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        // Enviar correo de restablecimiento
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Correo enviado a " + email, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}