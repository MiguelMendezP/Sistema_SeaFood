package com.example.sistema_seafood;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sistema_seafood.Notificacion.FirebaseBackgroundService;
import com.example.sistema_seafood.administrador.InicioAdmin;
import com.example.sistema_seafood.cliente.HomeCliente;
import com.example.sistema_seafood.models.usuarioModel;
import com.example.sistema_seafood.repartidor.HomeRepartidor;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ArrayList<usuarioModel> itemsUsuaarios = new ArrayList<>();
    Button btn_login, btn_registrar,btnRecuperarContrasenia;
    EditText et_mail, et_pass;
    String nombre = "";
    String rol = "";
    String correo = "";
    String contrasenia = "";
    String userID = "";

    //Inisiar sesion
    private static final int RC_SIGN_IN = 1;
    FirebaseAuth firebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        et_mail = findViewById(R.id.et_mail);
        et_pass = findViewById(R.id.et_pass);

        btn_login = findViewById(R.id.btn_login);
        btn_registrar = findViewById(R.id.btn_registrar);
        btnRecuperarContrasenia=findViewById(R.id.btnRecuperarContrasenia);
        getItemsUsuario();

        btnRecuperarContrasenia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
showRecuperacion();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String correo = et_mail.getText().toString();
                String contrasenia = et_pass.getText().toString();
                if(!correo.isEmpty() && !contrasenia.isEmpty()) {

                    firebaseAuth.signInWithEmailAndPassword(correo, contrasenia).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                for (int i = 0; i < itemsUsuaarios.size(); i++) {
                                    if (itemsUsuaarios.get(i).getCorreo().equals(correo)) {

                                        if (itemsUsuaarios.get(i).getRol().equals("cliente")) {
                                            guardarSesion(itemsUsuaarios.get(i).getCorreo(), itemsUsuaarios.get(i).getContrasenia(), itemsUsuaarios.get(i).getRol(), itemsUsuaarios.get(i).getNombre());
                                            Intent menuCliente = new Intent(MainActivity.this, HomeCliente.class);
                                            menuCliente.putExtra("correo", itemsUsuaarios.get(i).getCorreo());
                                            menuCliente.putExtra("cliente", itemsUsuaarios.get(i).getNombre());
                                            startActivity(menuCliente);
                                            finish();
                                            break;
                                        } else if (itemsUsuaarios.get(i).getRol().equals("repartidor")) {
                                            guardarSesion(itemsUsuaarios.get(i).getCorreo(), itemsUsuaarios.get(i).getContrasenia(), itemsUsuaarios.get(i).getRol(), itemsUsuaarios.get(i).getNombre());
                                            Intent menuRepartidor = new Intent(MainActivity.this, HomeRepartidor.class);
                                            startActivity(menuRepartidor);
                                            finish();
                                            break;

                                        } else if (itemsUsuaarios.get(i).getRol().equals("admin")) {
                                            guardarSesion(itemsUsuaarios.get(i).getCorreo(), itemsUsuaarios.get(i).getContrasenia(), itemsUsuaarios.get(i).getRol(), itemsUsuaarios.get(i).getNombre());
                                            Intent menuAdmin = new Intent(MainActivity.this, InicioAdmin.class);
                                            startActivity(menuAdmin);
                                            finish();
                                            break;
                                        }


                                    }
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Datos incorrectos", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                }else{
                    Toast.makeText(MainActivity.this, "Ingresa tus datos", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Button loginGoogle = findViewById(R.id.loginGoogle);
        loginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, registrar.class);
                startActivity(i);
            }

        });
    }

    // verifica que el usuario haya accedido:
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void showRecuperacion(){
        if(et_mail.getText().toString().equals("")){
            Toast.makeText(this,"Ingrese correo electronico",Toast.LENGTH_LONG).show();
        }
        else {
            androidx.appcompat.app.AlertDialog.Builder alert=new androidx.appcompat.app.AlertDialog.Builder(this);
            alert.setMessage("Se enviará un correo de recuperación")
                    .setCancelable(true)
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String emailAddress = et_mail.getText().toString();
                            firebaseAuth.sendPasswordResetEmail(emailAddress)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(),"Revisa tu bandeja de entrada",Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(),"Error: "+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
            androidx.appcompat.app.AlertDialog titulo=alert.create();
            titulo.setTitle("Recuperar Contraseña");
            titulo.show();
        }
    }

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            boolean correoExiste = false;
                            for (usuarioModel usuarioModel : itemsUsuaarios) {
                                if (usuarioModel.getCorreo().equals(user.getEmail())) {
                                    // El correo ya existe en algún usuario
                                    correoExiste = true;
                                    break; // Termina el bucle, ya que encontraste el correo buscado
                                }
                            }

                            if (correoExiste) {
                                SharedPreferences preferecnes = getSharedPreferences("sesion", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferecnes.edit();
                                editor.putString("rol", "cliente");
                                editor.commit();
                                updateUI(user);
                            } else {
                                AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
                                LayoutInflater inflater = getLayoutInflater();
                                View view = inflater.inflate(R.layout.dialog_agregar_numero,null);
                                alerta.setView(view);

                                final AlertDialog dialog = alerta.create();
                                dialog.show();

                                TextView et_telefono = view.findViewById(R.id.et_telefono);

                                Button btn_iniciarSesion = view.findViewById(R.id.btn_iniciarSesion);
                                btn_iniciarSesion.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String nombre = user.getDisplayName();
                                        String correo = user.getEmail();
                                        String contrasenia = user.getDisplayName();
                                        String numero = et_telefono.getText().toString();
                                        if (numero.length() == 10) {

                                            Map<String,Object> item = new HashMap<>();
                                            item.put("contrasenia",contrasenia);
                                            item.put("correo",correo);
                                            item.put("favoritos",new ArrayList<String>());
                                            item.put("nombre",nombre);
                                            item.put("numero",numero);
                                            item.put("rol","cliente");
                                            item.put("ubicacion",new GeoPoint(0.0,0.0));

                                            db.collection("usuarios").document(correo).set(item);
                                            dialog.dismiss();
                                            SharedPreferences preferecnes = getSharedPreferences("sesion", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = preferecnes.edit();
                                            editor.putString("rol", "cliente");
                                            editor.commit();
                                            updateUI(user);
                                        }else {
                                            Toast.makeText(MainActivity.this, "Formato de numero incorrecto", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void updateUI(FirebaseUser user) {
        user = firebaseAuth.getCurrentUser();
        SharedPreferences preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        rol = preferences.getString("rol", "rol");

        if (user != null && rol.equals("cliente")){
            irCliente(user.getEmail());
        }else{
            loginCredencialesLocales();
        }
    }
    private void irCliente(String email) {
        Intent menuRepartidor = new Intent(MainActivity.this, HomeCliente.class);
        menuRepartidor.putExtra("correo",email);
        startActivity(menuRepartidor);
        finish();
    }

    public void loginCredencialesLocales(){
        SharedPreferences preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        nombre = preferences.getString("nombre", "nombre");
        correo = preferences.getString("correo", "correo");
        contrasenia = preferences.getString("contrasenia", "contrasenia");
        rol = preferences.getString("rol", "rol");

        System.out.println(correo);
        System.out.println(contrasenia);

        if (preferences.getBoolean("estado", true) == true) {
            firebaseAuth.signInWithEmailAndPassword(correo, contrasenia).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        if (rol.equals("cliente")) {
                            Intent menuCliente = new Intent(MainActivity.this, HomeCliente.class);
                            menuCliente.putExtra("correo",correo);
                            menuCliente.putExtra("nombre",nombre);
                            startActivity(menuCliente);
                        } else if (rol.equals("repartidor")) {
                            Intent menuRepartidor = new Intent(MainActivity.this, HomeRepartidor.class);
                            startActivity(menuRepartidor);
                        } else if (rol.equals("admin")) {
                            Intent menuAdmin = new Intent(MainActivity.this, InicioAdmin.class);
                            startActivity(menuAdmin);

                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Datos incorrectos", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void guardarSesion(String correo, String contrasenia, String rol, String nombre) {
        SharedPreferences preferecnes = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferecnes.edit();
        editor.putBoolean("estado", true);
        editor.putString("correo", correo);
        editor.putString("contrasenia", contrasenia);
        editor.putString("rol", rol);
        editor.putString("nombre", nombre);
        editor.commit();
    }

    public void getItemsUsuario() {
        itemsUsuaarios.clear();
        db.collection("usuarios")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String correo = (String) document.getData().get("correo");
                                String nombre = (String) document.getData().get("nombre");
                                String contrasenia = (String) document.getData().get("contrasenia");
                                String numero = (String) document.getData().get("numero");
                                String rol = (String) document.getData().get("rol");
                                itemsUsuaarios.add(new usuarioModel(correo, nombre, contrasenia, numero, rol));
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }


}
