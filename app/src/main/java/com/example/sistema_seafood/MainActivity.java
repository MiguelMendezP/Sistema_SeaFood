package com.example.sistema_seafood;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String correo = et_mail.getText().toString();
                String contrasenia = et_pass.getText().toString();

                firebaseAuth.signInWithEmailAndPassword(correo, contrasenia).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            for (int i = 0; i < itemsUsuaarios.size(); i++) {
                                if (itemsUsuaarios.get(i).getCorreo().equals(correo)) {
                                    if (itemsUsuaarios.get(i).getRol().equals("cliente")) {
                                        Intent menuCliente = new Intent(MainActivity.this, HomeCliente.class);
                                        menuCliente.putExtra("correo",itemsUsuaarios.get(i).getCorreo());
                                        startActivity(menuCliente);
                                        finish();
                                        break;
                                    } else if (itemsUsuaarios.get(i).getRol().equals("repartidor")) {
                                        Intent menuRepartidor = new Intent(MainActivity.this, HomeRepartidor.class);
                                        startActivity(menuRepartidor);
                                        finish();
                                        break;

                                    } else if (itemsUsuaarios.get(i).getRol().equals("admin")) {
                                        Intent menuAdmin = new Intent(MainActivity.this, InicioAdmin.class);
                                        startActivity(menuAdmin);
                                        finish();
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
        });
        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, registrar.class);
                startActivity(i);
            }
        });
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