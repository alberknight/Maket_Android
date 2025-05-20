package com.example.apiconecta1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText etEmail, etPassword;
    private LinearLayout verificationLayout;
    private TextView tvVerificationEmail;
    private Button btnResendEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        verificationLayout = findViewById(R.id.layout_verificacion);
        tvVerificationEmail = findViewById(R.id.tv_verification_email);
        btnResendEmail = findViewById(R.id.btn_resend_email);

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> loginUser());

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

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            if (user.isEmailVerified()) {
                                // Correo verificado - redirigir al catálogo
                                startActivity(new Intent(this, CatalogoResenasActivity.class));
                                finish();
                            } else {
                                // Correo no verificado
                                showVerificationUI(user);
                                mAuth.signOut(); // Cerrar sesión hasta verificación
                            }
                        }
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showVerificationUI(FirebaseUser user) {
        verificationLayout.setVisibility(View.VISIBLE);
        tvVerificationEmail.setText("Por favor verifica tu correo: " + user.getEmail());

        btnResendEmail.setOnClickListener(v -> {
            sendVerificationEmail(user);
            // Iniciar verificación periódica
            startEmailVerificationCheck(user);
        });

        // Iniciar verificación periódica
        startEmailVerificationCheck(user);
    }

    private void startEmailVerificationCheck(FirebaseUser user) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                user.reload().addOnCompleteListener(task -> {
                    if (user.isEmailVerified()) {
                        verificationLayout.setVisibility(View.GONE);
                        loginUser(); // Intentar login nuevamente
                    } else {
                        // Continuar verificando cada 5 segundos
                        handler.postDelayed(this, 5000);
                    }
                });
            }
        }, 5000); // Verificar cada 5 segundos
    }

    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Correo de verificación reenviado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error al reenviar correo", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void resetPassword() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Ingresa tu correo electrónico", Toast.LENGTH_SHORT).show();
            return;
        }

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