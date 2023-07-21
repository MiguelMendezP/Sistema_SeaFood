package com.example.sistema_seafood.repartidor;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.sistema_seafood.Cliente;
import com.example.sistema_seafood.Pedido;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.Repartidor;
import com.example.sistema_seafood.Ubicacion;
import com.example.sistema_seafood.repartidor.EnvioFragment;
import com.example.sistema_seafood.repartidor.HistorialFragment;
import com.example.sistema_seafood.repartidor.PerfilRepartidorFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeRepartidor extends AppCompatActivity {
private EnvioFragment envioFragment;
private HistorialFragment historialFragment;

private Pedido pedido;
private PerfilRepartidorFragment perfil;
    BottomNavigationView bottomNavigationView;
    private Repartidor repartidor;

private PedidosDisponiblesRepartidor pedidosDisponiblesRepartidor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_repartidor);
        consultarUsuario();
        envioFragment=new EnvioFragment();
        historialFragment=new HistorialFragment();
        perfil=new PerfilRepartidorFragment();
        pedidosDisponiblesRepartidor= new PedidosDisponiblesRepartidor();
        bottomNavigationView = findViewById(R.id.barraNavegacion);
        setupBottomMenu();
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
                            //bottomNavigationView.setSelectedItemId(R.id.inicio_repartidor);
                        }
                        else if(item.getItemId()==R.id.envio){
                            showFragment(envioFragment);
                           // bottomNavigationView.setSelectedItemId(R.id.envio_actual);
                        }
                        else if(item.getItemId()==R.id.page_historial){
                            showFragment(historialFragment);
                            //bottomNavigationView.setSelectedItemId(R.id.page_historial);
                        }
                        else {
                            showFragment(perfil);
                           // bottomNavigationView.setSelectedItemId(R.id.page_perfil_repartidor);
                        }
                        //bottomNavigationView.setSelectedItemId(item.getItemId());
                        return true;
                    }
                });
        //setear aquí para que el listener muestre el fragment inicial al cargarse la pantalla

    }

    public void consultarUsuario(){
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("usuarios").document("miguel@gmail.com");
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

}