package com.example.sistema_seafood.cliente;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sistema_seafood.Pedido;
import com.example.sistema_seafood.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executor;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentEstadoPedido#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentEstadoPedido extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Pedido pedido;

    private GoogleMap googleMap;
    private View view;

    private TextView estado;

    public FragmentEstadoPedido() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentEstadoPedido.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentEstadoPedido newInstance(String param1, String param2) {
        FragmentEstadoPedido fragment = new FragmentEstadoPedido();
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
        view= inflater.inflate(R.layout.fragment_estado_pedido, container, false);
        estado=view.findViewById(R.id.estado);
        pedido=HomeCliente.getPedido();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //if(mapFragment!=null)
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
this.googleMap=googleMap;
this.googleMap.setOnMapClickListener(this);
this.googleMap.setOnMapLongClickListener(this);
pedido.getDocumentReference().addSnapshotListener(new EventListener<DocumentSnapshot>() {
    @Override
    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
        GeoPoint geoPoint=value.getGeoPoint("ubicacionPedido");
        LatLng ubicacion=new LatLng(geoPoint.getLatitude(),geoPoint.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(ubicacion).title("Mi ubicacion"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacion));
        switch (value.getString("estado")){
            case "en espera":
                estado.setText("Estamos preparando tu pedido");
                break;
            case "enviado":
                estado.setText("Tu pedido se encuentra en camino");
                break;
        }
    }
});

//LatLng miUbicacion= new LatLng(17.125263,-96.773983);
//this.googleMap.addMarker(new MarkerOptions().position(miUbicacion).title("Mi ubicacion"));
//this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(miUbicacion));
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

    }
}