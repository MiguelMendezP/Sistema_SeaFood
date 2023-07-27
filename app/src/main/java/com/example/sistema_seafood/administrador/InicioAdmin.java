package com.example.sistema_seafood.administrador;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sistema_seafood.R;
import com.example.sistema_seafood.Utils;
import com.example.sistema_seafood.administrador.ui.perfil.CambiasPasswordAdmin;
import com.example.sistema_seafood.cliente.PerfilFragment;
import com.example.sistema_seafood.models.usuarioModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sistema_seafood.databinding.ActivityInicioAdminBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class InicioAdmin extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityInicioAdminBinding binding;
    public static NavController navController;
    public static TextView titulo;

    public static Bitmap imgProfile;
    public static ImageView imageView;

    private static FloatingActionButton floatingActionButton;
    public static FirebaseFirestore firestore=FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    public usuarioModel usuarioM;
    private TextView user,email;
    private CambiasPasswordAdmin cambiasPasswordAdmin;
    public static Bitmap bitmap;
    private PerfilFragment perfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityInicioAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarInicioAdmin.toolbar);

        consultarUsuario();
        Utils.getImageProfile(this);

        cambiasPasswordAdmin = new CambiasPasswordAdmin();

        titulo=findViewById(R.id.title);
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
                else if(item.getItemId()==R.id.nav_perfilCambiarContrasenia){
                    setTitulo("Cambiar contraseña");
                    navController.navigate(R.id.nav_perfilCambiarContrasenia);
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

        imageView =(ImageView)binding.navView.getHeaderView(0).findViewById(R.id.imgAdminMenu);
        user=(TextView) binding.navView.getHeaderView(0).findViewById(R.id.nameUser);
        email=(TextView) binding.navView.getHeaderView(0).findViewById(R.id.emailUserCliente);

        SharedPreferences preferences = InicioAdmin.this.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        String nombre = preferences.getString("correo","correo");
        String correo = preferences.getString("nombre","nombre");
        user.setText(nombre);
        email.setText(correo);

    }
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();



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

    public void consultarUsuario(){
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("usuarios").document(mAuth.getCurrentUser().getEmail());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        String correo = document.getString("correo");
                        String nombre = document.getString("nombre");
                        String contrasenia = document.getString("contrasenia");
                        String numTelefono = document.getString("numero");
                        String rol = document.getString("rol");
                        System.out.println(nombre);
                        usuarioM = new usuarioModel(correo, nombre, contrasenia, numTelefono, rol);
                        usuarioM.setDocumentReference(document.getReference());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void imageAdmin(){

    }

    public usuarioModel getUsuarioModel(){
        return usuarioM;
    }
}