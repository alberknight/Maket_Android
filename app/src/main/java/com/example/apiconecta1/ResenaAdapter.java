package com.example.apiconecta1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ResenaAdapter extends RecyclerView.Adapter<ResenaAdapter.ResenaViewHolder> {

    private List<Resena> resenas;

    public ResenaAdapter(List<Resena> resenas) {
        this.resenas = resenas;
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
        Resena resena = resenas.get(position);

        holder.tvUsuario.setText(resena.getNombreUsuario());
        holder.tvFecha.setText(resena.getFecha());
        holder.ratingBar.setRating(resena.getPuntuacion());
        holder.tvComentario.setText(resena.getComentario());
    }

    @Override
    public int getItemCount() {
        return resenas.size();
    }

    public static class ResenaViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsuario, tvFecha, tvComentario;
        RatingBar ratingBar;

        public ResenaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsuario = itemView.findViewById(R.id.tvUsuario);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvComentario = itemView.findViewById(R.id.tvComentario);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}