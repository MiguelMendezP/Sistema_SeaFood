package com.example.sistema_seafood.administrador;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.sistema_seafood.MainActivity;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.Utils;
import com.example.sistema_seafood.cliente.HomeCliente;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sistema_seafood.databinding.ActivityInicioAdminBinding;
import com.google.firebase.auth.FirebaseAuth;

public class InicioAdmin extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    public static NavController navController;
    public static TextView titulo;
private ActivityInicioAdminBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getImageProfile(this);
        binding = ActivityInicioAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        titulo=findViewById(R.id.title);
        setSupportActionBar(binding.appBarInicioAdmin.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_extras, R.id.nav_categorias,R.id.nav_agregarCategoria, R.id.nav_platillos,R.id.nav_agregarPlatillo,R.id.nav_pedidos,R.id.nav_perfil,R.id.nav_repartidores,R.id.nav_ayuda)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_inicio_admin);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.nav_pedidos){
                    setTitulo("Pedidos");
                    navController.navigate(R.id.nav_pedidos);
                }
                else if(item.getItemId()==R.id.nav_perfil){
                    setTitulo("Perfil");
                    navController.navigate(R.id.nav_perfil);
                }
                else if(item.getItemId()==R.id.nav_categorias){
                    setTitulo("Categorias");
                    navController.navigate(R.id.nav_categorias);
                }
                else if(item.getItemId()==R.id.nav_platillos){
                    setTitulo("Platillos");
                    navController.navigate(R.id.nav_platillos);
                } else if (item.getItemId()==R.id.nav_extras) {
                    setTitulo("Extras");
                    navController.navigate(R.id.nav_extras);
                } else if (item.getItemId()==R.id.nav_repartidores) {
                    setTitulo("Repartidores");
                    navController.navigate(R.id.nav_repartidores);
                }else{
                    setTitulo("Ayuda");
                    navController.navigate(R.id.nav_ayuda);
                }
                drawer.closeDrawers();
                return true;
            }
        });

    }
    public static void setTitulo(String titulo){
        InicioAdmin.titulo.setText(titulo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.inicio_admin, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_inicio_admin);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}