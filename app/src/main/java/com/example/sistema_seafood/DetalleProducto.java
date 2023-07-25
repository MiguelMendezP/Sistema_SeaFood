package com.example.sistema_seafood;

import android.view.View;

public class DetalleProducto {
    private int cantidad;
    private double  precio;

    //Suma el producto a la etiqueta cantidad
    public String sumarClicked(View view) {
        cantidad++;
        precio = Multiplicar();
        return String.valueOf(cantidad);
    }

    //resta el producto a la etiqueta cantidad
    public String restarClicked(View view) {
        if (cantidad > 0){
            cantidad--;
            precio = Multiplicar();
        }
        return String.valueOf(cantidad);
    }

    //Multiplica el precio del producto
    private double Multiplicar() {
        precio = cantidad*precio;
        return precio;
    }
}
