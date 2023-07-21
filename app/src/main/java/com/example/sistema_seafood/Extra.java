package com.example.sistema_seafood;

import android.graphics.Bitmap;
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



public class Extra extends Producto{

    private Bitmap imagen;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    public Extra(String nombre, String descripcion, double precio) {
        super(nombre, descripcion, precio);
        cargarImagen();
    }

    public void cargarImagen(){

        String path = nombre+".jpg";
        try {
            // Crea un archivo temporal para almacenar la imagen
            File localFile = File.createTempFile(nombre,"jpg");

            // Descarga la imagen desde Firebase Cloud Storage al archivo temporal
            storageRef.child("extras").child(path).getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Carga el archivo temporal en un Bitmap
                            imagen= BitmapFactory.decodeFile(localFile.getAbsolutePath());
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

    @Override
    public Bitmap getImagen() {
        return imagen;
    }

    public String toString(){
        return nombre;
    }


    private String referencia;
    public Extra(String referencia, String nombre, String descripcion, double precio) {
        super(nombre, descripcion, precio);
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}
