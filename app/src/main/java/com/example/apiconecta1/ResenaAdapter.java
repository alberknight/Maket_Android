package com.example.apiconecta1;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ResenaAdapter extends RecyclerView.Adapter<ResenaAdapter.ResenaViewHolder> {

    private List<Resena> resenas;
    private List<String> resenaIds; // Lista para guardar IDs de documentos

    public ResenaAdapter(List<Resena> resenas, List<String> resenaIds) {
        this.resenas = resenas != null ? resenas : new ArrayList<>();
        this.resenaIds = resenaIds != null ? resenaIds : new ArrayList<>();
    }

    @NonNull
    @Override
    public ResenaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_resena, parent, false);
        return new ResenaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResenaViewHolder holder, int position) {
        if (position < 0 || position >= resenas.size()) return;

        Resena resena = resenas.get(position);
        if (resena == null) return;

        try {
            holder.tvLugar.setText(resena.getLugar() != null ? resena.getLugar() : "");
            holder.tvUsuario.setText(resena.getNombreUsuario() != null ? resena.getNombreUsuario() : "");
            holder.tvFecha.setText(resena.getFecha() != null ? resena.getFecha() : "");
            holder.ratingBar.setRating(resena.getPuntuacion());
            holder.tvComentario.setText(resena.getComentario() != null ? resena.getComentario() : "");
        } catch (Exception e) {
            Log.e("ResenaAdapter", "Error al bindear datos", e);
        }
    }

    @Override
    public int getItemCount() {
        return resenas.size();
    }

    // Método para actualizar datos
    public void updateData(List<Resena> nuevasResenas, List<String> nuevosIds) {
        this.resenas.clear();
        this.resenaIds.clear();

        if (nuevasResenas != null) {
            this.resenas.addAll(nuevasResenas);
        }
        if (nuevosIds != null) {
            this.resenaIds.addAll(nuevosIds);
        }
        notifyDataSetChanged();
    }

    // Método para eliminar una reseña específica
    public void removeResena(String resenaId) {
        int index = resenaIds.indexOf(resenaId);
        if (index != -1) {
            resenas.remove(index);
            resenaIds.remove(index);
            notifyItemRemoved(index);
        }
    }

    public static class ResenaViewHolder extends RecyclerView.ViewHolder {
        TextView tvLugar, tvUsuario, tvFecha, tvComentario;
        RatingBar ratingBar;

        public ResenaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLugar = itemView.findViewById(R.id.tvLugar);
            tvUsuario = itemView.findViewById(R.id.tvUsuario);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvComentario = itemView.findViewById(R.id.tvComentario);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}