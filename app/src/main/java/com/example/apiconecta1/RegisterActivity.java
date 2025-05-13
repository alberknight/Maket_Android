package com.example.apiconecta1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText etUsername, etEmail, etPhone, etAddress, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Vincular vistas
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etPassword = findViewById(R.id.etPassword);

        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        // Validaciones básicas
        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Nombre, correo y contraseña son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear usuario en Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Guardar datos adicionales en Firestore
                        String userId = mAuth.getCurrentUser().getUid();
                        saveUserToFirestore(userId, username, email, phone, address);
                    } else {
                        Toast.makeText(this, "Error al registrar: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToFirestore(String userId, String username, String email, String phone, String address) {
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("email", email);
        user.put("phone", phone);
        user.put("address", address);
        user.put("timestamp", System.currentTimeMillis());

        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Registro completado!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, CatalogoResenasActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    // Opcional: eliminar el usuario creado en Auth si falla Firestore
                    mAuth.getCurrentUser().delete();
                });
    }
}