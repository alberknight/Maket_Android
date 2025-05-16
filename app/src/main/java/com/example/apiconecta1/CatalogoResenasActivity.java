package com.example.apiconecta1;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentChange;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CatalogoResenasActivity extends AppCompatActivity {

    private RecyclerView rvResenas;
    private ResenaAdapter adapter;
    private FloatingActionButton fabNuevaResena;
    private Button btnLogout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo_resenas);

        // Configurar Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configurar RecyclerView
        rvResenas = findViewById(R.id.rvResenas);
        rvResenas.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ResenaAdapter(new ArrayList<>(), new ArrayList<>());
        rvResenas.setAdapter(adapter);

        // Botones
        fabNuevaResena = findViewById(R.id.fabNuevaResena);

        fabNuevaResena.setOnClickListener(v -> {
            startActivity(new Intent(this, NuevaResenaActivity.class));
        });

        cargarResenas();
    }

    private void cargarResenas() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("resenas")
                .whereNotEqualTo("usuarioId", currentUserId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("FirestoreError", "Error al cargar reseñas", error);
                        Snackbar.make(rvResenas, "Error al cargar reseñas", Snackbar.LENGTH_LONG)
                                .setAction("Reintentar", v -> cargarResenas())
                                .show();
                        return;
                    }

                    // 1. Procesar cambios individuales (incluyendo eliminaciones)
                    if (value != null) {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case REMOVED:
                                    adapter.removeResena(dc.getDocument().getId());
                                    break;
                                case ADDED:
                                case MODIFIED:
                                    // Estos casos se manejarán en la actualización completa
                                    break;
                            }
                        }
                    }

                    // 2. Actualización completa de la lista
                    List<Resena> nuevasResenas = new ArrayList<>();
                    List<String> nuevosIds = new ArrayList<>();

                    if (value != null) {
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Resena resena = doc.toObject(Resena.class);
                            if (resena != null) {
                                resena.setId(doc.getId());
                                nuevasResenas.add(resena);
                                nuevosIds.add(doc.getId());
                            }
                        }
                    }

                    adapter.updateData(nuevasResenas, nuevosIds);
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalogo_resenas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_detalles_cuenta) {
            // Navegar a ProfileActivity
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        } else if (id == R.id.action_cerrar_sesion) {
            // Cerrar sesión
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}