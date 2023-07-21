package com.example.sistema_seafood.cliente;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.sistema_seafood.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AyudaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AyudaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final int REQUEST_CALL_PERMISSION = 1;

    private Button op1,op2,op3,op4,op5;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AyudaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AyudaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AyudaFragment newInstance(String param1, String param2) {
        AyudaFragment fragment = new AyudaFragment();
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
        ((HomeCliente)getActivity()).setTitulo("Ayuda");
        View view = inflater.inflate(R.layout.fragment_ayuda, container, false);

        ((Button)view.findViewById(R.id.opcion1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.nav_opcion1);
            }
        });
        ((Button)view.findViewById(R.id.opcion2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.nav_opcion2);
            }
        });

        ((Button)view.findViewById(R.id.opcion3)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.nav_opcion3);
            }
        });

        ((Button)view.findViewById(R.id.opcion4)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.nav_opcion4);
            }
        });

        ((Button)view.findViewById(R.id.opcion5)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.nav_opcion5);
            }
        });

        ((Button)view.findViewById(R.id.contactar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numeroTelefono = "9510000000";
                iniciarLlamada(numeroTelefono);
            }
        });

        return view;
    }
    private void iniciarLlamada(String numeroTelefono) {
        Intent intentLlamada = new Intent(Intent.ACTION_DIAL);
        intentLlamada.setData(Uri.parse("tel:" + numeroTelefono));
        startActivity(intentLlamada);
    }
}