package com.example.sistema_seafood;

import java.util.Date;

public class Valoracion {
    private String usuario,comentario;
    private double puntuacion;
    private Date fecha;

    public Valoracion(String usuario, String comentario, double puntuacion, Date fecha) {
        this.usuario = usuario;
        this.comentario = comentario;
        this.puntuacion = puntuacion;
        this.fecha = fecha;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public double getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(double puntuacion) {
        this.puntuacion = puntuacion;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
