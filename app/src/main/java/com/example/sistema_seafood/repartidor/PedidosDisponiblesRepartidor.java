package com.example.sistema_seafood.repartidor;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Build;
import android.os.Bundle;

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
import android.widget.GridView;

import com.example.sistema_seafood.Carrito;
import com.example.sistema_seafood.Categoria;
import com.example.sistema_seafood.Pedido;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.Ubicacion;
import com.example.sistema_seafood.cliente.AdaptadorCategoria;
import com.example.sistema_seafood.cliente.CategoriaFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PedidosDisponiblesRepartidor#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PedidosDisponiblesRepartidor extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private GridView gridViewMesas;
    private AdaptadorPedidosDisponible adaptadorPedidosDisponible;
    private View view;

    public PedidosDisponiblesRepartidor() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PedidosDisponiblesRepartidor.
     */
    // TODO: Rename and change types and number of parameters
    public static PedidosDisponiblesRepartidor newInstance(String param1, String param2) {
        PedidosDisponiblesRepartidor fragment = new PedidosDisponiblesRepartidor();
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

    public void consultarPedidosDisponibles(){
        FirebaseFirestore.getInstance().collection("Pedidos")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    if(dc.getDocument().getString("estado").equals("listo")){
                                        PedidoRepartidor pedido=new PedidoRepartidor(dc.getDocument().getString("cliente"),dc.getDocument().getString("estado"),dc.getDocument().getDate("fecha"), (ArrayList<Map>) dc.getDocument().get("productos"), dc.getDocument().getGeoPoint("ubicacion"),dc.getDocument().getReference(),dc.getDocument().getString("direccion"));
                                        adaptadorPedidosDisponible.add(pedido);
                                    }
                                    break;
                                case MODIFIED:
                                    if(dc.getDocument().getString("estado").equals("listo")){
                                        PedidoRepartidor pedido=new PedidoRepartidor(dc.getDocument().getString("cliente"),dc.getDocument().getString("estado"),dc.getDocument().getDate("fecha"), (ArrayList<Map>) dc.getDocument().get("productos"), dc.getDocument().getGeoPoint("ubicacion"),dc.getDocument().getReference(),dc.getDocument().getString("direccion"));
                                        adaptadorPedidosDisponible.add(pedido);
                                    }
                                    else if(dc.getDocument().getString("estado").equals("enviado") || dc.getDocument().getString("estado").equals("entregado")){
                                        adaptadorPedidosDisponible.remove(dc.getDocument().getReference());
                                    }
                                    break;
                                case REMOVED:
                            }
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_pedidos_disponibles_repartidor, container, false);
        gridViewMesas =view.findViewById(R.id.contenedorPedidosDisponibles);
        adaptadorPedidosDisponible=new AdaptadorPedidosDisponible(getContext());
        consultarPedidosDisponibles();
        gridViewMesas.setAdapter(adaptadorPedidosDisponible);
        return view;
    }
}