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
import com.example.sistema_seafood.R;
import java.util.ArrayList;
import java.util.List;

public class AdaptadorCategoria extends BaseAdapter {
    public Context context;
    private static List<Categoria> categorias;

    public static Categoria getCat(String name){
        for (Categoria categoria: categorias){
            if (categoria.getNombre().equals(name)){
                return categoria;
            }
        }
        return null;
    }


    public Categoria getCategoria(int position){
        return categorias.get(position);
    }

    public AdaptadorCategoria(Context context){
        this.context=context;
        this.categorias =new ArrayList<>();
    }

    public void add(Categoria categoria){
        categorias.add(categoria);
        this.notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return categorias.size();
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
        Categoria aux= categorias.get(i);
        ImageView imageView=v.findViewById(R.id.imgCategoria);
        TextView title=v.findViewById(R.id.titular);
        aux.setImg(imageView);
        title.setText(aux.getNombre());
        return v;
    }
}
