package com.example.sistema_seafood;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.sistema_seafood.cliente.ConfirmarFragment;
import com.example.sistema_seafood.cliente.HomeCliente;
import com.example.sistema_seafood.cliente.PerfilFragment;
import com.example.sistema_seafood.repartidor.ObtenerCoordenadas;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ObtenerUbicacionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ObtenerUbicacionFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static String motivo;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private GoogleMap googleMap;
    private View view;

    private Marker marker;

    private TextView ubicacion;
    private FusedLocationProviderClient fusedLocationClient;



    public ObtenerUbicacionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ObtenerUbicacionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ObtenerUbicacionFragment newInstance(String param1, String param2) {
        ObtenerUbicacionFragment fragment = new ObtenerUbicacionFragment();
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
        view= inflater.inflate(R.layout.fragment_obtener_ubicacion, container, false);
        HomeCliente.setTitulo("Ubicación");
        ubicacion=view.findViewById(R.id.showDireccion);
        TextView showMotivo=view.findViewById(R.id.showMotivo);
        if(motivo.equals("perfil")){
            showMotivo.setText("Seleccione su nueva ubicación");
        }
        else {
            showMotivo.setText("Seleccione su ubicación de entrega");
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapUbicacion);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        ((Button)view.findViewById(R.id.btnConfirmarUbicacion)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(motivo.equals("perfil")){
                    PerfilFragment.nuevaDireccion=Utils.getAddressFromLatLng(getContext(),marker.getPosition().latitude,marker.getPosition().longitude);
                    Navigation.findNavController(view).navigate(R.id.nav_perfil);
                }
                else {
                    ConfirmarFragment.direccionEntrega=marker.getPosition();
                    Navigation.findNavController(view).navigate(R.id.nav_confirmar);
                }


            }
        });
        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap=googleMap;
        this.googleMap.setOnMapClickListener(this);
        this.googleMap.setOnMapLongClickListener(this);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Si se concedió el permiso, habilitar la capa de ubicación en el mapa
            googleMap.setMyLocationEnabled(true);
            // Obtener la ubicación actual
            obtenerUbicacionActual();
        } else {
            // Si no se concedió el permiso, solicitarlo al usuario
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                obtenerUbicacionActual();
                return false;
            }
        });
//        ObtenerCoordenadas obtenerCoordenadas=new ObtenerCoordenadas(getContext());
//        double [] ubicacion= obtenerCoordenadas.getCoordenadasActuales();
//                LatLng latLng=new LatLng(ubicacion[0],ubicacion[1]);
//                googleMap.addMarker(new MarkerOptions().position(latLng).title("Ubicacion Actual"));
//                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15.0f));
    }

    private void obtenerUbicacionActual() {
        // Verificar si se concedió el permiso de ubicación
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Obtener la última ubicación conocida del FusedLocationProviderClient
            fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        // Ubicación obtenida con éxito
                        double latitud = location.getLatitude();
                        double longitud = location.getLongitude();
                        ubicacion.setText(Utils.getAddressFromLatLng(getContext(),latitud,longitud));
                        if (marker != null) {
                            marker.remove();
                        }
                        // Mover la cámara del mapa a la ubicación actual
                        LatLng latLng = new LatLng(latitud, longitud);
                        marker=googleMap.addMarker(new MarkerOptions().position(latLng).title("Ubicación Actual"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                    }
                }
            });
        }
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        if (marker != null) {
            marker.remove();
        }

        // Agregar un nuevo marcador en las coordenadas del punto donde se hizo clic
        marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("Ubicación de entrega"));

        // Opcionalmente, mover la cámara del mapa al nuevo marcador
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        ubicacion.setText(Utils.getAddressFromLatLng(getContext(),latLng.latitude,latLng.longitude));
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

    }
}