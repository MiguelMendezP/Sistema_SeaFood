package com.example.sistema_seafood.cliente;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sistema_seafood.Pedido;
import com.example.sistema_seafood.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentEstadoPedido#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentEstadoPedido extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.InfoWindowAdapter {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private GoogleMap googleMap;
    private MarkerOptions markerOptions;

    private Polyline polyline;

    private ListenerRegistration listenerRegistration;
    private View view;
    private Marker miMarcador;

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
        HomeCliente.setTitulo("Seguimiento del pedido");
        estado=view.findViewById(R.id.estado);
        estado.setText(HomeCliente.pedidoRepartidor.getEstado());
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
googleMap.setInfoWindowAdapter(this);
recibirActualizaciones();
    }

    private void recibirActualizaciones(){
        listenerRegistration = HomeCliente.pedidoRepartidor.getDocumentReference().addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("FirestoreListener", "Error al escuchar cambios", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    if(miMarcador!=null){
                        miMarcador.remove();
                    }
                    LatLng latLng=new LatLng(snapshot.getGeoPoint("ubicacionPedido").getLatitude(),snapshot.getGeoPoint("ubicacionPedido").getLongitude());
                    markerOptions = new MarkerOptions()
                            .position(latLng)
                            .title("Repartidor").icon(BitmapDescriptorFactory.fromResource(R.drawable.motorcycle));
                    //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.motorcycle));
                    miMarcador=googleMap.addMarker(markerOptions);
                   // miMarcador.showInfoWindow();
                   // miMarcador.setIcon(new BitmapDescriptor());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

                    if (polyline == null) {
                        // Si aún no hay una línea trazada, crear una nueva
                        List<PatternItem> pattern = Arrays.asList(new Dot(), new Gap(20f));
                        PolylineOptions polylineOptions = new PolylineOptions()
                                .add(latLng)
                                .color(ContextCompat.getColor(getContext(), R.color.amarillo))
                                .width(10)
                                .pattern(pattern);
                        polyline = googleMap.addPolyline(polylineOptions);
                    } else {
                        // Si ya hay una línea trazada, agregar un nuevo punto
                        List<LatLng> points = polyline.getPoints();
                        points.add(latLng);
                        polyline.setPoints(points);
                    }
                    // El documento existe y se ha recibido una actualización
                    // Accede a los datos del documento usando snapshot.getData()
                    // Por ejemplo, si el documento contiene un campo "nombre":
                    // String nombre = snapshot.getString("nombre");
                } else {
                    // El documento no existe (por ejemplo, ha sido eliminado)
                    Log.d("FirestoreListener", "El documento no existe");
                }
            }
        });
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null; // Usaremos solo getInfoContents para personalizar el globo
    }

    @Override
    public View getInfoContents(Marker marker) {
        // Obtener el diseño personalizado del globo
        View infoWindowView = LayoutInflater.from(getContext()).inflate(R.layout.custom_info_window, null);


        // Obtener el ImageView del diseño personalizado
        //ImageView markerIconImageView = infoWindowView.findViewById(R.id.marker_icon);

        // Establecer la imagen del icono del marcador (puedes cargarla dinámicamente según el marcador)
        //markerIconImageView.setImageResource(R.drawable.motorcycle);

        // Otros detalles, si es necesario

        return infoWindowView;
    }
}