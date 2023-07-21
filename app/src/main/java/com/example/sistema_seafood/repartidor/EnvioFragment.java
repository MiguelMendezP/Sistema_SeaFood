package com.example.sistema_seafood.repartidor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sistema_seafood.R;
import com.example.sistema_seafood.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EnvioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnvioFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;

    private View view;

    public ObtenerCoordenadas obtenerCoordenadas;

    public PedidoRepartidor pedido;
    private Marker marker;
    private LatLng originLatLng,destinationLatLng;


    public EnvioFragment() {
        // Required empty public constructor
    }


    public void setPedidoRepartidor(PedidoRepartidor pedidoRepartidor){
        pedido=pedidoRepartidor;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EnvioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EnvioFragment newInstance(String param1, String param2) {
        EnvioFragment fragment = new EnvioFragment();
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
        view= inflater.inflate(R.layout.fragment_envio, container, false);
        if(pedido==null){
            ((TextView)view.findViewById(R.id.nameCliente)).setText("Sin pedido");
            ((TextView)view.findViewById(R.id.destino)).setText("Desconocido");
mostrarAlerta();
        }
        else {
            obtenerCoordenadas=new ObtenerCoordenadas(getContext());
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapEnvio);
            mapFragment.getMapAsync(this);
            destinationLatLng=new LatLng(pedido.getUbicacion().getLatitude(),pedido.getUbicacion().getLongitude());
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
            ((TextView)view.findViewById(R.id.nameCliente)).setText(pedido.getCliente());
            ((TextView)view.findViewById(R.id.destino)).setText(pedido.getDireccion());
            ((Button)view.findViewById(R.id.btnEntregar)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pedido.setEstado("entregado");
                    ((HomeRepartidor)getActivity()).showPedidos();
                    pedido=null;
                }
            });

            ((FrameLayout)view.findViewById(R.id.showDetails)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDetails();
                }
            });
        }

        return view;
    }

    @SuppressLint("MissingInflatedId")
    public void showDetails(){
        AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.card_detalles, null);


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        ((TextView)dialogView.findViewById(R.id.nombreClienteDetails)).setText(pedido.getCliente());
        ((TextView)dialogView.findViewById(R.id.direccionClienteDetails)).setText(pedido.getDireccion());
        LinearLayout linearLayout=dialogView.findViewById(R.id.lisProductsDetails);
        for (Map map:pedido.getProductos()){
            TextView textView= new TextView(getContext());
            textView.setText(map.get("cantidad")+" "+map.get("producto"));
            linearLayout.addView(textView);
        }

        alert.setView(dialogView)
                .setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        AlertDialog titulo=alert.create();
        titulo.setTitle("Detalles");
        titulo.show();
    }

    public void mostrarAlerta(){
        AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
        alert.setMessage("No tienes ningun pedido asignado")
                .setCancelable(false)
                .setPositiveButton("Ver pedidos disponibles", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((HomeRepartidor)getActivity()).showInicio();
                    }
                });
        AlertDialog titulo=alert.create();
        titulo.setTitle("Sin pedido asignado");
        titulo.show();
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
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

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

                        originLatLng=new LatLng(latitud,longitud);
                        if (marker != null) {
                            marker.remove();
                        }
                        // Mover la cámara del mapa a la ubicación actual
                        LatLng latLng = new LatLng(latitud, longitud);
                        marker=googleMap.addMarker(new MarkerOptions().position(latLng).title("Ubicación Actual"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                        getDirections();

                    }
                }
            });
        }
    }

    private void getDirections() {
        // Crear la instancia del objeto de solicitud de ruta
        String origin = originLatLng.latitude+","+originLatLng.longitude; // Reemplaza con las coordenadas del origen
        String destination = destinationLatLng.latitude+","+destinationLatLng.longitude; // Reemplaza con las coordenadas del destino
        String apiKey = "AIzaSyDRyqbrSaX1GzpaPbREgCHfDvfpg3bRAcM"; // Reemplaza con tu clave de API

        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin + "&destination=" + destination + "&key=" + apiKey;

        // Realizar la solicitud utilizando Volley o cualquier otra biblioteca de tu elección
        // Analizar la respuesta JSON y extraer las coordenadas de la ruta

        // Añadir marcadores al mapa para el origen y el destino
        googleMap.addMarker(new MarkerOptions().position(originLatLng).title("Origen"));
        googleMap.addMarker(new MarkerOptions().position(destinationLatLng).title("Destino"));

        // Crear y añadir una polilínea para trazar la ruta en el mapa
        PolylineOptions polylineOptions = new PolylineOptions()
                .add(originLatLng)
                .add(destinationLatLng)
                .color(Color.BLUE);

        googleMap.addPolyline(polylineOptions);

        // Mover la cámara para mostrar toda la ruta
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(originLatLng);
        builder.include(destinationLatLng);
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
        googleMap.animateCamera(cu);
    }


}