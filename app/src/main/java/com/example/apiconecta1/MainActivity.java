package com.example.apiconecta1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance(); // Inicializar Firebase Auth

        // Verificar si el usuario ya inició sesión
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Usuario ya está autenticado, redirigir a CatalogoActivity
            startActivity(new Intent(this, CatalogoResenasActivity.class));
            finish(); // Evitar que el usuario regrese a MainActivity con el botón "Atrás"
            return; // Salir del método onCreate para evitar ejecutar el resto del código
        }

        // Botón para ir a RegisterActivity (solo visible si no hay sesión)
        Button btnGoToRegister = findViewById(R.id.btnGoToRegister);
        btnGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        // Botón para ir a LoginActivity (solo visible si no hay sesión)
        Button btnGoToLogin = findViewById(R.id.btnGoToLogin);
        btnGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }
}