package com.example.sistema_seafood.repartidor;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.sistema_seafood.Pedido;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.repartidor.EnvioFragment;
import com.example.sistema_seafood.repartidor.HistorialFragment;
import com.example.sistema_seafood.repartidor.PerfilRepartidorFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeRepartidor extends AppCompatActivity {
private EnvioFragment envioFragment;
private HistorialFragment historialFragment;

private Pedido pedido;
private PerfilRepartidorFragment perfil;
    BottomNavigationView bottomNavigationView;
private PedidosDisponiblesRepartidor pedidosDisponiblesRepartidor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_repartidor);
        envioFragment=new EnvioFragment();
        historialFragment=new HistorialFragment();
        perfil=new PerfilRepartidorFragment();
        pedidosDisponiblesRepartidor= new PedidosDisponiblesRepartidor();
        bottomNavigationView = findViewById(R.id.barraNavegacion);
        setupBottomMenu();
    }

    @SuppressLint("NonConstantResourceId")
    public void setupBottomMenu() {

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.page_historial){
                    showFragment(historialFragment);
                }
                else if(item.getItemId()==R.id.inicio_repartidor){
                    showFragment(pedidosDisponiblesRepartidor);
                }
                else if(item.getItemId()==R.id.envio_actual){
                    showFragment(envioFragment);
                }
                else {
                    showFragment(perfil);
                }
                return false;
            }
        });
        //setear aquí para que el listener muestre el fragment inicial al cargarse la pantalla
        bottomNavigationView.setSelectedItemId(R.id.inicio_repartidor);
    }

    public void showFragment(Fragment frg) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedorFragmentRepartidor, frg)
                .commit();
    }

    public void showEnvio(PedidoRepartidor pedidoRepartidor){
       envioFragment.setPedidoRepartidor(pedidoRepartidor);
       bottomNavigationView.setSelectedItemId(R.id.envio_actual);
    }




}