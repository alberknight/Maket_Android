package com.example.apiconecta1;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView tvUsername, tvEmail, tvPhone, tvAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = FirebaseFirestore.getInstance();
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);

        // Obtener el UID del usuario actual
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Recuperar datos de Firestore
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        tvUsername.setText(documentSnapshot.getString("username"));
                        tvEmail.setText(documentSnapshot.getString("email"));
                        tvPhone.setText(documentSnapshot.getString("phone"));
                        tvAddress.setText(documentSnapshot.getString("address"));
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
                });
    }
}