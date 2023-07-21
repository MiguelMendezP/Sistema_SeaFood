package com.example.sistema_seafood.repartidor;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class PedidoRepartidor {
    private String cliente;
    private String estado;
    private Date fecha;
    private ArrayList <Map> productos;
    private GeoPoint ubicacion;

    private String direccion;



    public DocumentReference getDocumentReference() {
        return documentReference;
    }

    public void setDocumentReference(DocumentReference documentReference) {
        this.documentReference = documentReference;
    }

    private DocumentReference documentReference;

    public PedidoRepartidor(String cliente, String estado, Date fecha, ArrayList<Map> productos, GeoPoint ubicacion, DocumentReference documentReference, String direccion) {
        this.cliente = cliente;
        this.estado = estado;
        this.fecha = fecha;
        this.productos = productos;
        this.ubicacion = ubicacion;
        this.documentReference=documentReference;
        this.direccion=direccion;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
        documentReference.update("estado",estado);
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public ArrayList<Map> getProductos() {
        return productos;
    }

    public void setProductos(ArrayList<Map> productos) {
        this.productos = productos;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public GeoPoint getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(GeoPoint ubicacion) {
        this.ubicacion = ubicacion;
    }

    public void aceptar(String repartidor){
        documentReference.update("repartidor",repartidor);
    }
}
