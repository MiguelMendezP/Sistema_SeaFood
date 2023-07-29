package com.example.sistema_seafood.administrador.ui.platillos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import com.example.sistema_seafood.MainActivity;
import com.example.sistema_seafood.Valoracion;
import com.example.sistema_seafood.administrador.ui.AdaptadorCategoria;
import com.example.sistema_seafood.models.Platillo;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.administrador.ui.AdaptadorPlatillo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlatilloFragment extends Fragment implements AdapterView.OnItemClickListener {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private ArrayList<Platillo> arrayPlatillos;
    private String mParam1;
    private String mParam2;
    private Categoria categoria;
    private String nombreCategoria;
    private View vista;
    private GridView contenedorPlatillos;
    private AdaptadorPlatillo adaptadorPlatillo;
    private Button btn_agregarPlatillo;

    private ImageView imagenEditar;
    private Uri imagenUri;

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


    public void setCategoria(Categoria categoria){
        this.categoria=categoria;
    }

    public static PlatilloFragment newInstance(String param1, String param2) {
        PlatilloFragment fragment = new PlatilloFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            nombreCategoria=getArguments().getString("categoria");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista=inflater.inflate(R.layout.fragment_platillo_admin, container, false);

        // Inicializar el GridView y configurar el adaptador
        contenedorPlatillos = vista.findViewById(R.id.contenedorPlatillos);
        adaptadorPlatillo = new AdaptadorPlatillo(getContext());
        consultarPlatillos(FirebaseFirestore.getInstance());
        contenedorPlatillos.setAdapter(adaptadorPlatillo);

        // Establecer el Listener para los clics en los elementos del GridView
        contenedorPlatillos.setOnItemClickListener(this);

        btn_agregarPlatillo = vista.findViewById(R.id.btn_agregarPlatillo);
        btn_agregarPlatillo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Navigation.findNavController(vista).navigate(R.id.nav_agregarPlatillo);

            }
        });

        return vista;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Platillo platillo = arrayPlatillos.get(position);
        mostrarDialog(platillo.getDocumentReference().getId(),platillo.getImagen(), platillo.getNombre(), platillo.getDescripcion(), platillo.getDescuento(), platillo.getPrecio(),platillo.getCategoria(), platillo.getNumPlatillos());
    }

    public void mostrarDialog(String referencia, Bitmap imagen, String nombre, String descripcion,double descuento, double precio,String categoria, int numPlatiillo) {
        AlertDialog.Builder alerta = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_editar_platillo, null);
        alerta.setView(view);

        final AlertDialog dialog = alerta.create();
        dialog.show();

        TextView et_nombre = view.findViewById(R.id.et_nombre);
        TextView et_descripcion = view.findViewById(R.id.et_descripcion);
        TextView et_descuento = view.findViewById(R.id.et_descuento);
        TextView et_pecio = view.findViewById(R.id.et_pecio);
        et_nombre.setText(nombre);
        et_descripcion.setText(descripcion);
        et_descuento.setText(String.valueOf(descuento));
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

                // Paso 1: Obtener la referencia al documento que contiene la matriz
                DocumentReference docRef = FirebaseFirestore.getInstance().collection("Categoria").document(referencia);

                // Paso 2: Obtener la matriz actual del documento
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                List<Map<String, Object>> platillos = (List<Map<String, Object>>) document.get("platillos");

                                // Paso 3: Modificar el elemento específico de la matriz
                                if (platillos != null && platillos.size() > 0 && !et_nombre.getText().toString().isEmpty()
                                        && !et_descripcion.getText().toString().isEmpty()
                                        && Double.parseDouble(et_descuento.getText().toString()) >= 0
                                        && Double.parseDouble(et_pecio.getText().toString()) >= 0) {

                                    progressDialog.show();

                                    // Crear una nueva lista modificable para los platillos
                                    List<Map<String, Object>> nuevaListaPlatillos = new ArrayList<>(platillos);

                                    Map<String, Object> elementoModificar = nuevaListaPlatillos.get(numPlatiillo);
                                    // Aquí puedes modificar las propiedades del elemento específico
                                    elementoModificar.put("nombre", et_nombre.getText().toString());
                                    elementoModificar.put("descripcion", et_descripcion.getText().toString());
                                    elementoModificar.put("descuento", Double.parseDouble(et_descuento.getText().toString()));
                                    elementoModificar.put("precio", Double.parseDouble(et_pecio.getText().toString()));

                                    // Paso 4: Actualizar la matriz en el documento
                                    docRef.update("platillos", nuevaListaPlatillos)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    if(imagenUri != null){
                                                        //Agregar el archivo a la nueva carpeta
                                                        StorageReference newImageRef = storage.getReference().child("categorias/"+categoria.toLowerCase()+"/"+et_nombre.getText().toString().toLowerCase()+".jpg");
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
                                                                // El nuevo archivo ha sido cargado exitosamente
                                                                consultarPlatillos(FirebaseFirestore.getInstance());
                                                                contenedorPlatillos = vista.findViewById(R.id.contenedorPlatillos);
                                                                adaptadorPlatillo = new AdaptadorPlatillo(getContext());
                                                                contenedorPlatillos.setAdapter(adaptadorPlatillo);

                                                                if (progressDialog.isShowing()) {
                                                                    progressDialog.dismiss();
                                                                }

                                                                dialog.dismiss();
                                                                Toast.makeText(getActivity(), "Platillo actualizado", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }else{
                                                        // El nuevo archivo ha sido cargado exitosamente
                                                        consultarPlatillos(FirebaseFirestore.getInstance());
                                                        contenedorPlatillos = vista.findViewById(R.id.contenedorPlatillos);
                                                        adaptadorPlatillo = new AdaptadorPlatillo(getContext());
                                                        contenedorPlatillos.setAdapter(adaptadorPlatillo);

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
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getActivity(), "Ocurrió un error al actualizar la matriz", Toast.LENGTH_SHORT).show();
                                                    // Ocurrió un error al actualizar la matriz
                                                }
                                            });
                                } else {
                                    Toast.makeText(getActivity(), "Dejaste un campo vacío", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                            }
                        } else {
                            // Ocurrió un error al obtener el documento
                        }
                    }
                });


            }
        });

        Button btn_eliminar = view.findViewById(R.id.btn_eliminar);
        btn_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Eliminando platillo...");

                AlertDialog.Builder alerta = new AlertDialog.Builder(getActivity());
                alerta.setMessage("¿Seguro que quieres eliminar el platillo?")
                        .setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                progressDialog.show();

                                // Paso 1: Obtener la referencia al documento que contiene la matriz
                                System.out.println(categoria);
                                DocumentReference categoriaRef = FirebaseFirestore.getInstance().collection("Categoria").document(referencia);

                                // Paso 2: Obtener la matriz actual del documento
                                categoriaRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {

                                                List<Map<String, Object>> platillos = (List<Map<String, Object>>) document.get("platillos");

                                                // Paso 3: Modificar la matriz para eliminar el platillo específico
                                                if (platillos != null && platillos.size() > 0) {
                                                    // Aquí debes buscar el platillo que deseas eliminar, por ejemplo, usando su nombre
                                                    platillos.remove(numPlatiillo);
                                                    System.out.println(platillos.get(numPlatiillo).get("nombre"));

                                                    // Paso 4: Actualizar la matriz en el documento
                                                    categoriaRef.update("platillos", platillos)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            consultarPlatillos(FirebaseFirestore.getInstance());
                                                            contenedorPlatillos = vista.findViewById(R.id.contenedorPlatillos);
                                                            adaptadorPlatillo = new AdaptadorPlatillo(getContext());
                                                            contenedorPlatillos.setAdapter(adaptadorPlatillo);

                                                            if (progressDialog.isShowing()) {
                                                                progressDialog.dismiss();
                                                            }

                                                            dialog.dismiss();
                                                            Toast.makeText(getActivity(), "Platillo eliminado", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            if (progressDialog.isShowing()) {
                                                                progressDialog.dismiss();
                                                            }

                                                            dialog.dismiss();
                                                            Toast.makeText(getActivity(), "Ocurrio un error", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            } else {
                                                // El documento no existe
                                            }
                                        } else {
                                            // Ocurrió un error al obtener el documento
                                        }
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
                titulo.setTitle("Eliminar platillo");
                titulo.show();
            }
        });
    }


    public void consultarPlatillos(FirebaseFirestore db){
        arrayPlatillos = new ArrayList<>();
        db.collection("Categoria")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                ArrayList<HashMap<String, Object>> matrizObjetos = (ArrayList<HashMap<String, Object>>) document.get("platillos");
                                for (int i = 0; i < matrizObjetos.size(); i++) {
                                    HashMap<String, Object> objeto = matrizObjetos.get(i);

                                    String nombre = objeto.get("nombre").toString();
                                    String descripcion = objeto.get("descripcion").toString();
                                    double precio = Double.parseDouble(objeto.get("precio").toString());
                                    System.out.println(precio);
                                    double descuento = Double.parseDouble(objeto.get("descuento").toString());
                                    String categoria = document.getString("nombre");
                                    int numPlatillo = i;

                                    Platillo aux = new Platillo(nombre, descripcion, precio, descuento, categoria, numPlatillo, document);
                                    adaptadorPlatillo.add(aux);
                                    arrayPlatillos.add(aux);

                                }

                                //(String nombre, String descripcion, double precio, double descuento)

                            }
                        } else {
                            Log.d(MotionEffect.TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }




}