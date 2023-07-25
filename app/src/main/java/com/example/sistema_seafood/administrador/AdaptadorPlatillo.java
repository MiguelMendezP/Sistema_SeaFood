package com.example.sistema_seafood.administrador;

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
import com.example.sistema_seafood.models.Platillo;
import com.example.sistema_seafood.R;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorPlatillo extends BaseAdapter {
    public Context context;
    private static List<Platillo> platillos;

    private Categoria categoria;

    public static Platillo getPlatillo(String nombre){
        for (Platillo platillo:platillos){
            if(platillo.getNombre().equals(nombre)){
                return platillo;
            }
        }
        return null;
    }

    public Platillo getPlatillo(int position){
        return platillos.get(position);
    }


    public AdaptadorPlatillo(Context context){
        this.context=context;
        this.platillos= new ArrayList<>();
        this.categoria = categoria;
    }

    public void add(Platillo platillo){
        platillos.add(platillo);
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
        v= layoutInflater.inflate(R.layout.card_categoria,null);
        Platillo aux = platillos.get(i);
        ImageView imageView = v.findViewById(R.id.imgValoracion);
        TextView title=v.findViewById(R.id.tituloCategoria);
        aux.mostrarImagen(imageView);
        title.setText(aux.getNombre());
        return v;
    }
}
