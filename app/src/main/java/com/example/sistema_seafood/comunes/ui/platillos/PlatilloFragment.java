package com.example.sistema_seafood.comunes.ui.platillos;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import com.example.sistema_seafood.models.Platillo;
import com.example.sistema_seafood.comunes.Categoria;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.comunes.AdaptadorPlatillo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class PlatilloFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Categoria categoria;
    private String nombreCategoria;
    private View vista;
    private GridView contenedorPlatillos;
    private AdaptadorPlatillo adaptadorPlatillo;
    private Button btn_agregarPlatillo;

    public PlatilloFragment() {
        // Required empty public constructor
        //this.categoria=new Categoria("EXAMPLE",null);
    }

    public PlatilloFragment(Categoria categoria){
        this.categoria=categoria;
    }

    public void setCategoria(Categoria categoria){
        this.categoria=categoria;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment1 PlatilloFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        vista=inflater.inflate(R.layout.fragment_platillo, container, false);

        contenedorPlatillos = vista.findViewById(R.id.contenedorPlatillos);
        adaptadorPlatillo = new AdaptadorPlatillo(getContext());
        consultarPlatillos(FirebaseFirestore.getInstance());
        contenedorPlatillos.setAdapter(adaptadorPlatillo);


        btn_agregarPlatillo = vista.findViewById(R.id.btn_agregarPlatillo);
        btn_agregarPlatillo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), agregarPlatillo.class);
                startActivity(i);

            }
        });

        return vista;
    }

    public void consultarPlatillos(FirebaseFirestore db){
        db.collection("Categoria")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                ArrayList<HashMap<String, Object>> matrizObjetos = (ArrayList<HashMap<String, Object>>) document.get("platillos");
                                for (HashMap<String, Object> objeto : matrizObjetos) {
                                    String nombre = objeto.get("nombre").toString();
                                    String descripcion = objeto.get("descripcion").toString();
                                    double precio = Double.parseDouble(objeto.get("precio").toString());
                                    double descuento = Double.parseDouble(objeto.get("descuento").toString());


                                    Platillo aux=new Platillo(nombre, descripcion, precio, descuento, document.getString("nombre"));
                                    adaptadorPlatillo.add(aux);
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