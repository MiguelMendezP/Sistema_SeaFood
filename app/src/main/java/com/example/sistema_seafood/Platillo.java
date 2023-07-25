package com.example.sistema_seafood;

import static java.sql.Types.TIMESTAMP;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Platillo extends Producto{
    private int descuento;
    private List<Valoracion> valoraciones;
    private List<Map> vals;

    private String categoria;

    private DocumentReference documentReference;

private Bitmap imagen;

    public DocumentReference getDocumentReference() {
        return documentReference;
    }

    public void setDocumentReference(DocumentReference documentReference) {
        this.documentReference = documentReference;
    }

    private double puntuacion;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    public Platillo(String nombre, String descripcion, double precio, int descuento, List<Valoracion> valoraciones) {
        super(nombre, descripcion, precio);
        this.descuento=descuento;
        this.valoraciones=valoraciones;
    }

    public String getCategoria(){
        return categoria;
    }

    public Platillo(String nombre, String descripcion, double precio, int descuento, List<Map> valoraciones, double puntuacion, String categoria) {
        super(nombre, descripcion, precio);
        this.descuento=descuento;
        this.vals=valoraciones;
        this.puntuacion=puntuacion;
        this.categoria=categoria;
        consultarImagen(categoria);
    }

    public List<Valoracion> getValoraciones(){
        if(valoraciones==null){
            valoraciones=new ArrayList<>();
            for (Map valoracion:vals){
                Timestamp timestamp= (Timestamp) valoracion.get("fecha");
                Date fecha=timestamp.toDate();
                valoraciones.add(new Valoracion(valoracion.get("usuario").toString(),valoracion.get("comentario").toString(),Double.parseDouble(valoracion.get("puntuacion").toString()),fecha));
            }
            return valoraciones;
        }
        return valoraciones;
    }

    public double getPuntuacion(){
        return puntuacion;
    }

    @Override
    public Bitmap getImagen() {
        return imagen;
    }
    @Override
    public double getPrecio(){
        return precio - (precio*descuento/100);
    }

    public int getDescuento(){
        return descuento;
    }


    public void setDescuento(int descuento){
        this.descuento=descuento;
    }

    public void setImagen(ImageView imageView, String categoria){
        imageView.setImageBitmap(imagen);
        //this.imageView=imageView;
//        String path = nombre.toLowerCase()+".jpg";
//        try {
//            // Crea un archivo temporal para almacenar la imagen
//            File localFile = File.createTempFile(nombre.toLowerCase(),"jpg");
//
//            // Descarga la imagen desde Firebase Cloud Storage al archivo temporal
//            storageRef.child("categorias").child(categoria.toLowerCase()).child(path).getFile(localFile)
//                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                            // Carga el archivo temporal en un Bitmap
//                            imagen=BitmapFactory.decodeFile(localFile.getAbsolutePath());
//                            imageView.setImageBitmap(imagen);
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            // Manejar errores en caso de que la descarga falle
//                        }
//                    });
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void consultarImagen(String categoria){
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
                            imagen=BitmapFactory.decodeFile(localFile.getAbsolutePath());
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