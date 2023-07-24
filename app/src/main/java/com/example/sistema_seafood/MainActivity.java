package com.example.sistema_seafood;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sistema_seafood.administrador.InicioAdmin;
import com.example.sistema_seafood.cliente.HomeCliente;
import com.example.sistema_seafood.models.usuarioModel;
import com.example.sistema_seafood.repartidor.HomeRepartidor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<usuarioModel> itemsUsuaarios = new ArrayList<usuarioModel>();
    Button btn_login, btn_registrar;
    EditText et_mail, et_pass;
    String nombre = "";
    String rol = "";
    String correo = "";
    String contrasenia = "";

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        et_mail = findViewById(R.id.et_mail);
        et_pass = findViewById(R.id.et_pass);

        btn_login = findViewById(R.id.btn_login);
        btn_registrar = findViewById(R.id.btn_registrar);
        getItemsUsuario();

        SharedPreferences preferences = getSharedPreferences("sesion",Context.MODE_PRIVATE);
        nombre = preferences.getString("nombre","nombre");
        correo = preferences.getString("correo","correo");
        contrasenia = preferences.getString("contrasenia","contrasenia");
        rol = preferences.getString("rol","rol");
        System.out.println(preferences.getBoolean("estado",true));

        if(preferences.getBoolean("estado",true) == true){
            firebaseAuth.signInWithEmailAndPassword(correo, contrasenia).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        if (rol.equals("cliente")) {
                            Intent menuCliente = new Intent(MainActivity.this, HomeCliente.class);
                            startActivity(menuCliente);
                        } else if (rol.equals("repartidor")) {
                            Intent menuRepartidor = new Intent(MainActivity.this, HomeRepartidor.class);
                            startActivity(menuRepartidor);
                        } else if (rol.equals("admin")) {
                            Intent menuAdmin = new Intent(MainActivity.this, InicioAdmin.class);
                            startActivity(menuAdmin);

                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Datos incorrectos", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    String correo = et_mail.getText().toString();
                    String contrasenia = et_pass.getText().toString();
                    if (correo.isEmpty() || contrasenia.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
                    }else {
                        autentificacion(correo, contrasenia);
                    }
            }
        });
        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, registrar.class);
                startActivity(i);
            }
        });
    }

    public void autentificacion(String correo, String contrasenia){

        firebaseAuth.signInWithEmailAndPassword(correo, contrasenia).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    for (int i = 0; i < itemsUsuaarios.size(); i++) {
                        if (itemsUsuaarios.get(i).getCorreo().equals(correo)) {

                            if (itemsUsuaarios.get(i).getRol().equals("cliente")) {
                                guardarSesion(itemsUsuaarios.get(i).getCorreo(), itemsUsuaarios.get(i).getContrasenia(), itemsUsuaarios.get(i).getRol(), itemsUsuaarios.get(i).getNombre());
                                Intent menuCliente = new Intent(MainActivity.this, HomeCliente.class);
                                startActivity(menuCliente);
                                break;
                            } else if (itemsUsuaarios.get(i).getRol().equals("repartidor")) {
                                guardarSesion(itemsUsuaarios.get(i).getCorreo(), itemsUsuaarios.get(i).getContrasenia(),itemsUsuaarios.get(i).getRol(), itemsUsuaarios.get(i).getNombre());
                                Intent menuRepartidor = new Intent(MainActivity.this, HomeRepartidor.class);
                                startActivity(menuRepartidor);
                                break;

                            } else if (itemsUsuaarios.get(i).getRol().equals("admin")) {

                                guardarSesion(itemsUsuaarios.get(i).getCorreo(), itemsUsuaarios.get(i).getContrasenia(),itemsUsuaarios.get(i).getRol(), itemsUsuaarios.get(i).getNombre());
                                Intent menuAdmin = new Intent(MainActivity.this, InicioAdmin.class);
                                startActivity(menuAdmin);
                                break;
                            }
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Datos incorrectos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void guardarSesion(String correo,String contrasenia, String rol, String nombre){
        SharedPreferences preferecnes = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferecnes.edit();
        editor.putBoolean("estado",true);
        editor.putString("correo",correo);
        editor.putString("contrasenia",contrasenia);
        editor.putString("rol",rol);
        editor.putString("nombre",nombre);
        editor.commit();
    }

    public void getItemsUsuario() {
        itemsUsuaarios.clear();
        db.collection("usuarios")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String correo = (String) document.getData().get("correo");
                                String nombre = (String) document.getData().get("nombre");
                                String contrasenia = (String) document.getData().get("contrasenia");
                                String numero = (String) document.getData().get("numero");
                                String rol = (String) document.getData().get("rol");
                                itemsUsuaarios.add(new usuarioModel(correo, nombre, contrasenia, numero, rol));
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

}