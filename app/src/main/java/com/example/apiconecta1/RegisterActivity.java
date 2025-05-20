package com.example.apiconecta1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText etUsername, etEmail, etPhone, etAddress, etPassword;
    private LinearLayout layoutVerificacion;
    private ProgressDialog progressDialog;
    private Handler verificationHandler = new Handler();
    private Runnable verificationRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etPassword = findViewById(R.id.etPassword);
        layoutVerificacion = findViewById(R.id.layout_verificacion);

        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Nombre, correo y contraseña son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registrando usuario...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            sendVerificationEmail(user);
                            saveUserData(user, username, email, phone, address);
                        }
                    } else {
                        progressDialog.dismiss();
                        if (task.getException() instanceof FirebaseNetworkException) {
                            Toast.makeText(this, "Error de red. Verifica tu conexión", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showVerificationLayout(user.getEmail());
                        startEmailVerificationCheck(user);
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Error al enviar correo de verificación", Toast.LENGTH_SHORT).show();
                        Log.e("EmailVerification", "Error al enviar correo", task.getException());
                    }
                });
    }

    private void showVerificationLayout(String email) {
        progressDialog.dismiss();
        layoutVerificacion.setVisibility(View.VISIBLE);
        TextView tvMessage = findViewById(R.id.tv_verification_message);
        tvMessage.setText("Hemos enviado un correo de verificación a " + email);

        Button btnResend = findViewById(R.id.btn_resend_email);
        btnResend.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                sendVerificationEmail(user);
            }
        });
    }

    private void startEmailVerificationCheck(FirebaseUser user) {
        verificationRunnable = new Runnable() {
            @Override
            public void run() {
                user.reload().addOnCompleteListener(task -> {
                    if (user.isEmailVerified()) {
                        verificationHandler.removeCallbacks(verificationRunnable);
                        redirectToCatalog();
                    } else {
                        verificationHandler.postDelayed(this, 3000); // Revisar cada 3 segundos
                    }
                });
            }
        };
        verificationHandler.postDelayed(verificationRunnable, 3000);
    }

    private void saveUserData(FirebaseUser user, String username, String email, String phone, String address) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", email);
        userData.put("phone", phone);
        userData.put("address", address);
        userData.put("emailVerified", false);
        userData.put("createdAt", FieldValue.serverTimestamp());

        db.collection("users").document(user.getUid())
                .set(userData)
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error al guardar datos", e);
                    user.delete(); // Eliminar usuario de Auth si falla Firestore
                });
    }

    private void redirectToCatalog() {
        startActivity(new Intent(this, CatalogoResenasActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (verificationHandler != null && verificationRunnable != null) {
            verificationHandler.removeCallbacks(verificationRunnable);
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}