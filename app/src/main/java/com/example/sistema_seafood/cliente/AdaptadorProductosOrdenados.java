package com.example.sistema_seafood.cliente;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.sistema_seafood.ProductoOrdenado;
import com.example.sistema_seafood.R;

import java.util.List;

public class AdaptadorProductosOrdenados extends BaseAdapter {
    public Context context;
    public List<ProductoOrdenado> productoOrdenados;

    public ProductoOrdenado getProductoOrdenado(int position){
        return productoOrdenados.get(position);
    }

    public AdaptadorProductosOrdenados(Context context){
        this.context=context;
        this.productoOrdenados = HomeCliente.getCarrito().getProductoOrdenados();
    }

    public void actualizar(){
        this.productoOrdenados = HomeCliente.getCarrito().getProductoOrdenados();
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return productoOrdenados.size();
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
        v= layoutInflater.inflate(R.layout.car_producto_ordenado,null);
        ProductoOrdenado aux= productoOrdenados.get(i);
        ((ImageView)v.findViewById(R.id.imgProducto)).setImageBitmap(aux.getProducto().getImagen());
        ((TextView)v.findViewById(R.id.nombreProducto)).setText(aux.getProducto().getNombre());
        TextView cantidad=v.findViewById(R.id.cantProductoOrd);
        cantidad.setText(aux.getCantidad()+"");
        TextView subtotal=v.findViewById(R.id.subtotal);
        subtotal.setText("$"+aux.getSubtotal());
        ((Button)v.findViewById(R.id.btnadd)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    aux.setCantidad(aux.getCantidad()+1);
                    cantidad.setText(aux.getCantidad()+"");
subtotal.setText("$ "+aux.getSubtotal());
CarritoFragment.actualizar();
            }
        });

        ((Button)v.findViewById(R.id.btnremove)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
if(aux.getCantidad()==1){}
else {
    aux.setCantidad(aux.getCantidad()-1);
    cantidad.setText(aux.getCantidad()+"");
    subtotal.setText("$ "+aux.getSubtotal());
    CarritoFragment.actualizar();
}
            }
        });

        ((ImageButton)v.findViewById(R.id.btnEliminar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productoOrdenados.remove(aux);
                notifyDataSetChanged();
                CarritoFragment.actualizar();
            }
        });
        return v;
    }
}
