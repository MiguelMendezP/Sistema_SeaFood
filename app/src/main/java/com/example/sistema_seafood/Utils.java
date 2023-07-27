package com.example.sistema_seafood;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.sistema_seafood.administrador.InicioAdmin;
import com.example.sistema_seafood.cliente.HomeCliente;
import com.example.sistema_seafood.repartidor.HomeRepartidor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class Utils {
    private static StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();
    private static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>();
    public static Punto coordenadas=new Punto();
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");


    public static String getAddressFromLatLng(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String addressStr = "";

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                // Concatenate address lines into a single string
                StringBuilder addressBuilder = new StringBuilder();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressBuilder.append(address.getAddressLine(i));
                    if (i < address.getMaxAddressLineIndex()) {
                        addressBuilder.append(", ");
                    }
                }
                addressStr = addressBuilder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addressStr;
    }

    public static void uploadImageProfile(Uri imageUri, Context context) {
        if (imageUri != null) {
            StorageReference photoRef = firebaseStorage.child("usuarios").child(firebaseAuth.getCurrentUser().getUid() + ".jpg");

            photoRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Foto subida exitosamente
                            getImageProfile(context);
                            Toast.makeText(context, "Foto de perfil actualizada", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Error al subir la foto
                            Toast.makeText(context, "Se produjo un error al subir la imagen", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public static void getImageProfile(Context context) {
        //Toast.makeText(context,firebaseAuth.getCurrentUser().getUid(),Toast.LENGTH_SHORT).show();
        StorageReference profilePhotoRef = firebaseStorage.child("usuarios").child(firebaseAuth.getCurrentUser().getUid() + ".jpg");
        System.out.println("Si"+firebaseAuth.getCurrentUser().getUid());
        // Obtener la URL de descarga del archivo
        try {
            // Crea un archivo temporal para almacenar la imagen
            File localFile = File.createTempFile(firebaseAuth.getCurrentUser().getUid(),"jpg");

            // Descarga la imagen desde Firebase Cloud Storage al archivo temporal
            profilePhotoRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Carga el archivo temporal en un Bitmap
                            if(context instanceof HomeRepartidor){
                                HomeRepartidor.bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            }
                            if(context instanceof HomeCliente){
                                HomeCliente.imgProfile = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                HomeCliente.imageView.setImageBitmap(HomeCliente.imgProfile);
                                //HomeCliente.imgProfileMenu.setImageBitmap(HomeCliente.imgProfile);
                            }
                            if(context instanceof InicioAdmin){
                                InicioAdmin.imgProfile = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                InicioAdmin.imageView.setImageBitmap(InicioAdmin.imgProfile);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    public static String getDate(Date date){
        return sdf.format(date);
    }

    public static void showRoundedAlertDialog(Context context, String title, String message) {
        // Inflar el diseño personalizado para el AlertDialog con esquinas redondeadas
        LayoutInflater inflater = LayoutInflater.from(context);
        View roundedAlertDialogView = inflater.inflate(R.layout.layout_rounded_alert_dialog, null);

        // Configurar el contenido del AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(roundedAlertDialogView);
        builder.setTitle(title);
        builder.setMessage(message);

        // Crear y mostrar el AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // Personalizar las esquinas redondeadas del AlertDialog
        // Puedes ajustar el radio según tus preferencias
        float cornerRadius = context.getResources().getDimension(R.dimen.alert_dialog_corner_radius);
        Drawable backgroundDrawable = createRoundedDrawable(context, cornerRadius);
        roundedAlertDialogView.setBackground(backgroundDrawable);
    }

    private static Drawable createRoundedDrawable(Context context, float cornerRadius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(context.getResources().getColor(android.R.color.white));
        drawable.setCornerRadius(cornerRadius);
        return drawable;
    }
    public static Bitmap getBitmapFromUri(Uri imageUri,Context context) {
        try {
            // Utiliza un ContentResolver para abrir el flujo de datos de la imagen utilizando la URI
            ContentResolver resolver = context.getContentResolver();
            InputStream inputStream = resolver.openInputStream(imageUri);

            // Decodifica el flujo de datos en un objeto Bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // Recuerda cerrar el InputStream después de utilizarlo
            inputStream.close();

            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

