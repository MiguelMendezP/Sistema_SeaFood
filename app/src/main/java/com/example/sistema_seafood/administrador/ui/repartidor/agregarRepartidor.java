package com.example.sistema_seafood.administrador.ui.repartidor;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.sistema_seafood.R;
import com.example.sistema_seafood.databinding.FragmentAgregarCategoriaBinding;
import com.example.sistema_seafood.databinding.FragmentAgregarRepartidorBinding;
import com.example.sistema_seafood.registrar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class agregarRepartidor extends Fragment {

    private FragmentAgregarRepartidorBinding binding;
    FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText et_nombre, et_correo, et_contraseña, et_telefono;
    private Button btn_confirmCategoria;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAgregarRepartidorBinding.inflate(inflater, container, false);
        View vista = binding.getRoot();

        //Inatcniar Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        //Instanciar storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        //Instancias elementos
        et_nombre = vista.findViewById(R.id.et_nombre);
        et_correo = vista.findViewById(R.id.et_correo);
        et_contraseña = vista.findViewById(R.id.et_contraseña);
        et_telefono = vista.findViewById(R.id.et_telefono);
        btn_confirmCategoria = vista.findViewById(R.id.btn_confirmCategoria);

        btn_confirmCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre = et_nombre.getText().toString();
                String correo = et_correo.getText().toString();
                String contrasenia = et_contraseña.getText().toString();
                String numero = et_telefono.getText().toString();

                if (validarRegistro()) {
                    firebaseAuth.createUserWithEmailAndPassword(correo, contrasenia)
                            .addOnCompleteListener(createUserTask -> {
                                if (createUserTask.isSuccessful()) {
                                    // El usuario se creó exitosamente, obtén la referencia del usuario actual
                                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        String userId = firebaseUser.getUid();

                                        Map<String, Object> item = new HashMap<>();
                                        item.put("contrasenia", contrasenia);
                                        item.put("correo", correo);
                                        item.put("favoritos", new ArrayList<String>());
                                        item.put("nombre", nombre);
                                        item.put("numero", numero);
                                        item.put("rol", "repartidor");
                                        item.put("ubicacion", new GeoPoint(0.0, 0.0));
                                        item.put("referenciaImagen", userId);

                                        db.collection("usuarios").document(correo).set(item);

                                        mostrarDialog(vista);

                                    }
                                } else {
                                    // Si ocurre un error durante la creación del usuario, puedes obtener más información del error:
                                    Exception exception = createUserTask.getException();
                                    // Manejar el error aquí.
                                }
                            });
                }
            }
        });
        return vista;
    }




    public void mostrarDialog(View vista){
        AlertDialog.Builder alerta = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_repartidor_creado,null);
        alerta.setView(view);

        final AlertDialog dialog = alerta.create();
        dialog.show();

        TextView tv_agradecimiento = view.findViewById(R.id.tv_agradecimiento);
        tv_agradecimiento.setText("Repartidor " + et_nombre.getText().toString());

        Button btn_iniciarSesion = view.findViewById(R.id.btn_iniciarSesion);
        btn_iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Navigation.findNavController(vista).navigate(R.id.nav_repartidores);
            }
        });
    }

    private boolean validarRegistro() {
        // Obtener los valores ingresados por el usuario
        String nombre = et_nombre.getText().toString().trim();
        String correo = et_correo.getText().toString().trim();
        String contrasena = et_contraseña.getText().toString().trim();
        String telefono = et_telefono.getText().toString().trim();

        // Verificar que los campos no estén vacíos
        if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(getActivity(), "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar que el correo sea válido (puedes agregar una validación más estricta aquí)
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(getActivity(), "Correo electrónico no válido.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar que las contraseñas sea mayor de 6 digitos
        if (contrasena.length() <= 6) {
            Toast.makeText(getActivity(), "Las contraseñas es menor a 6 digitos.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Si todos los campos son válidos, devolver true
        return true;
    }

}
