package com.example.sistema_seafood.cliente;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.sistema_seafood.Cliente;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.repartidor.HomeRepartidor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CambiarContraseniaCliente#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CambiarContraseniaCliente extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;
    private ImageButton visible1,visible2;
    private boolean isVisible1=false,isVisible2=false;
    private EditText newPassword, confirmPassword, currentPassword;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    public CambiarContraseniaCliente() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CambiarContraseniaCliente.
     */
    // TODO: Rename and change types and number of parameters
    public static CambiarContraseniaCliente newInstance(String param1, String param2) {
        CambiarContraseniaCliente fragment = new CambiarContraseniaCliente();
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
        view=inflater.inflate(R.layout.fragment_cambiar_contrasenia_cliente, container, false);
        visible1=view.findViewById(R.id.visible1);
        visible2=view.findViewById(R.id.visible2);
        currentPassword = view.findViewById(R.id.editCurrentPass);
        newPassword=view.findViewById(R.id.editNewPass);
        confirmPassword=view.findViewById(R.id.editConfirm);

        visible1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVisible1=!isVisible1;
                if(isVisible1){
                    visible1.setImageResource(R.drawable.baseline_visibility_24);
                    newPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                else {
                    visible1.setImageResource(R.drawable.baseline_visibility_off_24);
                    newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        visible2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVisible2=!isVisible2;
                if(isVisible2){
                    visible2.setImageResource(R.drawable.baseline_visibility_24);
                    confirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                else {
                    visible2.setImageResource(R.drawable.baseline_visibility_off_24);
                    confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        ((Button)view.findViewById(R.id.btnSaveChanges)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                if(newPassword.getText().toString().equals(confirmPassword.getText().toString()) && !currentPassword.getText().toString().isEmpty()){
                    String newPass = confirmPassword.getText().toString();

                    user.updatePassword(newPass)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Contraseña cambiada exitosamente
                                        Toast.makeText(getContext(), "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                                        actualizarSesion(newPass);
                                        if(getActivity() instanceof HomeCliente){
                                            Navigation.findNavController(view).navigate(R.id.nav_perfil);
                                        }
                                        else if((getActivity() instanceof HomeRepartidor)){
                                            ((HomeRepartidor)getActivity()).showPerfil();
                                        }else{
                                            Navigation.findNavController(v).navigate(R.id.nav_perfil);
                                        }
                                    } else {
                                        // Ocurrió un error al cambiar la contraseña
                                        Toast.makeText(getContext(), "Igrese una contraseña válida, que contenga números y caracteres", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else if (currentPassword.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Ingresa tu contraseña actual", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(), "Las contraseñas no coinciden, confirme su nueva contraseña", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return view;
    }
    public void actualizarSesion(String contrasenia) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("contrasenia", contrasenia);
        editor.apply();
    }

}