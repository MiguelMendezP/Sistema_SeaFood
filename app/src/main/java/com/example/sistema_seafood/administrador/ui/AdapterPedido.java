package com.example.sistema_seafood.administrador.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sistema_seafood.Pedido;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.administrador.InicioAdmin;
import com.example.sistema_seafood.administrador.ui.pedido.PedidoFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AdapterPedido extends RecyclerView.Adapter<AdapterPedido.MyViewHolder> {

    private Context context;
    private ArrayList<Pedido> pedidos;

    private FusedLocationProviderClient fusedLocationClient;

    public AdapterPedido(Context context, ArrayList<Pedido> pedidos) {
        this.context = context;
        this.pedidos = pedidos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_pedidos, parent, false);


        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPedido.MyViewHolder holder, int position) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        Pedido pedido = pedidos.get(position);
        switch (pedido.getEstado()) {
            case "pendiente":
                holder.layoutPendiente.setVisibility(View.VISIBLE);
                break;
            case "preparacion":
                holder.layoutPendiente.setVisibility(View.INVISIBLE);
                holder.layoutPreparacion.setVisibility(View.VISIBLE);
                break;
            case "listo":
                holder.layoutPendiente.setVisibility(View.INVISIBLE);
                holder.layoutRechazado.setVisibility(View.VISIBLE);
                holder.estados.setText("Esperando repartidor");
                break;
            case "enviado":
                holder.layoutPendiente.setVisibility(View.INVISIBLE);
                holder.layoutRechazado.setVisibility(View.VISIBLE);
                holder.estados.setText("El pedido esta en camino");
                break;
            case "entregado":
                holder.layoutPendiente.setVisibility(View.INVISIBLE);
                holder.layoutRechazado.setVisibility(View.VISIBLE);
                holder.estados.setText("El pedido ya se entrego");
                break;
            case "rechazado":
                holder.layoutPendiente.setVisibility(View.INVISIBLE);
                holder.layoutRechazado.setVisibility(View.VISIBLE);
                holder.estados.setText("Haz rechazado este pedido");
                break;
        }

        //Cambiar el formato de un objeto Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        String fechaFormateada = dateFormat.format(pedido.getFecha());

        holder.tv_fecha.setText(fechaFormateada);
        holder.tv_cliente.setText(pedido.getCorreoCliente());
        holder.tv_ubicacion.setText(pedido.getDireccion());
        holder.tv_precio.setText("Precio: $" + pedido.getTotal());
        holder.tv_productos.setText(pedido.getProductos());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        holder.btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarEstadoPedido(pedido.getReferencia(), "preparacion", db);
                holder.layoutPendiente.setVisibility(View.GONE);
                holder.layoutPreparacion.setVisibility(View.VISIBLE);
            }
        });
        holder.btn_rechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarEstadoPedido(pedido.getReferencia(), "rechazado", db);
                holder.layoutPendiente.setVisibility(View.GONE);
                holder.layoutRechazado.setVisibility(View.VISIBLE);
            }
        });
        holder.btn_pedidoListo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarEstadoPedido(pedido.getReferencia(), "listo", db);
                obtenerUbicacionActual(pedido.getDocumentReference());
                holder.layoutPreparacion.setVisibility(View.GONE);
                holder.layoutListo.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {

        return pedidos.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_fecha, tv_cliente, tv_ubicacion, tv_precio, tv_productos,estados;
        Button btn_aceptar, btn_rechazar, btn_pedidoListo;
        LinearLayout layoutPendiente, layoutPreparacion, layoutRechazado, layoutListo;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_fecha = itemView.findViewById(R.id.tv_fecha);
            tv_cliente = itemView.findViewById(R.id.tv_cliente);
            tv_ubicacion = itemView.findViewById(R.id.tv_ubicacion);
            tv_precio = itemView.findViewById(R.id.tv_precio);
            tv_productos = itemView.findViewById(R.id.tv_productos);
            estados = itemView.findViewById(R.id.estados);

            btn_aceptar = itemView.findViewById(R.id.btn_aceptar);
            btn_rechazar = itemView.findViewById(R.id.btn_rechazar);
            btn_pedidoListo = itemView.findViewById(R.id.btn_pedidoListo);

            layoutPendiente = itemView.findViewById(R.id.layoutPendiente);
            layoutPreparacion = itemView.findViewById(R.id.layoutPreparacion);
            layoutRechazado = itemView.findViewById(R.id.layoutRechazado);


        }
    }

    public void actualizarEstadoPedido(String referencia, String nuevoEstado, FirebaseFirestore db) {
        DocumentReference docRef = db.document(referencia);
        docRef.update("estado", nuevoEstado)
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
