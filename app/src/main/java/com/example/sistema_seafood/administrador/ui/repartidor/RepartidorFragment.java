package com.example.sistema_seafood.administrador.ui.repartidor;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sistema_seafood.Extra;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.Repartidor;
import com.example.sistema_seafood.Ubicacion;
import com.example.sistema_seafood.administrador.ui.AdaptadorRepartidor;
import com.example.sistema_seafood.administrador.ui.AdapterExtras;
import com.example.sistema_seafood.databinding.FragmentRepartidorBinding;
import com.example.sistema_seafood.models.usuarioModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class RepartidorFragment extends Fragment {

    private FragmentRepartidorBinding binding;
    private RecyclerView recyclerView;
    ArrayList<Repartidor> repartidores;
    private AdaptadorRepartidor adaptadorRepartidor;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRepartidorBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.recyclerView);
        repartidores = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adaptadorRepartidor = new AdaptadorRepartidor(getActivity(), repartidores);
        recyclerView.setAdapter(adaptadorRepartidor);

        consultarCategorias(FirebaseFirestore.getInstance());


        Button btn_agregarRepartidor = root.findViewById(R.id.btn_agregarRepartidor);
        btn_agregarRepartidor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Navigation.findNavController(root).navigate(R.id.nav_agregarRepartidor);

            }
        });

        return root;
    }

    public void consultarCategorias(FirebaseFirestore db) {
        db.collection("usuarios")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getString("rol").equals("repartidor")){
                                    Repartidor repartidor = new Repartidor(
                                            document.getString("referenciaImagen"),
                                            document.getString("correo"),
                                            document.getString("nombre"),
                                            document.getString("contrasenia"),
                                            document.getString("numero"),
                                            document.getString("rol"));

                                    repartidores.add(repartidor);
                                }
                            }
                            adaptadorRepartidor.notifyDataSetChanged();
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