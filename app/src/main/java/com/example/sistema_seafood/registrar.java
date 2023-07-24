package com.example.sistema_seafood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class registrar extends AppCompatActivity {

    Button btn_registrarseRegister;
    EditText et_nombreRegister, et_mailRegister, et_passRegister, et_pass2Register, et_phoneRegister;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        firebaseAuth = FirebaseAuth.getInstance();


        et_nombreRegister = findViewById(R.id.et_nombreRegister);
        et_mailRegister = findViewById(R.id.et_mailRegister);
        et_passRegister = findViewById(R.id.et_passRegister);
        et_pass2Register = findViewById(R.id.et_pass2Register);
        et_phoneRegister = findViewById(R.id.et_phoneRegister);

        btn_registrarseRegister = findViewById(R.id.btn_registrarseRegister);

        btn_registrarseRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nombre = et_nombreRegister.getText().toString();
                String correo = et_mailRegister.getText().toString();
                String contrasenia = et_passRegister.getText().toString();
                //String contrasenia2 = et_pass2Register.getText().toString();
                String numero = et_phoneRegister.getText().toString();

                firebaseAuth.createUserWithEmailAndPassword(correo, contrasenia).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Map<String,Object> item = new HashMap<>();
                            item.put("correo",correo);
                            item.put("nombre",nombre);
                            item.put("contrasenia",contrasenia);
                            item.put("numero",numero);
                            item.put("rol","cliente");
                            db.collection("usuarios").document(correo).set(item);
                            Toast.makeText(registrar.this, "Usuario creado con exito", Toast.LENGTH_SHORT).show();
                            //finish();
                        }else{
                            Toast.makeText(registrar.this, "Hubo un error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}