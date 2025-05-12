package com.example.apiconecta1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class CatalogoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);

        // Mostrar email del usuario actual (opcional)
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userEmail = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : "Invitado";
        Toast.makeText(this, "Bienvenido: " + userEmail, Toast.LENGTH_SHORT).show();

        // Dentro de onCreate() de CatalogoActivity
        Button btnNuevaPublicacion = findViewById(R.id.btnNuevaPublicacion);
        btnNuevaPublicacion.setOnClickListener(v -> {
            startActivity(new Intent(this, PublicacionActivity.class));
        });

        // Botón para cerrar sesión
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut(); // Cierra la sesión en Firebase
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();

        // Redirigir a MainActivity y limpiar el historial de navegación
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Cierra la actividad actual
    }

}