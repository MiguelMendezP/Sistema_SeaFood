package com.example.sistema_seafood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
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
                String numero = et_phoneRegister.getText().toString();

                if (validarRegistro()) {
                    firebaseAuth.createUserWithEmailAndPassword(correo, contrasenia).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Map<String,Object> item = new HashMap<>();
                                item.put("contrasenia",contrasenia);
                                item.put("correo",correo);
                                item.put("favoritos",new ArrayList<String>());
                                item.put("nombre",nombre);
                                item.put("numero",numero);
                                item.put("rol","cliente");
                                item.put("ubicacion",new GeoPoint(0.0,0.0));

                                db.collection("usuarios").document(correo).set(item);

                                mostrarDialog();
                                //finish();
                            }else{
                                Toast.makeText(registrar.this, "Hubo un error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
    }

    public void mostrarDialog(){
        AlertDialog.Builder alerta = new AlertDialog.Builder(registrar.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_cuenta_creada,null);
        alerta.setView(view);

        final AlertDialog dialog = alerta.create();
        dialog.show();

        TextView tv_agradecimiento = view.findViewById(R.id.tv_agradecimiento);
        tv_agradecimiento.setText("Bienvenido "+et_nombreRegister.getText().toString());

        Button btn_iniciarSesion = view.findViewById(R.id.btn_iniciarSesion);
        btn_iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });
    }

    private boolean validarRegistro() {
        // Obtener los valores ingresados por el usuario
        String nombre = et_nombreRegister.getText().toString().trim();
        String correo = et_mailRegister.getText().toString().trim();
        String contrasena = et_passRegister.getText().toString().trim();
        String confirmarContrasena = et_pass2Register.getText().toString().trim();
        String telefono = et_phoneRegister.getText().toString().trim();

        // Verificar que los campos no estén vacíos
        if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty() || confirmarContrasena.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar que el correo sea válido (puedes agregar una validación más estricta aquí)
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Correo electrónico no válido.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar que las contraseñas sea mayor de 6 digitos
        if (contrasena.length() <= 6) {
            Toast.makeText(this, "Las contraseñas es mejor a 6 digitos.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar que las contraseñas coincidan
        if (!contrasena.equals(confirmarContrasena)) {
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Si todos los campos son válidos, devolver true
        return true;
    }
}