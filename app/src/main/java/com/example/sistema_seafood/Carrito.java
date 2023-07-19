package com.example.sistema_seafood;

import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public double getTotal(){
        double total=0;
        for (ProductoOrdenado productoOrdenado:productosOrdenados){
            total+=productoOrdenado.getSubtotal();
        }
        return total;
    }

    public List<Map> getProductos(){
        ArrayList<Map> productos=new ArrayList<>();
        for (ProductoOrdenado productoOrdenado:productosOrdenados){
            Map map =new HashMap();
            map.put("producto",productoOrdenado.getProducto().getNombre());
            map.put("cantidad",productoOrdenado.getCantidad());
            productos.add(map);
        }
        return productos;
    }
}
