package com.example.sistema_seafood.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.sistema_seafood.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class usuarioModel {
    private String correo;
    private String nombre;
    private String contrasenia;
    private String numero;
    private String rol;
    private String referenciaImagen;
    private Bitmap bitmap;

    private DocumentReference documentReference;

    public DocumentReference getDocumentReference() {
        return documentReference;
    }

    public void setDocumentReference(DocumentReference documentReference) {
        this.documentReference = documentReference;
    }


    public usuarioModel(String correo, String nombre, String contrasenia, String numero, String rol) {
        this.correo = correo;
        this.nombre = nombre;
        this.contrasenia = contrasenia;
        this.numero = numero;
        this.rol = rol;
    }

    public usuarioModel(String correo, String nombre, String contrasenia, String numero, String rol, String referenciaImagen) {
        this.referenciaImagen = referenciaImagen;
        this.nombre = nombre;
        this.numero = numero;
        this.correo = correo;
        this.contrasenia = contrasenia;
        this.rol = rol;
    }


    public String getCorreo() {
        return correo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public String getNumero() {
        return numero;
    }

    public String getRol() {
        return rol;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getReferenciaImagen() {
        return referenciaImagen;
    }

    public void setReferenciaImagen(String referenciaImagen) {
        this.referenciaImagen = referenciaImagen;
    }

    private Bitmap imagen;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    public void mostrarImagen(ImageView imageView){
        String path = referenciaImagen+".jpg";

        try {
            // Crea un archivo temporal para almacenar la imagen
            File localFile = File.createTempFile(referenciaImagen.toLowerCase(), "jpg");
            // Descarga la imagen desde Firebase Cloud Storage al archivo temporal
            storageRef.child("usuarios").child(path).getFile(localFile)
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
                            imageView.setImageResource(R.drawable.perfil);
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
