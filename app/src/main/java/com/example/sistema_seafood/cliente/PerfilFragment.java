package com.example.sistema_seafood.cliente;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sistema_seafood.Cliente;
import com.example.sistema_seafood.R;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;
    private ImageView imageView;

    private TextView correo,nombre, ubicacion,telefono, fechaNac;

    private Cliente cliente;

    public PerfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();
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
        ((HomeCliente)getActivity()).setTitulo("Perfil");
        view= inflater.inflate(R.layout.fragment_perfil, container, false);
        imageView =view.findViewById(R.id.imgPerfil);
        imageView.setImageResource(R.drawable.profile);
        cliente=(HomeCliente.getCliente());
        correo=view.findViewById(R.id.mostrarCorreo);
        nombre=view.findViewById(R.id.mostrarNombre);
        telefono=view.findViewById(R.id.muestraTelefono);
        fechaNac=view.findViewById(R.id.muestrFecha);
        ubicacion=view.findViewById(R.id.muestraDireccion);

        correo.setText(cliente.getCorreo());
        nombre.setText(cliente.getNombre());
        telefono.setText(cliente.getNumTelefono());
        fechaNac.setText(new Date().toString());
        ubicacion.setText(cliente.getUbicacion().getLatitud() + "");
        return view;
    }
}