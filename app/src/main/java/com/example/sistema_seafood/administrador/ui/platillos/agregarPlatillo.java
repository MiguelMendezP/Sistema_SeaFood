package com.example.sistema_seafood.administrador.ui.platillos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
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

import com.example.sistema_seafood.Categoria;
import com.example.sistema_seafood.Platillo;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.databinding.FragmentAgregarPlatilloBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class agregarPlatillo extends Fragment {

    private ImageButton btn_selectImagen;
    private Button btn_confirmCategoria;
    private Spinner spinnerCategorias;
    private EditText et_nombre, et_descripcion, et_descuento, et_pecio;
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

        View vista = inflater.inflate(R.layout.fragment_agregar_platillo, container, false);

        consultarCategorias(db);

        //Instanciar boton
        btn_selectImagen = vista.findViewById(R.id.btn_selectImagen);
        btn_confirmCategoria = vista.findViewById(R.id.btn_confirmCategoria);
        imagenSelect = vista.findViewById(R.id.imagenSelect);

        et_nombre = vista.findViewById(R.id.et_nombre);
        et_descripcion=vista.findViewById(R.id.et_descripcion);
        et_descuento= vista.findViewById(R.id.et_descuento);
        et_pecio= vista.findViewById(R.id.et_pecio);

        //instanciar Spinner
        spinnerCategorias = vista.findViewById(R.id.spinnerCategorias);


        btn_selectImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        btn_confirmCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarRegistro()){
                    String nombre = et_nombre.getText().toString();
                    String descripcion = et_descripcion.getText().toString();
                    Double descuento = Double.parseDouble(et_descuento.getText().toString());
                    Double pecio = Double.parseDouble(et_pecio.getText().toString());
                    String categoria = (String) spinnerCategorias.getSelectedItem();

                    Map<String,Object> platilloElemento = new HashMap<>();

                    platilloElemento.put("descripcion",descripcion);
                    platilloElemento.put("descuento",descuento);
                    platilloElemento.put("nombre",nombre);
                    platilloElemento.put("precio",pecio);
                    platilloElemento.put("puntuacion",0.0);
                    platilloElemento.put("valoraciones",new ArrayList<Platillo>());


                    if(subirImagen(categoria.toLowerCase())){
                        db = FirebaseFirestore.getInstance();
                        CollectionReference categoriasRef = db.collection("Categoria");
                        Query query = categoriasRef.whereEqualTo("nombre", categoria).limit(1);
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                                        String categoriaId = documentSnapshot.getId();

                                        // Paso 2: Obtén la matriz de platillos del documento de la categoría
                                        List<Map<String, Object>> platillos = (List<Map<String, Object>>) documentSnapshot.get("platillos");

                                        // Paso 3: Agrega el nuevo platillo a la matriz
                                        platillos.add(platilloElemento);

                                        // Paso 4: Actualiza la matriz de platillos en el documento de la categoría
                                        categoriasRef.document(categoriaId).update("platillos", platillos)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // El platillo se ha agregado correctamente
                                                        mostrarDialog(vista);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Ocurrió un error al agregar el platillo
                                                    }
                                                });
                                    } else {
                                        // No se encontró la categoría con el nombre especificado
                                    }
                                } else {
                                    // Ocurrió un error al obtener el documento de la categoría
                                }
                            }
                        });
                    }
                }

            }
        });
        return vista;
    }

    private boolean validarRegistro() {
        // Obtener los valores ingresados por el usuario
        String nombre = et_nombre.getText().toString();
        String descripcion = et_descripcion.getText().toString();
        String descuento = et_descuento.getText().toString();
        String pecio = et_pecio.getText().toString();



        // Verificar que los campos no estén vacíos
        if (nombre.isEmpty() || descripcion.isEmpty() || et_pecio.getText().toString().isEmpty() || et_descuento.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Double.parseDouble(et_descuento.getText().toString()) <= 0 && Double.parseDouble(et_descuento.getText().toString()) >= 100) {
            Toast.makeText(getActivity(), "El descuento no puede ser mayor a 100%", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Double.parseDouble(et_pecio.getText().toString()) <= 0) {
            Toast.makeText(getActivity(), "El preciodebe ser mayor a 0", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Si todos los campos son válidos, devolver true
        return true;
    }


    private boolean subirImagen(String categoria) {

        String imageName = et_nombre.getText().toString().toLowerCase();
        StorageReference folderRef = storage.getReference().child("categorias/"+categoria);

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

    public void consultarCategorias(FirebaseFirestore db){
        db.collection("Categoria")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> listCategorias = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Categoria aux = new Categoria(document.getString("nombre"),document);
                                listCategorias.add(aux.getNombre());
                            }
                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                                    getActivity(),
                                    android.R.layout.simple_spinner_item,
                                    listCategorias
                            );
                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerCategorias.setAdapter(spinnerAdapter);
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
        tv_agradecimiento.setText("Platillo creada");

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

