package com.example.sistema_seafood;

public class Repartidor {
    private String nombre, numTelefono,correo;
    private Ubicacion ubicacion;

    public Repartidor(String nombre, String numTelefono, String correo, Ubicacion ubicacion) {
        this.nombre = nombre;
        this.numTelefono = numTelefono;
        this.correo = correo;
        this.ubicacion = ubicacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNumTelefono() {
        return numTelefono;
    }

    public void setNumTelefono(String numTelefono) {
        this.numTelefono = numTelefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public boolean tomarPedido(Pedido pedido){
        pedido.setRepartidor(this);
        return true;
    }
}
