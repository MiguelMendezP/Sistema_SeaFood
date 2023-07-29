package com.example.sistema_seafood.Notificacion;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.sistema_seafood.cliente.HomeCliente;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FirebaseBackgroundService extends Service {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference pedidosRef = db.collection("Pedidos");
    private Notificacion notificacion;
    public static final String EXTRA_CLIENTE = "cliente";

    private String cliente;


    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra(EXTRA_CLIENTE)) {
            cliente = intent.getStringExtra(EXTRA_CLIENTE);
        }

        notificacion = new Notificacion(this);
        // Agrega el listener para observar cambios en la colección de "pedidos"
        pedidosRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable com.google.firebase.firestore.FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("FirebaseBackgroundService", "Error al obtener los pedidos: ", e);
                    return;
                }
                // Itera sobre los cambios en la colección
                for (DocumentChange documentChange : querySnapshot.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                        // Se detectó un cambio en un documento existente
                        String clientePedido = documentChange.getDocument().getString("cliente");
                        String estadoPedido = documentChange.getDocument().getString("estado");

                        // Verificar si el cliente es igual a "Juan"
                        if (cliente.equals(clientePedido) && estadoPedido != null) {
                            switch (estadoPedido){
                                case "pendiente" :
                                    notificacion.lanzarNotificacion("Pedido Pendiente", "Tu pedido está próximo a prepararse");
                                    break;
                                case "preparacion" :
                                    notificacion.lanzarNotificacion("Pedido en Preparacion", "Tu pedido se esta preparando");
                                    break;
                                case "enviado" :
                                    if(!HomeCliente.pedidoRepartidor.getEstado().equals("enviado")){
                                        notificacion.lanzarNotificacion("Pedido Enviado", "Tu pedido esta en camino");
                                    }
                                    break;
                                case "entregado" :
                                    notificacion.lanzarNotificacion("Pedido Entregado", "Tu pedido se ha entregado, que lo disfrutes!!");
                                    break;
                                case "rechazado" :
                                    notificacion.lanzarNotificacion("Pedido Rechazado", "Tu pedido lamentablemente ha sido rechazado");
                                    break;
                            }
                        }
                    }
                }
            }
        });
        return START_NOT_STICKY; // Si el sistema mata el servicio, no lo reinicie automáticamente.
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}