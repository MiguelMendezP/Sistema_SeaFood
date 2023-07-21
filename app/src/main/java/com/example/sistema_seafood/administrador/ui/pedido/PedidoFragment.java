package com.example.sistema_seafood.administrador.ui.pedido;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sistema_seafood.Pedido;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.administrador.AdapterPedido;
import com.example.sistema_seafood.databinding.FragmentPedidoBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class PedidoFragment extends Fragment {

    private FragmentPedidoBinding binding;
    private RecyclerView recyclerView;
    private ArrayList<Pedido> listaPedidos;
    private AdapterPedido adapterPedido;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPedidoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.recyclerView);
        listaPedidos = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterPedido = new AdapterPedido(getActivity(), listaPedidos);
        recyclerView.setAdapter(adapterPedido);

        consultarCategorias(FirebaseFirestore.getInstance());

        return root;
    }
    public void consultarCategorias(FirebaseFirestore db) {
        db.collection("Pedidos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String productosListados = "";

                                ArrayList<HashMap<String, Object>> matrizObjetos = (ArrayList<HashMap<String, Object>>) document.get("productos");
                                for (HashMap<String, Object> objeto : matrizObjetos) {
                                    String cantidad = objeto.get("cantidad").toString();
                                    String producto = objeto.get("producto").toString();
                                    productosListados = productosListados + producto+" X"+cantidad + "\n";
                                }


                                String referencia = document.getReference().getPath();
                                Pedido pedido = new Pedido(
                                        referencia,
                                        document.getString("cliente"),
                                        document.getDate("fecha"),
                                        document.getString("direccion"),
                                        document.getString("estado"),
                                        productosListados,
                                        document.getString("repartidor"),
                                        document.getDouble("total"));
                                listaPedidos.add(pedido);
                            }
                            adapterPedido.notifyDataSetChanged();
                        } else {
                            Log.d(MotionEffect.TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}