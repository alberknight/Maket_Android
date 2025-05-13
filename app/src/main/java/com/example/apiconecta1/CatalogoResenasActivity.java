package com.example.apiconecta1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class CatalogoResenasActivity extends AppCompatActivity {

    private RecyclerView rvResenas;
    private ResenaAdapter adapter;
    private FloatingActionButton fabNuevaResena;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo_resenas);

        // Configurar RecyclerView
        rvResenas = findViewById(R.id.rvResenas);
        rvResenas.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ResenaAdapter(new ArrayList<>());
        rvResenas.setAdapter(adapter);

        // Botones
        fabNuevaResena = findViewById(R.id.fabNuevaResena);
        btnLogout = findViewById(R.id.btnLogout);

        fabNuevaResena.setOnClickListener(v -> {
            startActivity(new Intent(this, NuevaResenaActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        cargarResenas();
    }

    private void cargarResenas() {
        FirebaseFirestore.getInstance().collection("resenas")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error al cargar reseñas", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<Resena> resenas = new ArrayList<>();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Resena resena = doc.toObject(Resena.class);
                        resena.setId(doc.getId());
                        resenas.add(resena);
                    }
                    adapter = new ResenaAdapter(resenas);
                    rvResenas.setAdapter(adapter);
                });
    }
}