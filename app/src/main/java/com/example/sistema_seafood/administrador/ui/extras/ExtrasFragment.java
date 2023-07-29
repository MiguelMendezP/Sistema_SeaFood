package com.example.sistema_seafood.administrador.ui.extras;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sistema_seafood.Extra;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.administrador.ui.AdaptadorCategoria;
import com.example.sistema_seafood.administrador.ui.AdaptadorPlatillo;
import com.example.sistema_seafood.administrador.ui.AdapterExtras;
import com.example.sistema_seafood.databinding.FragmentExtrasBinding;
import com.example.sistema_seafood.models.Platillo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtrasFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<Extra> arrayExtras;
    private AdapterExtras adapterExtras;
    private View vista;

    private ImageView imagenEditar;
    private Uri imagenUri;

    private EditText et_nombre, et_descripcion, et_pecio;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    imagenEditar.setImageURI(uri);
                    imagenUri = uri;
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_extras, container, false);

        consultarExtras(FirebaseFirestore.getInstance());
        recyclerView = vista.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterExtras = new AdapterExtras(getActivity(), arrayExtras);
        recyclerView.setAdapter(adapterExtras);

        // Establecer el Listener para los clics en los elementos del GridView
        adapterExtras.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = recyclerView.getChildAdapterPosition(view);
                Extra extras = arrayExtras.get(position);
                mostrarDialog(extras.getDocumentReference().getId(), extras.getImagen(), extras.getNombre(), extras.getDescripcion(), extras.getPrecio());
            }
        });

        Button btn_agregarExtra = vista.findViewById(R.id.btn_agregarExtra);
        btn_agregarExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Navigation.findNavController(vista).navigate(R.id.nav_agregarExtras);

            }
        });

        return vista;
    }

    public void mostrarDialog(String referencia, Bitmap imagen, String nombre, String descripcion, double precio) {
        AlertDialog.Builder alerta = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_editar_extra, null);
        alerta.setView(view);

        final AlertDialog dialog = alerta.create();
        dialog.show();

        et_nombre = view.findViewById(R.id.et_nombre);
        et_descripcion = view.findViewById(R.id.et_descripcion);
        et_pecio = view.findViewById(R.id.et_pecio);

        et_nombre.setText(nombre);
        et_descripcion.setText(descripcion);
        et_pecio.setText(String.valueOf(precio));

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
                if(validarRegistro()){
                    progressDialog.show();
                    // Paso 1: Obtener la referencia al documento que contiene la matriz
                    DocumentReference docRef = FirebaseFirestore.getInstance().collection("extras").document(referencia);

                    Map<String, Object> actualizacion = new HashMap<>();
                    actualizacion.put("descripcion", et_descripcion.getText().toString());
                    actualizacion.put("nombre", et_nombre.getText().toString());
                    actualizacion.put("precio", Double.parseDouble(et_pecio.getText().toString()));

                    docRef.update(actualizacion)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // La actualización fue exitosa

                                    if (imagenUri != null) {
                                        // Obtener la referencia a Firebase Storage donde se almacenará el archivo
                                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                                        StorageReference fileRef = storageRef.child("extras/"+ et_nombre.getText().toString() + ".jpg");

                                        // Subir el archivo a Firebase Storage
                                        fileRef.putFile(imagenUri)
                                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        // El nuevo archivo ha sido cargado exitosamente
                                                        consultarExtras(FirebaseFirestore.getInstance());
                                                        recyclerView = vista.findViewById(R.id.recyclerView);
                                                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                                        adapterExtras = new AdapterExtras(getActivity(), arrayExtras);
                                                        recyclerView.setAdapter(adapterExtras);

                                                        if (progressDialog.isShowing()) {
                                                            progressDialog.dismiss();
                                                        }

                                                        dialog.dismiss();
                                                        Toast.makeText(getActivity(), "Platillo actualizado", Toast.LENGTH_SHORT).show();

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        if (progressDialog.isShowing()) {
                                                            progressDialog.dismiss();
                                                        }
                                                        Toast.makeText(getActivity(), "A ocurrido un error con la imagen", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        // El nuevo archivo ha sido cargado exitosamente
                                        consultarExtras(FirebaseFirestore.getInstance());
                                        recyclerView = vista.findViewById(R.id.recyclerView);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                        adapterExtras = new AdapterExtras(getActivity(), arrayExtras);
                                        recyclerView.setAdapter(adapterExtras);

                                        if (progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }

                                        dialog.dismiss();
                                        Toast.makeText(getActivity(), "Platillo actualizado", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Ocurrió un error al intentar actualizar el documento
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Ocurrió un error al actualizar la matriz", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(getActivity(), "Dejaste un campo vacío", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btn_eliminar = view.findViewById(R.id.btn_eliminar);
        btn_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Eliminando extras...");

                AlertDialog.Builder alerta = new AlertDialog.Builder(getActivity());
                alerta.setMessage("¿Seguro que quieres eliminar este extra?")
                        .setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                progressDialog.show();

                                // Paso 1: Obtener la referencia al documento que contiene la matriz
                                DocumentReference categoriaRef = FirebaseFirestore.getInstance().collection("extras").document(referencia);
                                categoriaRef.delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {


                                                consultarExtras(FirebaseFirestore.getInstance());
                                                recyclerView = vista.findViewById(R.id.recyclerView);
                                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                                adapterExtras = new AdapterExtras(getActivity(), arrayExtras);
                                                recyclerView.setAdapter(adapterExtras);

                                                if (progressDialog.isShowing()) {
                                                    progressDialog.dismiss();
                                                }

                                                dialog.dismiss();
                                                Toast.makeText(getActivity(), "Extra eliminado", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Error al eliminar el documento
                                                if (progressDialog.isShowing()) {
                                                    progressDialog.dismiss();
                                                }

                                                dialog.dismiss();
                                                Toast.makeText(getActivity(), "Ocurrio un error", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog titulo = alerta.create();
                titulo.setTitle("Eliminar extra");
                titulo.show();
            }
        });
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


    public void consultarExtras(FirebaseFirestore db) {
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

                            adapterExtras.notifyDataSetChanged();
                        } else {
                            Log.d(MotionEffect.TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}