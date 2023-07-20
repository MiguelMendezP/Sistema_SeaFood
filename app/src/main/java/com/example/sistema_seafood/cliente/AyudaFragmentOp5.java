package com.example.sistema_seafood.cliente;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.sistema_seafood.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AyudaFragmentOp5#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AyudaFragmentOp5 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AyudaFragmentOp5() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AyudaFragmentOp5.
     */
    // TODO: Rename and change types and number of parameters
    public static AyudaFragmentOp5 newInstance(String param1, String param2) {
        AyudaFragmentOp5 fragment = new AyudaFragmentOp5();
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

        View view = inflater.inflate(R.layout.fragment_ayuda_opcion2, container, false);
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