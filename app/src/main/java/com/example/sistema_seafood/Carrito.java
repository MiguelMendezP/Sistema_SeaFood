package com.example.sistema_seafood;

import java.util.ArrayList;
import java.util.List;

public class Carrito {
    private List<ProductoOrdenado> productosOrdenados;

    public Carrito(){
        productosOrdenados=new ArrayList<>();
    }

    public List<ProductoOrdenado> getProductoOrdenados(){
        return productosOrdenados;
    }

    public boolean add(ProductoOrdenado productoOrdenado){
        return productosOrdenados.add(productoOrdenado);
    }

    public boolean removeProducto(ProductoOrdenado productoOrdenado){
        return productosOrdenados.remove(productoOrdenado);
    }

    private double getTotal(){
        double total=0;
        for (ProductoOrdenado productoOrdenado:productosOrdenados){
            total+=productoOrdenado.getSubtotal();
        }
        return total;
    }
}
