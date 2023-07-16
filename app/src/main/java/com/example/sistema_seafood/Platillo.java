package com.example.sistema_seafood;

import java.util.List;

public class Platillo extends Producto{
    private int descuento;
    private List<Valoracion> valoraciones;

    public Platillo(String nombre, String descripcion, double precio, int descuento, List<Valoracion> valoraciones) {
        super(nombre, descripcion, precio);
        this.descuento=descuento;
        this.valoraciones=valoraciones;
    }

    @Override
    public double getPrecio(){
        return precio - (precio*descuento/100);
    }

    public int getDescuento(){
        return descuento;
    }

    public List<Valoracion> getValoraciones() {
        return valoraciones;
    }

    public void setDescuento(int descuento){
        this.descuento=descuento;
    }

    public void setValoraciones(List<Valoracion> valoraciones) {
        this.valoraciones = valoraciones;
    }
}