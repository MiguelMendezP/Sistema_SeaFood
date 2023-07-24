package com.example.sistema_seafood.repartidor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.GeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EnvioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnvioFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, LocationListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    JsonObjectRequest jsonObjectRequest;
    RequestQueue request;
    private String mParam2;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationManager locationManager;
    private View view;


    public static PedidoRepartidor pedido;
    private Marker marker;
    private LatLng originLatLng, destinationLatLng;


    public EnvioFragment() {
        // Required empty public constructor
    }


    public void setPedidoRepartidor(PedidoRepartidor pedidoRepartidor) {
        pedido = pedidoRepartidor;
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
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                ((HomeRepartidor)getActivity()).showInicio();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_envio, container, false);
        request = Volley.newRequestQueue(getContext());
        if (pedido == null) {
            ((TextView) view.findViewById(R.id.nameCliente)).setText("Sin pedido");
            ((TextView) view.findViewById(R.id.destino)).setText("Desconocido");
            mostrarAlerta();
        } else {
            ((TextView) view.findViewById(R.id.nameCliente)).setText(pedido.getCliente());
            ((TextView) view.findViewById(R.id.destino)).setText(pedido.getDireccion());
            ((Button) view.findViewById(R.id.btnEntregar)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pedido.setEstado("entregado");
                    ((HomeRepartidor) getActivity()).showPedidos();
                    pedido = null;
                }
            });
            ((FrameLayout) view.findViewById(R.id.showDetails)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDetails();
                }
            });

            ((Button) view.findViewById(R.id.btnEntregar)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    entregarPedido();
                }
            });

            ((Button)view.findViewById(R.id.btnTrazarRuta)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    trazarRuta();
                }
            });
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapEnvio);
            mapFragment.getMapAsync(this);
        }
        actualizarUbicacion();
            return view;
        }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap=googleMap;
////        this.mMap.setOnMapClickListener(this);
////        this.mMap.setOnMapLongClickListener(this);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Si se concedió el permiso, habilitar la capa de ubicación en el mapa
            mMap.setMyLocationEnabled(true);
            // Obtener la ubicación actual
            obtenerUbicacionActual();
        } else {
            // Si no se concedió el permiso, solicitarlo al usuario
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    public void trazarRuta(){
        LatLng center = null;
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;

        // setUpMapIfNeeded();

        // recorriendo todas las rutas
        for(int i=0;i<Utils.routes.size();i++){
            points = new ArrayList<LatLng>();
            lineOptions = new PolylineOptions();

            // Obteniendo el detalle de la ruta
            List<HashMap<String, String>> path = Utils.routes.get(i);

            // Obteniendo todos los puntos y/o coordenadas de la ruta
            for(int j=0;j<path.size();j++){
                HashMap<String,String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                if (center == null) {
                    //Obtengo la 1ra coordenada para centrar el mapa en la misma.
                    center = new LatLng(lat, lng);
                }
                points.add(position);
            }

            // Agregamos todos los puntos en la ruta al objeto LineOptions
            lineOptions.addAll(points);
            //Definimos el grosor de las Polilíneas
            lineOptions.width(2);
            //Definimos el color de la Polilíneas
            lineOptions.color(Color.BLUE);
        }

        // Dibujamos las Polilineas en el Google Map para cada ruta
        mMap.addPolyline(lineOptions);

        LatLng origen = new LatLng(Utils.coordenadas.getLatitudInicial(), Utils.coordenadas.getLongitudInicial());
        mMap.addMarker(new MarkerOptions().position(origen).title("ubicación de la sucursal"));

        LatLng destino = new LatLng(Utils.coordenadas.getLatitudFinal(), Utils.coordenadas.getLongitudFinal());
        mMap.addMarker(new MarkerOptions().position(destino).title("Ubicación de entrega del pedido"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 15));
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
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                    }
                }
            });
        }
    }

    @SuppressLint("MissingInflatedId")
    public void showDetails(){
        AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.car_detalles, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        ((TextView)dialogView.findViewById(R.id.nombreClienteDetails)).setText(pedido.getCliente());
        ((TextView)dialogView.findViewById(R.id.telefonoClienteDetails)).setText("");
        ((TextView)dialogView.findViewById(R.id.totalDetails)).setText("$ "+pedido.getTotal());
        LinearLayout linearLayout=dialogView.findViewById(R.id.listProductsDetails);
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

    public void entregarPedido(){
        AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.car_detalles, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        ((TextView)dialogView.findViewById(R.id.nombreClienteDetails)).setText(pedido.getCliente());
        ((TextView)dialogView.findViewById(R.id.telefonoClienteDetails)).setText("");
        ((TextView)dialogView.findViewById(R.id.totalDetails)).setText("$ "+pedido.getTotal());
        LinearLayout linearLayout=dialogView.findViewById(R.id.listProductsDetails);
        for (Map map:pedido.getProductos()){
            TextView textView= new TextView(getContext());
            textView.setText(map.get("cantidad")+" "+map.get("producto"));
            linearLayout.addView(textView);
        }

        alert.setView(dialogView)
                .setNegativeButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        removeUpdates();
                        pedido.getDocumentReference().update("estado","entregado");
                        ((HomeRepartidor)getContext()).showPedidos();
                        pedido=null;
                    }
                });

        AlertDialog titulo=alert.create();
        titulo.setTitle("Entrega de pedido");
        titulo.show();
    }

    public void actualizarUbicacion(){
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        // Comprueba si se tienen los permisos necesarios para acceder a la ubicación
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Registra el LocationListener para recibir actualizaciones de ubicación
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5l, 1f, this::onLocationChanged);
        } else {
            // Si no se tienen los permisos, solicítalos
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);}
    }

    public void removeUpdates(){
        locationManager.removeUpdates(this::onLocationChanged);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(pedido!=null) {
            pedido.getDocumentReference().update("ubicacionPedido", new GeoPoint(location.getLatitude(), location.getLongitude()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15f));
        }
    }


}