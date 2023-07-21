package com.example.sistema_seafood.repartidor;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sistema_seafood.R;
import com.example.sistema_seafood.Repartidor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilRepartidorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilRepartidorFragment extends Fragment {

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
    ImageView imageView;

    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private Uri selectedImageUri;
    private StorageReference storageRef= FirebaseStorage.getInstance().getReference();

    private Repartidor repartidor;
    public PerfilRepartidorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerfilRepartidorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PerfilRepartidorFragment newInstance(String param1, String param2) {
        PerfilRepartidorFragment fragment = new PerfilRepartidorFragment();
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_perfil_repartidor, container, false);
        //login();
        repartidor=((HomeRepartidor)getActivity()).getRepartidor();
        imageView= view.findViewById(R.id.imgRepartidor);
        ((TextView)view.findViewById(R.id.nombreRepartidor)).setText(repartidor.getNombre());
        EditText correo=view.findViewById(R.id.editEmailRepartidor);
        EditText telefono=view.findViewById(R.id.editTelefonoRepartidor);
        correo.setHint(repartidor.getCorreo());
        telefono.setHint(repartidor.getNumTelefono());
        imageView.setImageBitmap(HomeRepartidor.bitmap);

        ((ImageButton)view.findViewById(R.id.btnNewFoto)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });
        ((ImageButton)view.findViewById(R.id.btnEditCorreo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                correo.setEnabled(true);
                correo.requestFocus();
                telefono.setEnabled(false);
                showTeclado(correo);
            }
        });

        ((ImageButton)view.findViewById(R.id.btnEditTelefono)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telefono.setEnabled(true);
                telefono.requestFocus();
                correo.setEnabled(false);
                showTeclado(telefono);
            }
        });
        ((Button)view.findViewById(R.id.btnActualizar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhoto(selectedImageUri);
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

        }
    }
    private void uploadPhoto(Uri imageUri) {
        if (imageUri != null) {
            StorageReference photoRef = storageRef.child("usuarios").child(mAuth.getCurrentUser().getUid() + ".jpg");

            photoRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Foto subida exitosamente
                            Toast.makeText(getContext(), "Foto de perfil actualizada.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Error al subir la foto
                            Log.e("ProfileActivity", "Error al subir la foto: " + e.getMessage());
                        }
                    });
        }
    }

    public void showTeclado(EditText editText){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }
}