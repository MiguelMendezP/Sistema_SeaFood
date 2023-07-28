package com.example.sistema_seafood.cliente;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sistema_seafood.Carrito;
import com.example.sistema_seafood.ObtenerUbicacionFragment;
import com.example.sistema_seafood.Pedido;
import com.example.sistema_seafood.ProductoOrdenado;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.Ubicacion;
import com.example.sistema_seafood.Utils;
import com.example.sistema_seafood.repartidor.HomeRepartidor;
import com.example.sistema_seafood.repartidor.PedidoRepartidor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConfirmarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfirmarFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static LatLng direccionEntrega;

    private LinearLayout linearProducto,linearTotal;
    private View view;

    private GridView gridView;

    private AdaptadorProductosConfirmar adaptadorProductosConfirmar;

    public ConfirmarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConfirmarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConfirmarFragment newInstance(String param1, String param2) {
        ConfirmarFragment fragment = new ConfirmarFragment();
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
        view= inflater.inflate(R.layout.fragment_confirmar, container, false);
        HomeCliente.setTitulo("Confirmar Pedido");
        gridView=view.findViewById(R.id.contenedorConfirmar);
adaptadorProductosConfirmar=new AdaptadorProductosConfirmar(getContext(),HomeCliente.getCarrito().getProductoOrdenados());
gridView.setAdapter(adaptadorProductosConfirmar);

        ((TextView)view.findViewById(R.id.txtSubtotal)).setText(HomeCliente.getCarrito().getTotal()+"");
        ((TextView)view.findViewById(R.id.txtEnvio)).setText(""+30.0);
        ((TextView)view.findViewById(R.id.txtTotal)).setText(HomeCliente.getCarrito().getTotal()+30.0+"");
//        ((TextView)view.findViewById(R.id.totalPedido)).setText("$ "+(HomeCliente.getCarrito().getTotal()+30));
        String direccion;

        if (direccionEntrega==null){
            direccion= Utils.getAddressFromLatLng(getContext(),HomeCliente.cliente.getUbicacion().getLatitude(),HomeCliente.getCliente().getUbicacion().getLongitude());
        }
        else {
            direccion=Utils.getAddressFromLatLng(getContext(),direccionEntrega.latitude,direccionEntrega.longitude);
        }
        TextView direccionGuardada = ((TextView)view.findViewById(R.id.direccionGuardada));
        direccionGuardada.setText(direccion);
        Button btnAgregarDireccion=view.findViewById(R.id.btnNuevaUbicacion);
        btnAgregarDireccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObtenerUbicacionFragment.motivo="ubicacionEntrega";
                Navigation.findNavController(view).navigate(R.id.nav_nueva_ubicacion);
            }
        });
        Button btnConfirmar=view.findViewById(R.id.btnConfirmar);
        if(!direccionGuardada.getText().equals("")){
            btnConfirmar.setEnabled(true);
        }
        else {
            btnConfirmar.setEnabled(false);
        }
        ((Button)view.findViewById(R.id.btnConfirmar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeoPoint geoPoint;
                if(direccionEntrega==null){
                    geoPoint=HomeCliente.cliente.getUbicacion();
                }
                else {
                    geoPoint=new GeoPoint(direccionEntrega.latitude,direccionEntrega.longitude);
                }
                Map map=new HashMap();
                map.put("cliente",HomeCliente.getCliente().getNombre());
                map.put("fecha",new Date());
                map.put("ubicacion",geoPoint);
                map.put("repartidor","no asignado");
                map.put("estado","pendiente");
                map.put("productos",HomeCliente.getCarrito().getProductos());
                map.put("ubicacionPedido",new GeoPoint(17.097837343208298, -96.75758794245301));
                map.put("direccion",Utils.getAddressFromLatLng(getContext(),geoPoint.getLatitude(),geoPoint.getLongitude()));
                map.put("total",HomeCliente.getCarrito().getTotal()+30);
                map.put("telefono",HomeCliente.cliente.getNumTelefono());


                FirebaseFirestore.getInstance().collection("Pedidos").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                ListenerRegistration listenerRegistration = documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                        // Aquí se manejan los cambios en el documento
                                        if (documentSnapshot != null && documentSnapshot.exists()) {
                                            PedidoRepartidor pedidoRepartidor=new PedidoRepartidor(documentSnapshot.getString("cliente"),documentSnapshot.getString("estado"),documentSnapshot.getDate("fecha"), (ArrayList<Map>) documentSnapshot.get("productos"), documentSnapshot.getGeoPoint("ubicacion"),documentSnapshot.getReference(),documentSnapshot.getString("direccion"),documentSnapshot.getGeoPoint("ubicacionPedido"),documentSnapshot.getDouble("total"),documentSnapshot.getString("telefono"));
                                            HomeCliente.pedidoRepartidor=pedidoRepartidor;
                                            HomeCliente.floatingActionButton.setVisibility(View.VISIBLE);
                                            // El documento existe y contiene datos
                                            // Puedes obtener los datos con documentSnapshot.getData()
                                        } else {
                                            // El documento no existe o está vacío
                                        }
                                    }
                                });
                                //HomeCliente.pedido.setDocumentReference(documentReference);
                                HomeCliente.carrito=new Carrito();
                                Toast.makeText(getContext(),"Tu pedido se ha realizado con éxito", Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(view).navigate(R.id.nav_home);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
            }
        });
        return view;
    }
}