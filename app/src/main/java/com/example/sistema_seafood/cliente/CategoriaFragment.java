package com.example.sistema_seafood.cliente;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.sistema_seafood.Categoria;
import com.example.sistema_seafood.Platillo;
import com.example.sistema_seafood.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoriaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoriaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static Categoria categoria;
    private View vista;
    private GridView contenedorPlatillos;
    private AdaptadorPlatillo adaptadorPlatillo;


    public CategoriaFragment() {
        // Required empty public constructor
        //this.categoria=new Categoria("EXAMPLE",null);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista=inflater.inflate(R.layout.fragment_categoria, container, false);
        contenedorPlatillos =vista.findViewById(R.id.contenedorPlatillos);
        adaptadorPlatillo=new AdaptadorPlatillo(getContext(),categoria);
        HomeCliente.setTitulo(categoria.getNombre());
        contenedorPlatillos.setAdapter(adaptadorPlatillo);
        contenedorPlatillos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PlatilloFragment.setPlatillo(adaptadorPlatillo.getPlatillo(i));
                Navigation.findNavController(view).navigate(R.id.nav_platillo);
            }
        });
        return vista;
    }
}