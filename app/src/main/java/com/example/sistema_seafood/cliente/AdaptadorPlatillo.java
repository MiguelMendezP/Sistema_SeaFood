package com.example.sistema_seafood.cliente;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.sistema_seafood.Categoria;
import com.example.sistema_seafood.Platillo;
import com.example.sistema_seafood.R;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorPlatillo extends BaseAdapter {
    public Context context;
    private List<Platillo> platillos;
    private List<View> vistas;

    private Categoria categoria;

    public Platillo getPlatillo(int position){
        return platillos.get(position);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Platillo getPlatillo(String nombre){
        for (Platillo mesa:platillos){
            if(mesa.getNombre().equals(nombre)){
                return mesa;
            }
        }
        return null;
    }


    public AdaptadorPlatillo(Context context, Categoria categoria){
        this.context=context;
        this.platillos=categoria.getPlatillos();
        this.categoria=categoria;
        this.vistas=new ArrayList<>();
    }

    public void add(Platillo mesa){
        platillos.add(mesa);
        this.notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return platillos.size();
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
        v= layoutInflater.inflate(R.layout.card,null);
        Platillo aux=platillos.get(i);
        ImageView imageView=v.findViewById(R.id.imgCategoria);
        TextView mMesa=v.findViewById(R.id.titular);
        TextView mPuntuacion=v.findViewById(R.id.valoracion);
        mPuntuacion.setText(aux.getPuntuacion()+"");
        mPuntuacion.setVisibility(View.VISIBLE);
        TextView descuento=v.findViewById(R.id.showDescuento);
        if(aux.getDescuento()>0){
            descuento.setText(aux.getDescuento()+"%");
            descuento.setVisibility(View.VISIBLE);
        }
        v.findViewById(R.id.star).setVisibility(View.VISIBLE);
        aux.setImagen(imageView,categoria.getNombre());
        mMesa.setText(aux.getNombre());
        vistas.add(v);
        return v;
    }
}
