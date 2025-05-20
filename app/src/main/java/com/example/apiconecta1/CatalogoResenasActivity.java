package com.example.apiconecta1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class CatalogoResenasActivity extends AppCompatActivity {

    private RecyclerView rvResenas;
    private ResenaAdapter adapter;
    private FloatingActionButton fabNuevaResena;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo_resenas);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Configurar Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configurar RecyclerView
        rvResenas = findViewById(R.id.rvResenas);
        rvResenas.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ResenaAdapter(new ArrayList<>(), new ArrayList<>());
        rvResenas.setAdapter(adapter);

        // Botón para nueva reseña
        fabNuevaResena = findViewById(R.id.fabNuevaResena);
        fabNuevaResena.setOnClickListener(v -> {
            // Verificar si el correo está verificado antes de permitir nueva reseña
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null && user.isEmailVerified()) {
                startActivity(new Intent(this, NuevaResenaActivity.class));
            } else {
                Toast.makeText(this, "Debes verificar tu correo primero", Toast.LENGTH_SHORT).show();
            }
        });

        // Verificar estado de autenticación
        verifyUserAccess();
        cargarResenas();
    }

    private void verifyUserAccess() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || !user.isEmailVerified()) {
            // Si no está autenticado o no verificó correo, regresar al login
            Toast.makeText(this, "Debes verificar tu correo electrónico primero", Toast.LENGTH_LONG).show();
            mAuth.signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void cargarResenas() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || !user.isEmailVerified()) return;

        String currentUserId = user.getUid();

        db.collection("resenas")
                .whereNotEqualTo("usuarioId", currentUserId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Snackbar.make(rvResenas, R.string.error_cargar_resenas, Snackbar.LENGTH_LONG)
                                .setAction(R.string.reintentar, v -> cargarResenas())
                                .show();
                        return;
                    }

                    // Procesar cambios
                    List<Resena> nuevasResenas = new ArrayList<>();
                    List<String> nuevosIds = new ArrayList<>();

                    if (value != null) {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.REMOVED) {
                                adapter.removeResena(dc.getDocument().getId());
                            }
                        }

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
    protected void onResume() {
        super.onResume();
        verifyUserAccess();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalogo_resenas, menu);

        // Opcional: Ocultar opciones si no está verificado
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || !user.isEmailVerified()) {
            menu.findItem(R.id.action_detalles_cuenta).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_detalles_cuenta) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        } else if (id == R.id.action_cerrar_sesion) {
            mAuth.signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}