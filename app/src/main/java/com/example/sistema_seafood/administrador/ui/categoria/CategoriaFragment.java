package com.example.sistema_seafood.administrador.ui.categoria;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sistema_seafood.Categoria;
import com.example.sistema_seafood.administrador.ui.AdaptadorCategoria;
import com.example.sistema_seafood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoriaFragment extends Fragment implements AdapterView.OnItemClickListener {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private View vista;
    private boolean inicio=true;
    //Adaptador
    private GridView contenedorCategoria;
    private ArrayList<Categoria> categorias;
    private AdaptadorCategoria adaptadorCategoria;
    private Button btn_agregarCategoria;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();


    public static CategoriaFragment newInstance(String param1, String param2) {
        CategoriaFragment fragment = new CategoriaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private ImageView imagenEditar;
    private Uri imagenUri;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    imagenEditar.setImageURI(uri);
                    imagenUri = uri;
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public void consultarCategorias(FirebaseFirestore db){
        categorias = new ArrayList<>();
        db.collection("Categoria")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Categoria aux = new Categoria(document.getString("nombre"),document);
                                adaptadorCategoria.add(aux);
                                categorias.add(aux);
                            }
                        } else {
                            Log.d(MotionEffect.TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        inicio=false;
        vista= inflater.inflate(R.layout.fragment_categoria_admin, container, false);

        // Inicializar el GridView y configurar el adaptador
        consultarCategorias(db);
        contenedorCategoria = vista.findViewById(R.id.contenedorCategoria);
        adaptadorCategoria = new AdaptadorCategoria(getContext());
        contenedorCategoria.setAdapter(adaptadorCategoria);

        // Establecer el Listener para los clics en los elementos del GridView
        contenedorCategoria.setOnItemClickListener(this);

        //Agregar nueva categoria
        btn_agregarCategoria = vista.findViewById(R.id.btn_agregarCategoria);
        btn_agregarCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Navigation.findNavController(vista).navigate(R.id.nav_agregarCategoria);

            }
        });

        return vista;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Categoria categoria = categorias.get(position);
        mostrarDialog(categoria.getDocumentReference().getId(),categoria.getImagen(), categoria.getNombre());
    }

    public void mostrarDialog(String referencia, Bitmap imagen, String nombre) {
        System.out.println(referencia);
        AlertDialog.Builder alerta = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_editar_categoria, null);
        alerta.setView(view);

        final AlertDialog dialog = alerta.create();
        dialog.show();

        TextView textEditar = view.findViewById(R.id.textEditar);
        textEditar.setText(nombre);

        imagenEditar = view.findViewById(R.id.imagenEditar);
        imagenEditar.setImageBitmap(imagen);


        ImageButton btn_selectImagen = view.findViewById(R.id.btn_selectImagen);
        btn_selectImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        Button btn_confirmCategoria = view.findViewById(R.id.btn_confirmCategoria);
        btn_confirmCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Subiendo Archivo...");
                progressDialog.show();


                DocumentReference docRef = FirebaseFirestore.getInstance().collection("Categoria").document(referencia);

                // Paso 3: Actualiza el documento
                docRef.update("nombre", textEditar.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // La actualización fue exitosa

                                //Eliminar imagen anterior
                                //StorageReference deleteRef = storageRef.child("categorias/"+nombre.toLowerCase()+"/"+nombre.toLowerCase()+".jpg");
                                //deleteRef.delete();
                                // Paso 1: Obtén una lista de todos los archivos en la carpeta original
                                StorageReference originalFolderRef = storage.getReference().child("categorias/"+nombre.toLowerCase());
                                StorageReference newFolderRef = storage.getReference().child("categorias/"+textEditar.getText().toString().toLowerCase());

                                originalFolderRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                    @Override
                                    public void onSuccess(ListResult listResult) {
                                        // Paso 2: Crea una nueva carpeta con el nuevo nombre
                                        newFolderRef.putBytes(new byte[0]).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                // Paso 3: Copia todos los archivos de la carpeta original a la nueva carpeta
                                                for (StorageReference item : listResult.getItems()) {
                                                    StorageReference newItemRef = newFolderRef.child(item.getName());
                                                    item.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                        @Override
                                                        public void onSuccess(byte[] bytes) {
                                                            newItemRef.putBytes(bytes);
                                                        }
                                                    });
                                                }

                                                // Paso 4: Agregar el archivo a la nueva carpeta
                                                StorageReference newImageRef = storage.getReference().child("categorias/"+textEditar.getText().toString().toLowerCase()+"/"+textEditar.getText().toString().toLowerCase()+".jpg");
                                                UploadTask uploadTask = newImageRef.putFile(imagenUri);
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

                                                        StorageReference folderDelete = storage.getReference("categorias/"+nombre.toLowerCase());
                                                        folderDelete.delete();

                                                        consultarCategorias(db);
                                                        contenedorCategoria = vista.findViewById(R.id.contenedorCategoria);
                                                        adaptadorCategoria = new AdaptadorCategoria(getContext());
                                                        contenedorCategoria.setAdapter(adaptadorCategoria);

                                                        dialog.dismiss();

                                                        if (progressDialog.isShowing()) {
                                                            progressDialog.dismiss();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Manejar errores en caso de que la actualización falle
                                Log.w("TAG", "Error al actualizar el documento", e);
                            }
                        });

            }
        });
    }


}