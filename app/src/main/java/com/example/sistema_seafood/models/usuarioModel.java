package com.example.sistema_seafood.models;

public class usuarioModel {
    String correo;
    String nombre;
    String contrasenia;
    String numero;
    String rol;

    public usuarioModel(String correo, String nombre, String contrasenia, String numero, String rol) {
        this.correo = correo;
        this.nombre = nombre;
        this.contrasenia = contrasenia;
        this.numero = numero;
        this.rol = rol;
    }

    public String getCorreo() {
        return correo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public String getNumero() {
        return numero;
    }

    public String getRol() {
        return rol;
    }
}
