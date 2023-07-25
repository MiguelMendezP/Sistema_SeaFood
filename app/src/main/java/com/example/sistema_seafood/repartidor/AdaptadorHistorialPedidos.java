package com.example.sistema_seafood.repartidor;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.sistema_seafood.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdaptadorHistorialPedidos extends BaseAdapter {
    public Context context;
    private List<PedidoRepartidor> pedidos;

    public PedidoRepartidor getCategoria(int position){
        return pedidos.get(position);
    }

    public AdaptadorHistorialPedidos(Context context){
        this.context=context;
        this.pedidos =new ArrayList<>();
    }

    public void actualizar(DocumentChange documentChange){
        for (PedidoRepartidor pedidoRepartidor:pedidos){
            if (pedidoRepartidor.getDocumentReference().equals(documentChange.getDocument().getReference())){
                pedidoRepartidor.setEstado(documentChange.getDocument().getString("estado"));
                return;
            }
        }
    }

    public void add(PedidoRepartidor pedido){
        pedidos.add(pedido);
        notifyDataSetChanged();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v;
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        PedidoRepartidor aux= pedidos.get(i);
        v= layoutInflater.inflate(R.layout.card_pedido_entregado,null);
        ((TextView)v.findViewById(R.id.nombreC)).setText(aux.getCliente());
        ((TextView)v.findViewById(R.id.direccionC)).setText(aux.getDireccion());
        LinearLayout linearLayout=v.findViewById(R.id.listaProduct);
        for(Map producto:aux.getProductos()){
            TextView textView=new TextView(context);
            textView.setText(producto.get("cantidad").toString() + producto.get("producto").toString());
            linearLayout.addView(textView);
        }

        return v;
    }
}

