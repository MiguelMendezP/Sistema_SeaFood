package com.example.sistema_seafood;

import android.graphics.BitmapFactory;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Platillo extends Producto{
    private int descuento;
    private List<Valoracion> valoraciones;

    private ImageView imageView;
    private double puntuacion;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    public Platillo(String nombre, String descripcion, double precio, int descuento, List<Valoracion> valoraciones) {
        super(nombre, descripcion, precio);
        this.descuento=descuento;
        this.valoraciones=valoraciones;
    }

    public Platillo(String nombre, String descripcion, double precio, int descuento, List<Valoracion> valoraciones, double puntuacion) {
        super(nombre, descripcion, precio);
        this.descuento=descuento;
        this.valoraciones=valoraciones;
        this.puntuacion=puntuacion;
    }

    public double getPuntuacion(){
        return puntuacion;
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

    public void setImagen(ImageView imageView, String categoria){
        //this.imageView=imageView;
        String path = nombre.toLowerCase()+".jpg";
        try {
            // Crea un archivo temporal para almacenar la imagen
            File localFile = File.createTempFile(nombre.toLowerCase(),"jpg");

            // Descarga la imagen desde Firebase Cloud Storage al archivo temporal
            storageRef.child("categorias").child(categoria.toLowerCase()).child(path).getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Carga el archivo temporal en un Bitmap
                            imageView.setImageBitmap(BitmapFactory.decodeFile(localFile.getAbsolutePath()));
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
}