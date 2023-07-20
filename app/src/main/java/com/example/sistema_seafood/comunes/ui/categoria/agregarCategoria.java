package com.example.sistema_seafood.comunes.ui.categoria;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sistema_seafood.R;
import com.example.sistema_seafood.databinding.FragmentAgregarCategoriaBinding;
import com.example.sistema_seafood.databinding.FragmentRepartidorBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class agregarCategoria extends Fragment {

    private FragmentAgregarCategoriaBinding binding;

    private ImageButton btn_selectImagen;
    private Button btn_confirmCategoria;
    private TextView et_nameCategoria;
    private ImageView imagenSelect;
    private Uri imagenUri;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    private ProgressDialog progressDialog;
    private FirebaseFirestore db;


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

        binding = FragmentAgregarCategoriaBinding.inflate(inflater, container, false);
        View vista = binding.getRoot();

        //Instanciar storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

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
                subirImagen();

            }
        });
        return vista;
    }

    private void guardarEspecialidad() {
        /*db = FirebaseFirestore.getInstance();
        Map<String,Object> item = new HashMap<>();
        item.put("correo",correo);
        item.put("nombre",nombre);
        item.put("contrasenia",contrasenia);
        item.put("numero",numero);
        item.put("rol","cliente");


        db.collection("usuarios").document(correo).set(item);*/
    }

    private void subirImagen() {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Subiendo Archivo...");
        progressDialog.show();

        String imageName = et_nameCategoria.getText().toString();
        StorageReference riversRef = storageRef.child("categorias/especialidades/"+imageName+".jpg");

        UploadTask uploadTask = riversRef.putFile(imagenUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(getActivity(), "A ocurrido un error con la imagen", Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                guardarEspecialidad();
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(getActivity(), "Categoria creada", Toast.LENGTH_SHORT).show();

            }
        });
    }

}