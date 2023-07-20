package com.example.sistema_seafood.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.sistema_seafood.Producto;
import com.example.sistema_seafood.Valoracion;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Platillo extends Producto {
    private double descuento;
    private double puntuacion;
    private List<Valoracion> valoraciones;
    private Bitmap imagen;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    private QueryDocumentSnapshot documentReference;

    public Platillo(String nombre, String descripcion, double precio, double descuento, List<Valoracion> valoraciones, Double puntuacion) {
        super(nombre, descripcion, precio);
        this.descuento=descuento;
        this.valoraciones=valoraciones;
        this.puntuacion = puntuacion;
    }
    private String categoria;
    public Platillo(String nombre, String descripcion, double precio, double descuento, String categoria) {
        super(nombre, descripcion, precio);
        this.descuento=descuento;
        this.valoraciones=valoraciones;
        this.puntuacion = puntuacion;
        this.categoria=categoria;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public double getPrecio(){
        return precio - (precio*descuento/100);
    }

    public double getDescuento(){
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

    public void setImagen(ImageView imagenView, String categria){

    };
    public double getPuntuacion() {
        return puntuacion;
    }



    public void mostrarImagen(ImageView imageView){
        String path = nombre.toLowerCase()+".jpg";
        String pathCategoria = categoria.toLowerCase();
        try {
            // Crea un archivo temporal para almacenar la imagen
            File localFile = File.createTempFile(nombre.toLowerCase(),"jpg");

            // Descarga la imagen desde Firebase Cloud Storage al archivo temporal
            storageRef.child("categorias").child(pathCategoria.toLowerCase()).child(path).getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Carga el archivo temporal en un Bitmap
                            imagen = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            imageView.setImageBitmap(imagen);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Manejar errores en caso de que la descarga falle
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getImagen() {
        return imagen;
    }
}