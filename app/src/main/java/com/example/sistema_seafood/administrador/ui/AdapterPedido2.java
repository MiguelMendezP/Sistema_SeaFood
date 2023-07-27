package com.example.sistema_seafood.administrador.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.toolbox.Volley;
import com.example.sistema_seafood.Pedido;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.Utils;
import com.example.sistema_seafood.administrador.InicioAdmin;
import com.example.sistema_seafood.repartidor.EnvioFragment;
import com.example.sistema_seafood.repartidor.HomeRepartidor;
import com.example.sistema_seafood.repartidor.PedidoRepartidor;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AdapterPedido2 extends BaseAdapter {

    private Context context;
    private ArrayList<Pedido> pedidos;

    private FusedLocationProviderClient fusedLocationClient;

    public AdapterPedido2(Context context) {
        this.context = context;
        this.pedidos = new ArrayList<>();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

    }
    public void add(Pedido pedido){
        pedidos.add(pedido);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return pedidos.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vista;


        LayoutInflater layoutInflater=LayoutInflater.from(context);
        vista= layoutInflater.inflate(R.layout.card_pedidos,null);

        TextView tv_fecha, tv_cliente, tv_ubicacion, tv_precio, tv_productos, estados;
        Button btn_aceptar, btn_rechazar, btn_pedidoListo;
        LinearLayout layoutPendiente, layoutPreparacion, layoutRechazado;


        tv_fecha = vista.findViewById(R.id.tv_fecha);
        tv_cliente = vista.findViewById(R.id.tv_cliente);
        tv_ubicacion = vista.findViewById(R.id.tv_ubicacion);
        tv_precio = vista.findViewById(R.id.tv_precio);
        tv_productos = vista.findViewById(R.id.tv_productos);
        estados = vista.findViewById(R.id.estados);

        btn_aceptar = vista.findViewById(R.id.btn_aceptar);
        btn_rechazar = vista.findViewById(R.id.btn_rechazar);
        btn_pedidoListo = vista.findViewById(R.id.btn_pedidoListo);

        layoutPendiente = vista.findViewById(R.id.layoutPendiente);
        layoutPreparacion = vista.findViewById(R.id.layoutPreparacion);
        layoutRechazado = vista.findViewById(R.id.layoutRechazado);


        Pedido pedidoElem = pedidos.get(i);
        tv_fecha.setText(pedidoElem.getFecha().toString());
        tv_cliente.setText(pedidoElem.getCorreoCliente());
        tv_ubicacion.setText(pedidoElem.getDireccion());
        tv_precio.setText("Precio: $" + pedidoElem.getTotal());
        tv_productos.setText(pedidoElem.getProductos());

        switch (pedidoElem.getEstado()) {
            case "pendiente":
                layoutPendiente.setVisibility(View.VISIBLE);
                break;
            case "preparacion":
                layoutPendiente.setVisibility(View.GONE);
                layoutPreparacion.setVisibility(View.VISIBLE);
                break;
            case "listo":
                layoutPendiente.setVisibility(View.GONE);
                layoutRechazado.setVisibility(View.VISIBLE);
                estados.setText("Esperando repartidor");
                break;
            case "enviado":
                layoutPendiente.setVisibility(View.GONE);
                layoutRechazado.setVisibility(View.VISIBLE);
                estados.setText("El pedido esta en camino");
                break;
            case "entregado":
                layoutPendiente.setVisibility(View.GONE);
                layoutRechazado.setVisibility(View.VISIBLE);
                estados.setText("El pedido ya se entrego");
                break;
            case "rechazado":
                layoutPendiente.setVisibility(View.GONE);
                layoutRechazado.setVisibility(View.VISIBLE);
                estados.setText("Haz rechazado este pedido");
                break;
        }

        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedidoElem.getDocumentReference().update("estado","preparacion");
            }
        });

        btn_rechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedidoElem.getDocumentReference().update("estado","rechazado");
            }
        });

        btn_pedidoListo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedidoElem.getDocumentReference().update("estado","listo");
                obtenerUbicacionActual(pedidoElem.getDocumentReference());
            }
        });

        return vista;
    }

    public void actualizar(DocumentChange documentChange){
        for (Pedido pedido: pedidos
             ) {
            if (pedido.getDocumentReference().equals(documentChange.getDocument().getReference())){
                pedido.setEstado(documentChange.getDocument().getString("estado"));
                notifyDataSetChanged();
                break;
            }

        }
    }

    public void obtenerUbicacionActual(DocumentReference documento) {
        // Verificar si se concedió el permiso de ubicación
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Obtener la última ubicación conocida del FusedLocationProviderClient
            fusedLocationClient.getLastLocation().addOnSuccessListener(((InicioAdmin)context), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        // Ubicación obtenida con éxito
                        double latitud = location.getLatitude();
                        double longitud = location.getLongitude();
                        System.out.println(latitud);

                        documento.update("ubicacionPedido", new GeoPoint(latitud, longitud))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(MotionEffect.TAG, "Estado actualizado exitosamente");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(MotionEffect.TAG, "Error al actualizar el estado: " + e.getMessage());
                                    }
                                });
                    }
                }
            });
        }
    }

}
