package com.example.sistema_seafood.cliente;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.navigation.Navigation;

import com.example.sistema_seafood.Pedido;
import com.example.sistema_seafood.Producto;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.Utils;
import com.example.sistema_seafood.repartidor.PedidoRepartidor;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdaptadorPedidosRealizados extends BaseAdapter {
    public Context context;
    private List<PedidoRepartidor> pedidos;

    public PedidoRepartidor getCategoria(int position) {
        return pedidos.get(position);
    }

    public AdaptadorPedidosRealizados(Context context) {
        this.context = context;
        this.pedidos = new ArrayList<>();
    }

    public void add(PedidoRepartidor pedido) {
        pedidos.add(pedido);
        this.notifyDataSetChanged();
    }

    public void remove(DocumentReference documentReference) {
        for (PedidoRepartidor pedido : pedidos) {
            if (pedido.getDocumentReference().equals(documentReference)) {
                pedidos.remove(pedido);
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
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        v = layoutInflater.inflate(R.layout.card_pedido_cliente, null);

        TextView nombreCliente = v.findViewById(R.id.datePedido);
        TextView direccionCliente = v.findViewById(R.id.direccionPedido);
        PedidoRepartidor aux = pedidos.get(i);
        nombreCliente.setText(Utils.getDate(aux.getFecha()));
        direccionCliente.setText(aux.getDireccion());
        LinearLayout linearLayou = v.findViewById(R.id.listaProductosPedido);
        for (Map producto : aux.getProductos()) {
            TextView textView = new TextView(context);
            textView.setText(producto.get("cantidad") + " " + producto.get("producto"));
            linearLayou.addView(textView);
        }
        ((TextView)v.findViewById(R.id.textTotal)).setText("$ "+aux.getTotal());
        ((Button)v.findViewById(R.id.btnVolverPedir)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeCliente.volverPedir(aux.getProductos());
                Toast.makeText(context,"Productos añadidos al carrito",Toast.LENGTH_SHORT).show();
            }
        });

        ((Button)v.findViewById(R.id.btnCalificar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Producto> products=new ArrayList<>();
                for (Map map:aux.getProductos()){
                    products.add(HomeCliente.getProducto(map.get("producto").toString()));
                    Toast.makeText(context,map.get("producto").toString(),Toast.LENGTH_SHORT).show();
                }
                CalificarFragment.productos=products;
                HomeCliente.navController.navigate(R.id.nav_calificar);
            }
        });
        return v;
    }
}

