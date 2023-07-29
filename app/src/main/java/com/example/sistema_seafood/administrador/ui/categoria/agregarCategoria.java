package com.example.sistema_seafood.administrador.ui.categoria;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sistema_seafood.Platillo;
import com.example.sistema_seafood.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class agregarCategoria extends Fragment {


    private ImageButton btn_selectImagen;
    private Button btn_confirmCategoria;
    private TextView et_nameCategoria;
    private ImageView imagenSelect;
    private Uri imagenUri;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef;

    private ProgressDialog progressDialog;
    private FirebaseFirestore db;

    private String nombreSeleccionado;
    private Bitmap imagenSeleccionada;

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    imagenSelect.setImageURI(uri);
                    imagenUri = uri;
                } else {
                    Toast.makeText(getActivity(), "No haz seleccionado una foto", Toast.LENGTH_SHORT).show();
                }
            });


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_agregar_categoria, container, false);


        //Instanciar boton
        btn_selectImagen = vista.findViewById(R.id.btn_selectImagen);
        btn_confirmCategoria = vista.findViewById(R.id.btn_confirmCategoria);
        imagenSelect = vista.findViewById(R.id.imagenSelect);
        et_nameCategoria = vista.findViewById(R.id.et_nameCategoria);

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
                String nombreCat = et_nameCategoria.getText().toString();
                if(!nombreCat.isEmpty() && imagenUri != null){
                    subirImagen();
                    db = FirebaseFirestore.getInstance();
                    Map<String,Object> categoriaElemento = new HashMap<>();
                    categoriaElemento.put("nombre",nombreCat);
                    categoriaElemento.put("platillos",new ArrayList<Platillo>());

                    db.collection("Categoria").document().set(categoriaElemento);
                    mostrarDialog(vista);
                }else{
                    Toast.makeText(getActivity(), "Debes darle un nombre y seleccionar una imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return vista;
    }

    private void subirImagen() {
        String imageName = et_nameCategoria.getText().toString().toLowerCase();
        StorageReference folderRef = storage.getReference().child("categorias/"+imageName);

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
        }
    }

    public void mostrarDialog(View vista){
        AlertDialog.Builder alerta = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_repartidor_creado,null);
        alerta.setView(view);

        final AlertDialog dialog = alerta.create();
        dialog.show();

        TextView tv_agradecimiento = view.findViewById(R.id.tv_agradecimiento);
        tv_agradecimiento.setText("Categoria creada");

        Button btn_iniciarSesion = view.findViewById(R.id.btn_iniciarSesion);
        btn_iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Navigation.findNavController(vista).navigate(R.id.nav_categorias);
            }
        });
    }
}