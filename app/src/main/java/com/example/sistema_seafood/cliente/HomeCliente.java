package com.example.sistema_seafood.cliente;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sistema_seafood.Carrito;
import com.example.sistema_seafood.Categoria;
import com.example.sistema_seafood.Cliente;
import com.example.sistema_seafood.Extra;
import com.example.sistema_seafood.Pedido;
import com.example.sistema_seafood.Platillo;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.Ubicacion;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sistema_seafood.databinding.ActivityHomeClienteBinding;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeCliente extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeClienteBinding binding;

    public static ArrayList<Extra> extras=new ArrayList<>();

    private Fragment fragment;

    private ArrayList<Platillo> platillos;
    public static Carrito carrito=new Carrito();
    private static FloatingActionButton floatingActionButton;

    public static Pedido pedido;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();

    public static Cliente cliente;

    private TextView titulo;
    private float originalX, originalY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        login();
        binding = ActivityHomeClienteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        titulo=findViewById(R.id.title);
        setSupportActionBar(binding.appBarHomeCliente.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.nav_opcion1 ,R.id.nav_opcion2 ,R.id.nav_opcion3 ,R.id.nav_opcion4 ,R.id.nav_opcion5 ,R.id.nav_perfil, R.id.nav_favoritos,R.id.nav_pedidos,R.id.nav_ayuda,R.id.nav_categoria,R.id.nav_envio, R.id.nav_platillo,R.id.nav_carrito,R.id.nav_confirmar,R.id.nav_nueva_ubicacion)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home_cliente);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        binding.appBarHomeCliente.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.nav_envio);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.nav_home){
                    setTitulo("Bienvenido");
                    navController.navigate(R.id.nav_home);
                }
                else if(item.getItemId()==R.id.nav_perfil){
                    setTitulo("Perfil");
                    navController.navigate(R.id.nav_perfil);
                }
                else if(item.getItemId()==R.id.nav_favoritos){
                    setTitulo("Favoritos");
                    navController.navigate(R.id.nav_favoritos);
                }
                else if(item.getItemId()==R.id.nav_pedidos){
                    setTitulo("Pedidos");
                    navController.navigate(R.id.nav_pedidos);
                }
                else{
                    setTitulo("Ayuda");
                    navController.navigate(R.id.nav_ayuda);
                }
                drawer.closeDrawers();
                return true;
            }
        });
        consultarUsuario();

        floatingActionButton=findViewById(R.id.fab);
        floatingActionButton.setVisibility(View.INVISIBLE);
        consultarExtras();
    }

    public static Carrito getCarrito(){
        return carrito;
    }

    public static Cliente getCliente(){
        return cliente;
    }

    public void consultarUsuario(){
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("usuarios").document("jose@gmail.com");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String correo=document.getId();
                        String nombre=document.getString("nombre");
                        GeoPoint ubicacion = document.getGeoPoint("ubicacion");
                        double latitud = ubicacion.getLatitude();
                        double longitud = ubicacion.getLongitude();
                        Ubicacion ubic=new Ubicacion(latitud,longitud);
                        String numTelefono=document.getString("numero");
                        cliente=new Cliente(nombre,numTelefono,correo,ubic,new ArrayList<>(),new ArrayList<>());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home_cliente);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void goInicio(){
        this.titulo.setText("Bienvenido");
    }
    public void setTitulo(String titulo){
        this.titulo.setText(titulo);
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
                                    Toast.makeText(getApplicationContext(),"Usario y/o contradeñas incorrectas",Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                );
    }

    public void consultarExtras(){
        FirebaseFirestore.getInstance().collection("extras")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                extras.add(new Extra(document.getString("nombre"),document.getString("descripcion"),document.getDouble("precio")));
                            }
                        } else {
                            Log.d(MotionEffect.TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
//
    public static ArrayList<Extra> getExtras(){
        return extras;
    }

    public static void setPedido(Pedido pedido){
        HomeCliente.pedido=pedido;
        floatingActionButton.setVisibility(View.VISIBLE);
    }

    public static Pedido getPedido(){
        return pedido;
    }

}



