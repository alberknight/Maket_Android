package com.example.apiconecta1;

public class Resena {
    private String id;
    private String usuarioId;
    private String nombreUsuario;
    private String lugar;
    private float puntuacion;
    private String comentario;
    private String fecha;

    public Resena() {}

    // Getters
    public String getId() {
        return id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getLugar() {
        return lugar;
    }

    public float getPuntuacion() {
        return puntuacion;
    }

    public String getComentario() {
        return comentario;
    }

    public String getFecha() {
        return fecha;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public void setPuntuacion(float puntuacion) {
        this.puntuacion = puntuacion;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}