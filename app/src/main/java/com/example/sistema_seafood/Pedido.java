package com.example.sistema_seafood;

import com.google.firebase.firestore.DocumentReference;

import java.util.Date;

public class Pedido {
    private Cliente cliente;

    private String correoCliente;
    private String correoRepartidor;

    private DocumentReference documentReference;

    public String getCorreoCliente() {
        return correoCliente;
    }

    public void setCorreoCliente(String correoCliente) {
        this.correoCliente = correoCliente;
    }

    public String getCorreoRepartidor() {
        return correoRepartidor;
    }

    public void setCorreoRepartidor(String correoRepartidor) {
        this.correoRepartidor = correoRepartidor;
    }

    private Carrito carrito;
    private Date fecha;
    private Ubicacion ubicacion;
    private String estado;
    private Repartidor repartidor;

    public Pedido(Cliente cliente, Carrito carrito, Date fecha, Ubicacion ubicacion, String estado) {
        this.cliente=cliente;
        this.carrito = carrito;
        this.fecha = fecha;
        this.ubicacion = ubicacion;
        this.estado = estado;
    }

    public Pedido(){}

    public Pedido(String correoCliente,Date fecha,Ubicacion ubicacion,String estado,Carrito carrito,String correoRepartidor){
        this.correoCliente=correoCliente;
        this.correoRepartidor=correoRepartidor;
        this.fecha=fecha;
        this.ubicacion=ubicacion;
        this.estado=estado;
        this.carrito=carrito;

    }

    public Cliente getUsuario() {
        return cliente;
    }

    public void setUsuario(Cliente cliente) {
        this.cliente = cliente;
    }

    public Carrito getCarrito() {
        return carrito;
    }

    public void setCarrito(Carrito carrito) {
        this.carrito = carrito;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Repartidor getRepartidor() {
        return repartidor;
    }

    public void setRepartidor(Repartidor repartidor) {
        this.repartidor = repartidor;
    }

    public void setDocumentReference(DocumentReference documentReference){
        this.documentReference=documentReference;
    }

    public DocumentReference getDocumentReference(){
        return  documentReference;
    }
}
