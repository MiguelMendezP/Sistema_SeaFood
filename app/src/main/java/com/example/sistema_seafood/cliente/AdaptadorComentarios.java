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

import com.example.sistema_seafood.Categoria;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.Valoracion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdaptadorComentarios extends BaseAdapter {
    public Context context;
    private List<Valoracion> valoracions;

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");




    public AdaptadorComentarios(Context context, List<Valoracion> valoraciones){
        this.context=context;
        this.valoracions=valoraciones;
    }

    @Override
    public int getCount() {
        return valoracions.size();
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
        v= layoutInflater.inflate(R.layout.card_comentario,null);
        Valoracion aux= valoracions.get(i);
        LinearLayout linearLayout=v.findViewById(R.id.estrellas);
        for (int j=0;j<aux.getPuntuacion();j++){
            linearLayout.getChildAt(j).setVisibility(View.VISIBLE);
        }
        ((TextView)v.findViewById(R.id.nombreUsuario)).setText(aux.getUsuario());
        ((TextView)v.findViewById(R.id.comentario)).setText(aux.getComentario());
        ((TextView)v.findViewById(R.id.fechaComentario)).setText(sdf.format(aux.getFecha()));
        return v;
    }
}
