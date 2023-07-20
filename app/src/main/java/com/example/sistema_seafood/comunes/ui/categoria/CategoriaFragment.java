package com.example.sistema_seafood.comunes.ui.categoria;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.example.sistema_seafood.comunes.AdaptadorCategoria;
import com.example.sistema_seafood.comunes.Categoria;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.comunes.ui.platillos.PlatilloFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CategoriaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View vista;
    private GridView contenedorCategoria;
    private ArrayList<Categoria> mesas;
    private boolean inicio=true;

    private Bitmap bitmap;
    private AdaptadorCategoria adaptadorCategoria;
    private Button btn_agregarCategoria;
    private NavController navController;

    public CategoriaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoriaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoriaFragment newInstance(String param1, String param2) {
        CategoriaFragment fragment = new CategoriaFragment();
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
        }
    }

    public void consultarCategorias(FirebaseFirestore db){
        db.collection("Categoria")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Categoria aux=new Categoria(document.getString("nombre"),document);
                                adaptadorCategoria.add(aux);
                            }
                        } else {
                            Log.d(MotionEffect.TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        inicio=false;
        vista= inflater.inflate(R.layout.fragment_categoria, container, false);

        contenedorCategoria = vista.findViewById(R.id.contenedorCategoria);
        adaptadorCategoria = new AdaptadorCategoria(getContext());
        consultarCategorias(FirebaseFirestore.getInstance());
        contenedorCategoria.setAdapter(adaptadorCategoria);

        contenedorCategoria.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putString("categoria",adaptadorCategoria.getCategoria(i).getNombre());
                PlatilloFragment platilloFragment = new PlatilloFragment();
                platilloFragment.setCategoria(adaptadorCategoria.getCategoria(i));

            }
        });

        btn_agregarCategoria = vista.findViewById(R.id.btn_agregarCategoria);
        btn_agregarCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), agregarCategoria.class);
                startActivity(i);

            }
        });
        return vista;
    }
}