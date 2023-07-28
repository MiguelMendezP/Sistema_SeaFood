package com.example.sistema_seafood.cliente;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.sistema_seafood.ProductoOrdenado;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.Valoracion;

import java.util.List;

public class AdaptadorProductosConfirmar extends BaseAdapter {

public Context context;
private List<ProductoOrdenado> productos;

public AdaptadorProductosConfirmar(Context context, List<ProductoOrdenado> productos){
        this.context=context;
        this.productos =productos;
        }

@Override
public int getCount() {
        return productos.size();
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
        v= layoutInflater.inflate(R.layout.card_pedido_confirmado,null);
        ProductoOrdenado aux= productos.get(i);
    ((ImageView)v.findViewById(R.id.imgProductoConfirmado)).setImageBitmap(aux.getProducto().getImagen());
    ((TextView)v.findViewById(R.id.nombreProductoConfirmado)).setText(aux.getProducto().getNombre());
    ((TextView)v.findViewById(R.id.cantidadProductoConfirmado)).setText("x"+aux.getCantidad());
    ((TextView)v.findViewById(R.id.subtotalProductoConfirmado)).setText("$ " +aux.getSubtotal());
        return v;
        }
        }