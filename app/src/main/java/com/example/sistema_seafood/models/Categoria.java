package com.example.sistema_seafood.models;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Categoria {
    private String nombre;
    private Bitmap imagen;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    private QueryDocumentSnapshot documentReference;

    public Categoria(String nombre){
        this.nombre=nombre;
    }
    /*public Categoria(String nombre,List<Platillo> platillos){
        this.nombre=nombre;
        this.platillos=platillos;
        platillosConDescuento=new ArrayList<>();
    }*/


    /*public Categoria(String nombre, QueryDocumentSnapshot documentReference){
        this.nombre=nombre;
        this.documentReference=documentReference;
        platillosConDescuento=new ArrayList<>();
    }*/

    /*public void agruparPlatDesc(){
        for (Platillo platillo:platillos){
            if(platillo.getDescuento()>0){
                platillosConDescuento.add(platillo);
            }
        }
    }*/

    public String getNombre(){
        return nombre;
    }

    /*public List<Platillo> getPlatillos(){
        if(platillos==null){
            platillos=new ArrayList<>();
            for(Map map:(ArrayList<Map>)documentReference.get("platillos")){
                platillos.add(new Platillo(map.get("nombre").toString(),map.get("descripcion").toString(),Double.parseDouble(map.get("precio").toString()),Integer.parseInt(map.get("descuento").toString()),new ArrayList<>(),Double.parseDouble(map.get("puntuacion").toString())));
            }
        }
        return platillos;
    }*/

    /*public List<Platillo> getPlatillosConDescuento(){
        return platillosConDescuento;
    }*/

    public void mostrarImagen(ImageView imageView){
        String path = nombre.toLowerCase()+".jpg";
        try {
            // Crea un archivo temporal para almacenar la imagen
            File localFile = File.createTempFile(nombre.toLowerCase(),"jpg");

            // Descarga la imagen desde Firebase Cloud Storage al archivo temporal
            storageRef.child("categorias").child(nombre.toLowerCase()).child(path).getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Carga el archivo temporal en un Bitmap
                            imagen =BitmapFactory.decodeFile(localFile.getAbsolutePath());
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
