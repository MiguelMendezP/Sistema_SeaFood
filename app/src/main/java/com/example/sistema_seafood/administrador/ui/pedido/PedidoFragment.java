package com.example.sistema_seafood.administrador.ui.pedido;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.sistema_seafood.Pedido;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.administrador.ui.AdapterPedido2;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class PedidoFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private GridView gridView;
    private AdapterPedido2 adapterPedido;

    public static PedidoFragment newInstance(String param1, String param2) {
        PedidoFragment fragment = new PedidoFragment();
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

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_pedido, container, false);
        gridView = vista.findViewById(R.id.contenedorPedidosLista);
        adapterPedido=new AdapterPedido2(getContext());
        consultarPedidosDisponibles();
        gridView.setAdapter(adapterPedido);
        return vista;
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
                                        String productosListados = "";
                                        ArrayList<HashMap<String, Object>> matrizObjetos = (ArrayList<HashMap<String, Object>>) dc.getDocument().get("productos");
                                        for (HashMap<String, Object> objeto : matrizObjetos) {
                                            String cantidad = objeto.get("cantidad").toString();
                                            String producto = objeto.get("producto").toString();
                                            productosListados = productosListados + producto+" X"+cantidad + "\n";
                                        }

                                        String referencia = dc.getDocument().getReference().getPath();
                                        Pedido pedido = new Pedido(
                                                referencia,
                                                dc.getDocument().getString("cliente"),
                                                dc.getDocument().getDate("fecha"),
                                                dc.getDocument().getString("direccion"),
                                                dc.getDocument().getString("estado"),
                                                productosListados,
                                                dc.getDocument().getString("repartidor"),
                                                dc.getDocument().getDouble("total"));
                                        pedido.setDocumentReference(dc.getDocument().getReference());

                                        adapterPedido.add(pedido);

                                    break;
                                case MODIFIED:

                                    adapterPedido.actualizar(dc);

                                    break;
                                case REMOVED:
                            }
                        }
                    }
                });
    }
}