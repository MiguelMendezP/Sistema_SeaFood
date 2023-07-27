package com.example.sistema_seafood.administrador.ui.perfil;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.sistema_seafood.MainActivity;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.Utils;
import com.example.sistema_seafood.administrador.InicioAdmin;
import com.example.sistema_seafood.cliente.HomeCliente;
import com.example.sistema_seafood.databinding.FragmentPerfilAdminBinding;
import com.example.sistema_seafood.databinding.FragmentPerfilBinding;
import com.example.sistema_seafood.models.usuarioModel;
import com.example.sistema_seafood.repartidor.HomeRepartidor;
import com.example.sistema_seafood.repartidor.PerfilRepartidorFragment;
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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters

    private String mParam1;
    private String mParam2;

    private static final int GALLERY_REQUEST_CODE = 123;
    private static final int CAMERA_REQUEST_CODE = 456;
    private View view;
    private ImageView fotoPerfil;
    private Uri selectedImageUri;
    private TextView mostrarNombre, mostrarCorreo,mostrarNumero;
    private Button btn_cerrarSesion;
    private usuarioModel usuarioModel;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_perfil_admin, container, false);

        //login();
        usuarioModel = ((InicioAdmin)getActivity()).getUsuarioModel();
        fotoPerfil = view.findViewById(R.id.imgRepartidor);
        ((TextView) view.findViewById(R.id.mostrarNombre)).setText(usuarioModel.getNombre());
        EditText correo=view.findViewById(R.id.editEmailRepartidor);
        EditText telefono=view.findViewById(R.id.editTelefonoRepartidor);
        correo.setText(usuarioModel.getCorreo());
        telefono.setText(usuarioModel.getNumero());
        fotoPerfil.setImageBitmap(InicioAdmin.bitmap);

        ((ImageButton)view.findViewById(R.id.btnNewFoto)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
                Utils.uploadImageProfile(selectedImageUri,getContext());
            }
        });
        ((Button)view.findViewById(R.id.btn_cerrarSesion)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
                cerrarSesion();
            }
        });
        ((Button)view.findViewById(R.id.btn_actualizar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(correo.getText().toString().equals("") && telefono.getText().toString().equals("")){
                    Toast.makeText(getContext(),"Campos vacíos",Toast.LENGTH_SHORT).show();
                }
                else {
                    if(!correo.getText().toString().equals("")){
                        FirebaseAuth.getInstance().getCurrentUser().updateEmail(correo.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // El correo electrónico se actualizó exitosamente.
                                            Toast.makeText(getContext(), "Revise su bandeja de entrada y confirme el cambio", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Ocurrió un error al actualizar el correo electrónico.
                                            Toast.makeText(getContext(), "Error al actualizar el correo electrónico", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        usuarioModel.getDocumentReference().update("correo",correo.getText().toString());
                        correo.setText(correo.getText().toString());
                        correo.clearFocus();

                    }
                    if(!telefono.getText().toString().equals("")){
                        usuarioModel.getDocumentReference().update("numero",telefono.getText().toString());
                        telefono.setText(telefono.getText().toString());
                        telefono.clearFocus();

                    }
                    Toast.makeText(getContext(),"Información actualizada",Toast.LENGTH_SHORT).show();
                }

            }
        });

        ((Button)view.findViewById(R.id.btnChangePassRepartidor)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Navigation.findNavController(v).navigate(R.id.nav_perfilCambiarContrasenia);
            }
        });

        return view;
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

    public void cerrarSesionGoogle(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        googleSignInClient.signOut()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Aquí puedes manejar el resultado del cierre de sesión de Google si es necesario.
                        // Luego de desautorizar la cuenta de Google, procede a cerrar sesión en FirebaseAuth.
                        FirebaseAuth.getInstance().signOut();
                        // Ahora el usuario debería ver el panel de selección de cuenta de Google al iniciar sesión nuevamente.
                    }
                });
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
                        mostrarNumero.setText(document.getString("numero"));

                    } else {

                    }
                } else {
                    Log.d(TAG, "Error getting document: ", task.getException());
                }
            }
        });
    }
}