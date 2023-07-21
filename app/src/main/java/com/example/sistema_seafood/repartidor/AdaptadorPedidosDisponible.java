package com.example.sistema_seafood.repartidor;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.sistema_seafood.Categoria;
import com.example.sistema_seafood.Pedido;
import com.example.sistema_seafood.R;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdaptadorPedidosDisponible extends BaseAdapter {
    public Context context;
    private List<PedidoRepartidor> pedidos;

    public PedidoRepartidor getCategoria(int position){
        return pedidos.get(position);
    }

    public AdaptadorPedidosDisponible(Context context){
        this.context=context;
        this.pedidos =new ArrayList<>();
    }

    public void add(PedidoRepartidor pedido){
        pedidos.add(pedido);
        this.notifyDataSetChanged();
    }

    public void remove(DocumentReference documentReference){
        for(PedidoRepartidor pedidoRepartidor:pedidos){
            if (pedidoRepartidor.getDocumentReference().equals(documentReference)){
                pedidos.remove(pedidoRepartidor);
                this.notifyDataSetChanged();
                return;
            }
        }
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
        v= layoutInflater.inflate(R.layout.card_pedido,null);
        TextView nombreCliente=v.findViewById(R.id.nombreCliente);
        TextView direccionCliente=v.findViewById(R.id.direccionCliente);
        Button btnAceptar=v.findViewById(R.id.btnAceptarPedido);
        PedidoRepartidor aux= pedidos.get(i);
        nombreCliente.setText(aux.getCliente());
        direccionCliente.setText(aux.getDireccion());
        LinearLayout linearLayou=v.findViewById(R.id.listaProductos);
        for (Map producto:aux.getProductos()){
            TextView textView=new TextView(context);
            textView.setText(producto.get("cantidad")+" "+producto.get("producto"));
            linearLayou.addView(textView);
        }
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aux.aceptar(((HomeRepartidor)context).getRepartidor().getNombre());
                ((HomeRepartidor)context).showEnvio(aux);
            }
        });

        return v;
    }
}
