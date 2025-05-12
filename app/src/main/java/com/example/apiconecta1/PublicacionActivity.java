package com.example.apiconecta1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
public class PublicacionActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView ivArticulo;
    private EditText etNombreArticulo, etPrecio, etDescripcion;
    private Uri imageUri;
    private StorageReference storageRef;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicacion);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("publicaciones");
        db = FirebaseFirestore.getInstance();

        // Vincular vistas
        ivArticulo = findViewById(R.id.ivArticulo);
        etNombreArticulo = findViewById(R.id.etNombreArticulo);
        etPrecio = findViewById(R.id.etPrecio);
        etDescripcion = findViewById(R.id.etDescripcion);

        // Seleccionar imagen
        findViewById(R.id.btnSeleccionarImagen).setOnClickListener(v -> openFileChooser());

        // Publicar artículo
        findViewById(R.id.btnPublicar).setOnClickListener(v -> publicarArticulo());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(ivArticulo);
        }
    }

    private void publicarArticulo() {
        String nombre = etNombreArticulo.getText().toString().trim();
        String precio = etPrecio.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String usuarioId = mAuth.getCurrentUser().getUid();

        if (nombre.isEmpty() || precio.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Subir imagen primero
        StorageReference fileReference = storageRef.child(usuarioId + "_" + System.currentTimeMillis() + ".jpg");
        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> guardarPublicacionEnFirestore(nombre, precio, descripcion, uri.toString())))
                .addOnFailureListener(e -> Toast.makeText(PublicacionActivity.this,
                        "Error al subir imagen: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void guardarPublicacionEnFirestore(String nombre, String precio, String descripcion, String imageUrl) {
        Map<String, Object> publicacion = new HashMap<>();
        publicacion.put("nombre", nombre);
        publicacion.put("precio", Double.parseDouble(precio));
        publicacion.put("descripcion", descripcion);
        publicacion.put("imagenUrl", imageUrl);
        publicacion.put("usuarioId", mAuth.getCurrentUser().getUid());
        publicacion.put("fecha", FieldValue.serverTimestamp());

        db.collection("publicaciones").add(publicacion)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Artículo publicado con éxito", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Error al publicar: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}