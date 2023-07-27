package com.example.sistema_seafood.cliente;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sistema_seafood.Carrito;
import com.example.sistema_seafood.Categoria;
import com.example.sistema_seafood.Cliente;
import com.example.sistema_seafood.Extra;
import com.example.sistema_seafood.MainActivity;
import com.example.sistema_seafood.Notificacion.FirebaseBackgroundService;
import com.example.sistema_seafood.Pedido;
import com.example.sistema_seafood.Platillo;
import com.example.sistema_seafood.Producto;
import com.example.sistema_seafood.ProductoOrdenado;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sistema_seafood.databinding.ActivityHomeClienteBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeCliente extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeClienteBinding binding;

    public static ArrayList<Extra> extras=new ArrayList<>();
    public static Bitmap imgProfile;

    public static FirebaseFirestore firestore=FirebaseFirestore.getInstance();

    public static ImageView imageView;

    public static AdaptadorCategoria adaptadorCategoria;
    public static Carrito carrito=new Carrito();
    public static NavController navController;

    private static FloatingActionButton floatingActionButton;

    public static Pedido pedido;

    private FirebaseAuth mAuth=FirebaseAuth.getInstance();

    public static Cliente cliente;
    public static TextView titulo;
    private TextView user,email;

    public static List<Platillo> platillos=new ArrayList<>();

    public static List<Categoria> categorias=new ArrayList<>();

    public static List<Platillo> platillosFavoritos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        consultarUsuario(getIntent().getStringExtra("correo"));
        String clie = getIntent().getStringExtra("cliente");

        // Se almacena el nombre del cliente localmente en SharedPreferences si no es nulo o está vacío.
        if (clie != null && !clie.isEmpty()) {
            SharedPreferences sharedPreferences = getSharedPreferences("MiPref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("nombre_cliente", clie);
            editor.apply();
        } else {
            // Si clie es nulo o está vacío, recupera el nombre del cliente almacenado previamente.
            SharedPreferences sharedPreferencesBackground = getSharedPreferences("MiPref", Context.MODE_PRIVATE);
            clie = sharedPreferencesBackground.getString("nombre_cliente", null);
        }
        // Inicia el servicio en segundo plano solo si el nombre del cliente no es nulo o está vacío.
        if (clie != null && !clie.isEmpty()) {
            Intent serviceIntent = new Intent(this, FirebaseBackgroundService.class);
            serviceIntent.putExtra(FirebaseBackgroundService.EXTRA_CLIENTE, clie);
            startService(serviceIntent);
        }

        adaptadorCategoria=new AdaptadorCategoria(this);
        consultarCategorias();
        Utils.getImageProfile(this);
        binding = ActivityHomeClienteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        titulo=findViewById(R.id.title);
        setSupportActionBar(binding.appBarHomeCliente.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.nav_opcion1 ,R.id.nav_opcion2 ,R.id.nav_opcion3 ,R.id.nav_opcion4 ,R.id.nav_opcion5 ,R.id.nav_perfil, R.id.nav_favoritos,R.id.nav_pedidos,R.id.nav_ayuda,R.id.nav_categoria,R.id.nav_envio, R.id.nav_platillo,R.id.nav_carrito,R.id.nav_confirmar,R.id.nav_nueva_ubicacion,R.id.nav_change_pass, R.id.nav_cerrar_sesion,R.id.nav_calificar)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home_cliente);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        binding.appBarHomeCliente.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(pedido.getEstado().equals("pendiente")){
//                    mostrarPendiente();
//                }
//                else {
                    navController.navigate(R.id.nav_envio);
//                }
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
                } else if (item.getItemId()==R.id.nav_cerrar_sesion) {

                    cerrarSesion();
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build();

                    GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(HomeCliente.this, gso);
                    googleSignInClient.signOut()
                            .addOnCompleteListener(HomeCliente.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // Aquí puedes manejar el resultado del cierre de sesión de Google si es necesario.
                                    // Luego de desautorizar la cuenta de Google, procede a cerrar sesión en FirebaseAuth.
                                    FirebaseAuth.getInstance().signOut();
                                    // Ahora el usuario debería ver el panel de selección de cuenta de Google al iniciar sesión nuevamente.
                                }
                            });

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else{
                    setTitulo("Ayuda");
                    navController.navigate(R.id.nav_ayuda);
                }
                drawer.closeDrawers();
                return true;
            }
        });
        imageView=(ImageView)binding.navView.getHeaderView(0).findViewById(R.id.imgClienteMenu);
        user=(TextView) binding.navView.getHeaderView(0).findViewById(R.id.nameUser);
        email=(TextView) binding.navView.getHeaderView(0).findViewById(R.id.emailUserCliente);
        floatingActionButton=findViewById(R.id.fab);
        floatingActionButton.setVisibility(View.INVISIBLE);

        ((ImageView)findViewById(R.id.btnCarrito)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(carrito.getTotal()>0){
                    navController.navigate(R.id.nav_carrito);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Su carrito se encuentra vacío",Toast.LENGTH_SHORT).show();
                }
            }
        });
        consultarExtras();
    }


    public static Carrito getCarrito(){
        return carrito;
    }

    public static Cliente getCliente(){
        return cliente;
    }

    public void consultarUsuario(String correo){
        firestore.collection("usuarios").whereEqualTo("correo",correo)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String nombre=document.getString("nombre");
                                user.setText(nombre);
                                email.setText(correo);
                                GeoPoint ubicacion = document.getGeoPoint("ubicacion");
                                String numTelefono=document.getString("numero");
                                cliente=new Cliente(nombre,numTelefono,correo,ubicacion,(ArrayList<String>)document.get("favoritos"),new ArrayList<>(),document.getReference());
                                cliente.setDireccion(Utils.getAddressFromLatLng(getApplicationContext(),ubicacion.getLatitude(),ubicacion.getLongitude()));
                            }
                        } else {
                            Log.d(MotionEffect.TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void cerrarSesion(){
        SharedPreferences preferences = HomeCliente.this.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("estado",false);
        editor.putString("correo","");
        editor.putString("rol","");
        editor.putString("nombre","");
        editor.commit();
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
    public static void setTitulo(String titulo){
        HomeCliente.titulo.setText(titulo);
    }
    public void consultarExtras(){
        Toast.makeText(getApplicationContext(),"consulta de extras",Toast.LENGTH_SHORT).show();
        firestore.collection("extras")
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


    public static void setPedido(Pedido pedido){
        HomeCliente.pedido=pedido;
        floatingActionButton.setVisibility(View.VISIBLE);
    }

    public void consultarCategorias(){
        firestore.collection("Categoria")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Categoria aux = new Categoria(document.getString("nombre"), document);
                                adaptadorCategoria.add(aux);
                                categorias.add(aux);
                                HomeCliente.platillos.addAll(aux.getPlatillos());
                            }
                        } else {
                            Log.d(MotionEffect.TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void mostrarPendiente(){
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setMessage("Tu pedido se encuentra en estado pendiente, el restaurant aún no ha aceptado el pedido.")
                .setCancelable(true)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        AlertDialog titulo=alert.create();
        titulo.setTitle("Pendiente");
        titulo.show();
    }

    public static void consultarFavoritos(){
        platillosFavoritos=new ArrayList<>();
        for(Platillo platillo:HomeCliente.platillos){
            if(HomeCliente.cliente.getPlatillosFav().contains(platillo.getNombre())){
                HomeCliente.platillosFavoritos.add(platillo);
            }
        }
    }

    public static void volverPedir(ArrayList<Map> productos){
        List <String> producs=new ArrayList<>();
        for (Map map:productos){
            carrito.add(new ProductoOrdenado(getProducto(map.get("producto").toString()),Integer.parseInt(map.get("cantidad").toString())));
        }
    }

    public static Producto getProducto(String producto){
        for (Producto producto1:platillos){
            if (producto1.getNombre().equals(producto)){
                return producto1;
            }
        }
        for (Producto producto1:extras){
            if(producto1.getNombre().equals(producto)){
                return producto1;
            }
        }
        return null;
    }

    public static Categoria getCategoria(String categoria){
        for(Categoria categoria1:categorias){
            if(categoria1.getNombre().equals(categoria)){
                return categoria1;
            }
        }
        return null;
    }
}





