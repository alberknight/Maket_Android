package com.example.apiconecta1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NuevaResenaActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_resena);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Button btnPublicar = findViewById(R.id.btnPublicarResena);
        btnPublicar.setOnClickListener(v -> publicarResena());
    }

    private void publicarResena() {
        String lugar = ((EditText) findViewById(R.id.etLugar)).getText().toString();
        float puntuacion = ((RatingBar) findViewById(R.id.ratingBar)).getRating();
        String comentario = ((EditText) findViewById(R.id.etComentario)).getText().toString();

        if (lugar.isEmpty() || comentario.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener nombre del usuario actual (desde Firestore)
        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String nombreUsuario = documentSnapshot.getString("username");

                    // Crear objeto Reseña
                    Map<String, Object> resena = new HashMap<>();
                    resena.put("usuarioId", mAuth.getCurrentUser().getUid());
                    resena.put("nombreUsuario", nombreUsuario);
                    resena.put("lugar", lugar);
                    resena.put("puntuacion", puntuacion);
                    resena.put("comentario", comentario);
                    resena.put("fecha", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
                    resena.put("timestamp", FieldValue.serverTimestamp());

                    // Guardar en Firestore
                    db.collection("resenas").add(resena)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "¡Reseña publicada!", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                });
    }
}