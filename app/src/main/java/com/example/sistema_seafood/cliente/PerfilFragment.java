package com.example.sistema_seafood.cliente;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sistema_seafood.Cliente;
import com.example.sistema_seafood.MainActivity;
import com.example.sistema_seafood.ObtenerUbicacionFragment;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.Utils;
import com.example.sistema_seafood.repartidor.HomeRepartidor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
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
    private Uri selectedImageUri;

    private View view;

    public static String nuevaDireccion;
    private ImageView imageView;

private EditText correo, telefono,pasword1, pasword2;
TextView nombre,direccion;
    private Cliente cliente;


    public PerfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();
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
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                HomeCliente.navController.popBackStack(R.id.nav_home,false);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((HomeCliente)getActivity()).setTitulo("Perfil");
        view= inflater.inflate(R.layout.fragment_perfil, container, false);
        imageView =view.findViewById(R.id.imgPerfil);
        imageView.setImageBitmap(HomeCliente.imgProfile);
        cliente=(HomeCliente.getCliente());
        nombre=view.findViewById(R.id.mostrarNombre);
        correo=view.findViewById(R.id.editEmail);
        telefono=view.findViewById(R.id.editNumero);
        direccion=view.findViewById(R.id.textViewDireccion);
       // correo.setText(cliente.getCorreo());
        correo.setHint(cliente.getCorreo());
        nombre.setText(cliente.getNombre());
        //telefono.setText(cliente.getNumTelefono());
        telefono.setHint(cliente.getNumTelefono());
        if(nuevaDireccion==null){
            direccion.setText(cliente.getDireccion());
        }
        else {
            direccion.setText(nuevaDireccion);
        }
        ((ImageButton)view.findViewById(R.id.btnCargarImagen)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });

        ((Button)view.findViewById(R.id.btnChangePass)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.nav_change_pass);
            }
        });
        ((Button)view.findViewById(R.id.btnActualizarPerfil)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(correo.getText().toString().equals("")&&telefono.getText().toString().equals("") && direccion.getText().toString().equals(cliente.getDireccion())){
                   Toast.makeText(getContext(),"No se ha modificado algún campo",Toast.LENGTH_SHORT).show();
               }
               else{
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
                       cliente.getDocumentReference().update("correo",correo.getText().toString());
                       correo.setHint(correo.getText().toString());
                       correo.setText("");
                       correo.clearFocus();

                   }
                   if(!telefono.getText().toString().equals("")){
                       cliente.getDocumentReference().update("numero",telefono.getText().toString());
                       telefono.setHint(telefono.getText().toString());
                       telefono.setText("");
                       telefono.clearFocus();
                   }
                   if(!direccion.getText().toString().equals(cliente.getDireccion())){
                       cliente.getDocumentReference().update("direccion",direccion.getText().toString());
                   }
                   Toast.makeText(getContext(),"Información Actualizada",Toast.LENGTH_SHORT).show();
               }
            }
        });

        ((ImageButton)view.findViewById(R.id.btnAbrirMaps)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObtenerUbicacionFragment.motivo="perfil";
                Navigation.findNavController(view).navigate(R.id.nav_nueva_ubicacion);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);
            HomeCliente.imgProfile=Utils.getBitmapFromUri(selectedImageUri,getContext());
            HomeCliente.imageView.setImageURI(selectedImageUri);
            Utils.uploadImageProfile(selectedImageUri,getContext());
        }
    }
}