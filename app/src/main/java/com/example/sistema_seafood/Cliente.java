package com.example.sistema_seafood;

import com.example.sistema_seafood.models.Platillo;
import com.google.firebase.firestore.DocumentReference;

import java.util.Date;
import java.util.List;

public class Cliente {
    private String nombre,numTelefono,correo, direccion;
    private Ubicacion ubicacion;
    private List<Platillo> platillosFavoritos;

    private List<String> platillosFav;

    private List<Pedido> pedidos;

    public DocumentReference getDocumentReference() {
        return documentReference;
    }

    public void setDocumentReference(DocumentReference documentReference) {
        this.documentReference = documentReference;
    }

    private DocumentReference documentReference;

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Cliente(String nombre, String numTelefono, String correo, Ubicacion ubicacion, List<Pedido> pedidos) {
        this.nombre = nombre;
        this.numTelefono = numTelefono;
        this.correo = correo;
        this.ubicacion = ubicacion;
        this.platillosFavoritos = platillosFavoritos;
        this.pedidos=pedidos;
    }

    public Cliente(String nombre, String numTelefono, String correo, Ubicacion ubicacion, List<String> platillosFavoritos, List<Pedido> pedidos) {
        this.nombre = nombre;
        this.numTelefono = numTelefono;
        this.correo = correo;
        this.ubicacion = ubicacion;
        this.platillosFav = platillosFavoritos;
        this.pedidos=pedidos;
    }

    public List<String> getPlatillosFav() {
        return platillosFav;
    }

    public void setPlatillosFav(List<String> platillosFav) {
        this.platillosFav = platillosFav;
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

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public List<Platillo> getPlatillosFavoritos() {
        return platillosFavoritos;
    }

    public void setPlatillosFavoritos(List<Platillo> platillosFavoritos) {
        this.platillosFavoritos = platillosFavoritos;
    }

    public boolean realizarPedido(Carrito carrito, Ubicacion ubicacion){
        pedidos.add(new Pedido(this, carrito,new Date(),ubicacion,"en espera"));
        return true;
    }

    public boolean addFavorito(Platillo platillo){
        return platillosFavoritos.add(platillo);
    }

    public boolean removeFavorito(Platillo platillo){
        return platillosFavoritos.remove(platillo);
    }
}