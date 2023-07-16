package com.example.sistema_seafood;

public abstract class Producto {
    protected String nombre,descripcion;
    protected double precio;

    public Producto(String nombre, String descripcion, double precio){
        this.nombre=nombre;
        this.precio=precio;
        this.descripcion=descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}
