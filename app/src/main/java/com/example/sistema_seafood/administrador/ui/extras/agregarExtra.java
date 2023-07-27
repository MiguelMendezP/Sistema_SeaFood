package com.example.sistema_seafood.administrador.ui.extras;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.sistema_seafood.Extra;
import com.example.sistema_seafood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class agregarExtra extends Fragment {

    private ArrayList<Extra> arrayExtras;
    private ImageButton btn_selectImagen;
    private Button btn_confirmExtra;
    private EditText et_nombre, et_descripcion, et_pecio;
    private ImageView imagenSelect;
    private Uri imagenUri;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef;

    private ProgressDialog progressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    imagenSelect.setImageURI(uri);
                    imagenUri = uri;
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_agregar_extras, container, false);

        consultarExtras(db);

        //Instanciar boton
        btn_selectImagen = vista.findViewById(R.id.btn_selectImagen);
        btn_confirmExtra = vista.findViewById(R.id.btn_confirmExtra);
        imagenSelect = vista.findViewById(R.id.imagenSelect);

        et_nombre = vista.findViewById(R.id.et_nombre);
        et_descripcion=vista.findViewById(R.id.et_descripcion);
        et_pecio= vista.findViewById(R.id.et_pecio);



        btn_selectImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        btn_confirmExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = et_nombre.getText().toString();
                String descripcion = et_descripcion.getText().toString();
                String precio = et_pecio.getText().toString();
                if(validarRegistro()){
                    if(subirImagen()) {
                        db = FirebaseFirestore.getInstance();
                        Map<String, Object> extrasElem = new HashMap<>();
                        extrasElem.put("descripcion", descripcion);
                        extrasElem.put("nombre", nombre);
                        extrasElem.put("precio", Double.parseDouble(precio));

                        db.collection("extras").document().set(extrasElem);
                        mostrarDialog(vista);
                    }else{

                    }

                }else{
                }
            }
        });
        return vista;
    }

    private boolean validarRegistro() {
        // Obtener los valores ingresados por el usuario
        String nombre = et_nombre.getText().toString();
        String descripcion = et_descripcion.getText().toString();
        String pecio = et_pecio.getText().toString();



        // Verificar que los campos no estén vacíos
        if (nombre.isEmpty() || descripcion.isEmpty() || et_pecio.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Double.parseDouble(et_pecio.getText().toString()) <= 0) {
            Toast.makeText(getActivity(), "El preciodebe ser mayor a 0", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Si todos los campos son válidos, devolver true
        return true;
    }


    private boolean subirImagen() {

        String imageName = et_nombre.getText().toString();
        StorageReference folderRef = storage.getReference().child("extras/"+imageName);

        if(imagenUri != null){
            folderRef.child(imageName+".jpg").putFile(imagenUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // La imagen se subió exitosamente
                    })
                    .addOnFailureListener(exception -> {
                        // Ocurrió un error al intentar subir la imagen

                    });
        }else{
            Toast.makeText(getActivity(), "Debes seleccionar una imagen", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void consultarExtras(FirebaseFirestore db){
        arrayExtras = new ArrayList<>();
        db.collection("extras")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                    String referencia = document.getReference().getPath();
                                    Extra extra = new Extra(
                                            referencia,
                                            document.getString("nombre"),
                                            document.getString("descripcion"),
                                            document.getDouble("precio"));
                                    extra.setDocumentReference(document.getReference());
                                    arrayExtras.add(extra);

                            }
                        } else {
                            Log.d(MotionEffect.TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void mostrarDialog(View vista){
        AlertDialog.Builder alerta = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_repartidor_creado,null);
        alerta.setView(view);

        final AlertDialog dialog = alerta.create();
        dialog.show();

        TextView tv_agradecimiento = view.findViewById(R.id.tv_agradecimiento);
        tv_agradecimiento.setText("Extra creado");

        Button btn_iniciarSesion = view.findViewById(R.id.btn_iniciarSesion);
        btn_iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Navigation.findNavController(vista).navigate(R.id.nav_platillos);
            }
        });
    }
}

