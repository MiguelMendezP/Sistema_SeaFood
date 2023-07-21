package com.example.sistema_seafood.repartidor;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.sistema_seafood.Cliente;
import com.example.sistema_seafood.Pedido;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.Repartidor;
import com.example.sistema_seafood.Ubicacion;
import com.example.sistema_seafood.repartidor.EnvioFragment;
import com.example.sistema_seafood.repartidor.HistorialFragment;
import com.example.sistema_seafood.repartidor.PerfilRepartidorFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeRepartidor extends AppCompatActivity {
private EnvioFragment envioFragment;
private HistorialFragment historialFragment;

private Pedido pedido;
private PerfilRepartidorFragment perfil;
public static Bitmap bitmap;
    BottomNavigationView bottomNavigationView;
    private Repartidor repartidor;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();

    private StorageReference storageRef= FirebaseStorage.getInstance().getReference();

private PedidosDisponiblesRepartidor pedidosDisponiblesRepartidor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        consultarUsuario();
        login();
        setContentView(R.layout.activity_home_repartidor);

        envioFragment=new EnvioFragment();
        historialFragment=new HistorialFragment();
        perfil=new PerfilRepartidorFragment();
        pedidosDisponiblesRepartidor= new PedidosDisponiblesRepartidor();
        bottomNavigationView = findViewById(R.id.barraNavegacion);
        setupBottomMenu();
        loadProfilePhotoUrl();
    }

    @SuppressLint("NonConstantResourceId")
    public void setupBottomMenu() {
        bottomNavigationView.setSelectedItemId(R.id.inicio_repartidor);
        showFragment(pedidosDisponiblesRepartidor);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        // Aquí puedes manejar los clics en los elementos del menú

                        if(item.getItemId()==R.id.inicio_repartidor){
                            showFragment(pedidosDisponiblesRepartidor);
                        }
                        else if(item.getItemId()==R.id.envio){
                            showFragment(envioFragment);
                        }
                        else if(item.getItemId()==R.id.page_historial){
                            showFragment(historialFragment);
                        }
                        else {
                            showFragment(perfil);
                        }
                        return true;
                    }
                });
        //setear aquí para que el listener muestre el fragment inicial al cargarse la pantalla

    }

    public void consultarUsuario(){
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("usuarios").document("jose@gmail.com");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String correo=document.getString("correo");
                        String nombre=document.getString("nombre");
                        GeoPoint ubicacion = document.getGeoPoint("ubicacion");
                        double latitud = ubicacion.getLatitude();
                        double longitud = ubicacion.getLongitude();
                        Ubicacion ubic=new Ubicacion(latitud,longitud);
                        String numTelefono=document.getString("numero");
                        repartidor=new Repartidor(nombre,numTelefono,correo,ubic);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void showFragment(Fragment frg) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedorFragmentRepartidor, frg)
                .commit();
    }

    public void login(){
        mAuth.signInWithEmailAndPassword("dario@gmail.com", "jose16")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                } else {
                                    // If sign in fails, display a message to the user.
                                  //  Toast.makeText(this,"Usario y/o contradeñas incorrectas",Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                );
    }

    public void showEnvio(PedidoRepartidor pedidoRepartidor){
       envioFragment.setPedidoRepartidor(pedidoRepartidor);
       bottomNavigationView.setSelectedItemId(R.id.envio);
    }





    public void showInicio(){
        bottomNavigationView.setSelectedItemId(R.id.inicio_repartidor);
    }

    public void showPedidos(){
        bottomNavigationView.setSelectedItemId(R.id.page_historial);
    }

    public Repartidor getRepartidor(){
        return repartidor;
    }

    private void loadProfilePhotoUrl() {
        StorageReference profilePhotoRef = storageRef.child("usuarios").child(mAuth.getCurrentUser().getUid()+".jpg");

        // Obtener la URL de descarga del archivo
        profilePhotoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Aquí tienes la URL de descarga del archivo
                String profilePhotoUrl = uri.toString();

                // Cargar la imagen desde la URL de descarga utilizando Picasso
                Picasso.get()
                        .load(profilePhotoUrl)
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                // Aquí tienes el bitmap descargado
                                HomeRepartidor.bitmap=bitmap;
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                // Manejar el error si la descarga falla
                                Log.e("ProfileActivity", "Error al cargar la imagen: " + e.getMessage());
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                                // Aquí puedes realizar alguna acción mientras se carga la imagen
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Manejar el error si no se puede obtener la URL de descarga
                Log.e("ProfileActivity", "Error al obtener la URL de descarga: " + e.getMessage());
            }
        });
    }
}