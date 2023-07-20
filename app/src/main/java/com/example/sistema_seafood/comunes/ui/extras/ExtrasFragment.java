package com.example.sistema_seafood.comunes.ui.extras;

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

import com.example.sistema_seafood.Extra;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.comunes.AdapterExtras;
import com.example.sistema_seafood.databinding.FragmentExtrasBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ExtrasFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<Extra> listaExtras;
    private AdapterExtras adapterExtras;

    private FragmentExtrasBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentExtrasBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.recyclerView);
        listaExtras = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterExtras = new AdapterExtras(getActivity(), listaExtras);
        recyclerView.setAdapter(adapterExtras);

        consultarCategorias(FirebaseFirestore.getInstance());

        return root;
    }

    public void consultarCategorias(FirebaseFirestore db) {
        db.collection("extras")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String referencia = document.getReference().getPath();
                                Extra extra = new Extra(
                                        referencia,
                                        document.getString("nombre"),
                                        document.getString("descripcion"),
                                        document.getDouble("precio"));

                                listaExtras.add(extra);
                            }
                            adapterExtras.notifyDataSetChanged();
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