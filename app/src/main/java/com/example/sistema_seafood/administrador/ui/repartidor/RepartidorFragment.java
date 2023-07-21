package com.example.sistema_seafood.administrador.ui.repartidor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sistema_seafood.databinding.FragmentRepartidorBinding;


public class RepartidorFragment extends Fragment {

    private FragmentRepartidorBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRepartidorBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}