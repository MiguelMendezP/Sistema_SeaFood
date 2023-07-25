package com.example.sistema_seafood.administrador.ui.perfil;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sistema_seafood.MainActivity;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.cliente.HomeCliente;
import com.example.sistema_seafood.databinding.FragmentPerfilAdminBinding;
import com.example.sistema_seafood.databinding.FragmentPerfilBinding;
import com.example.sistema_seafood.models.usuarioModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PerfilFragment extends Fragment {

    private FragmentPerfilAdminBinding binding;
    TextView mostrarNombre, mostrarCorreo, muestrarContraseña,mostrarNumero;
    Button btn_cerrarSesion;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPerfilAdminBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mostrarCorreo = root.findViewById(R.id.mostrarCorreo);
        mostrarNombre = root.findViewById(R.id.mostrarNombre);
        muestrarContraseña = root.findViewById(R.id.muestrarContraseña);
        mostrarNumero = root.findViewById(R.id.mostrarNumero);

        SharedPreferences preferences = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        String correo = preferences.getString("correo","correo");
        setDatos(correo);

        btn_cerrarSesion = root.findViewById(R.id.btn_cerrarSesion);
        btn_cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alerta = new AlertDialog.Builder(getActivity());
                alerta.setMessage("¿Deseas cerrar sesion?")
                        .setCancelable(false)
                                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        cerrarSesion();
                                        FirebaseAuth.getInstance().signOut();
                                        Intent menuCliente = new Intent(getActivity(), MainActivity.class);
                                        startActivity(menuCliente);
                                    }
                                })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog titulo = alerta.create();
                titulo.setTitle("Cerrar sesion");
                titulo.show();
            }
        });

        return root;
    }

    public void cerrarSesion(){
        SharedPreferences preferences = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("estado",false);
        editor.putString("correo","");
        editor.putString("rol","");
        editor.putString("nombre","");
        editor.commit();
    }

    public void setDatos(String idUsuario) {
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("usuarios").document(idUsuario);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        mostrarNombre.setText(document.getString("nombre"));
                        mostrarCorreo.setText(document.getString("correo"));
                        muestrarContraseña.setText(document.getString("contrasenia"));
                        mostrarNumero.setText(document.getString("numero"));

                    } else {

                    }
                } else {
                    Log.d(TAG, "Error getting document: ", task.getException());
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