package com.example.sistema_seafood;

import java.util.ArrayList;
import java.util.List;

public class Categoria {
    private String nombre;
    private List<Platillo> platillos,platillosConDescuento;

    public Categoria(String nombre,List<Platillo> platillos){
        this.nombre=nombre;
        this.platillos=platillos;
        platillosConDescuento=new ArrayList<>();
    }

    public void agruparPlatDesc(){
        for (Platillo platillo:platillos){
            if(platillo.getDescuento()>0){
                platillosConDescuento.add(platillo);
            }
        }
    }

    public String getNombre(){
        return nombre;
    }

    public List<Platillo> getPlatillos(){
        return platillos;
    }
    public List<Platillo> getPlatillosConDescuento(){
        return platillosConDescuento;
    }
}
