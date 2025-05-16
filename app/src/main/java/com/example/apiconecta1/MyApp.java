package com.example.apiconecta1;
// MyApp.java
import android.app.Application;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false) // Desactiva caché
                .build();
        db.setFirestoreSettings(settings);
    }
}