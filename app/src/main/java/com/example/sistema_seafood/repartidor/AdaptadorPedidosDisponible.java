package com.example.sistema_seafood.repartidor;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.sistema_seafood.Categoria;
import com.example.sistema_seafood.Pedido;
import com.example.sistema_seafood.R;

import java.util.ArrayList;
import java.util.List;

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
        direccionCliente.setText(aux.getUbicacion().toString());
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeRepartidor)context).showEnvio(aux);
            }
        });

        return v;
    }
}
