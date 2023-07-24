package com.example.sistema_seafood;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class Repartidor {
    private String nombre, numTelefono,correo;
    private Ubicacion ubicacion;

    public Repartidor(String nombre, String numTelefono, String correo, Ubicacion ubicacion) {
        this.nombre = nombre;
        this.numTelefono = numTelefono;
        this.correo = correo;
        this.ubicacion = ubicacion;
    }

    private String referenciaImagen;
    private String contrasenia;
    private String rol;
    public Repartidor(String referenciaImagen, String correo, String nombre, String contrasenia, String numero, String rol) {
        this.referenciaImagen = referenciaImagen;
        this.nombre = nombre;
        this.numTelefono = numTelefono;
        this.correo = correo;
        this.contrasenia = contrasenia;
        this.rol = rol;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public String getRol() {
        return rol;
    }

    public String getReferenciaImagen() {
        return referenciaImagen;
    }

    public void setReferenciaImagen(String referenciaImagen) {
        this.referenciaImagen = referenciaImagen;
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

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public boolean tomarPedido(Pedido pedido){
        pedido.setRepartidor(this);
        return true;
    }

    private Bitmap imagen;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    private QueryDocumentSnapshot documentReference;

    public void mostrarImagen(ImageView imageView){
        String path = referenciaImagen+".jpg";
        System.out.println(path);

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
