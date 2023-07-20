package com.example.sistema_seafood;

public class Extra extends Producto{
    public Extra(String nombre, String descripcion, double precio) {
        super(nombre, descripcion, precio);
    }
    private String referencia;
    public Extra(String referencia, String nombre, String descripcion, double precio) {
        super(nombre, descripcion, precio);
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}
