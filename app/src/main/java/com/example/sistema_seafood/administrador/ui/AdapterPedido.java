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

    private LatLng originLatLng;
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
        Pedido pedido = pedidos.get(position);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        if (pedido.getEstado().equals("pendiente")) {
            holder.layoutAcepRech.setVisibility(View.VISIBLE);
        }
        if (pedido.getEstado().equals("preparacion")) {
            holder.layoutListo.setVisibility(View.VISIBLE);
        }
        if (pedido.getEstado().equals("enviado")) {
            holder.layoutDetalles.setVisibility(View.VISIBLE);
        }
        if (pedido.getEstado().equals("entregado")) {
            holder.layoutDetalles.setVisibility(View.VISIBLE);
        }
        if (pedido.getEstado().equals("rechazado")) {
            holder.layoutRechazado.setVisibility(View.VISIBLE);
        }
        if (pedido.getEstado().equals("listo")) {
            holder.layoutListo.setVisibility(View.VISIBLE);
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
                holder.layoutAcepRech.setVisibility(View.GONE);
                holder.layoutListo.setVisibility(View.VISIBLE);
            }
        });
        holder.btn_rechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarEstadoPedido(pedido.getReferencia(), "rechazado", db);
                holder.layoutAcepRech.setVisibility(View.GONE);
                holder.layoutRechazado.setVisibility(View.VISIBLE);
            }
        });
        holder.btn_pedidoListo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarEstadoPedido(pedido.getReferencia(), "listo", db);

                obtenerUbicacionActual(pedido.getDocumentReference());

                holder.layoutListo.setVisibility(View.GONE);
                holder.layoutEnviado.setVisibility(View.VISIBLE);
            }
        });
        holder.btn_pedidoDetalles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Enviar a la ventana de detalle

            }
        });
    }

    @Override
    public int getItemCount() {

        return pedidos.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_fecha, tv_cliente, tv_ubicacion, tv_precio, tv_productos;
        Button btn_aceptar, btn_rechazar, btn_pedidoListo, btn_pedidoDetalles;
        LinearLayout layoutAcepRech, layoutListo, layoutDetalles, layoutRechazado, layoutEnviado;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_fecha = itemView.findViewById(R.id.tv_fecha);
            tv_cliente = itemView.findViewById(R.id.tv_cliente);
            tv_ubicacion = itemView.findViewById(R.id.tv_ubicacion);
            tv_precio = itemView.findViewById(R.id.tv_precio);
            tv_productos = itemView.findViewById(R.id.tv_productos);

            btn_aceptar = itemView.findViewById(R.id.btn_aceptar);
            btn_pedidoDetalles = itemView.findViewById(R.id.btn_pedidoDetalles);
            btn_rechazar = itemView.findViewById(R.id.btn_rechazar);
            btn_pedidoListo = itemView.findViewById(R.id.btn_pedidoListo);

            layoutAcepRech = itemView.findViewById(R.id.layoutAcepRech);
            layoutListo = itemView.findViewById(R.id.layoutListo);
            layoutDetalles = itemView.findViewById(R.id.layoutDetalles);
            layoutRechazado = itemView.findViewById(R.id.layoutRechazado);
            layoutEnviado = itemView.findViewById(R.id.layoutEnviado);

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
        System.out.println("1");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Obtener la última ubicación conocida del FusedLocationProviderClient
            System.out.println("2");
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

    public void actualizarUbicacion(String referencia, GeoPoint nuevaUbicacion, FirebaseFirestore db) {
        DocumentReference docRef = db.document(referencia);
        docRef.update("ubicacionPedido", nuevaUbicacion)
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
