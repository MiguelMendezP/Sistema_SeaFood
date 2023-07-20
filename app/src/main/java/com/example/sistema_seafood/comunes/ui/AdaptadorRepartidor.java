package com.example.sistema_seafood.comunes.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sistema_seafood.Extra;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.Repartidor;
import com.example.sistema_seafood.comunes.AdapterExtras;

import java.util.ArrayList;

public class AdaptadorRepartidor extends RecyclerView.Adapter<AdaptadorRepartidor.MyViewHolder> {

    Context context;
    ArrayList<Repartidor> repartidores;

    public AdaptadorRepartidor(Context context, ArrayList<Repartidor> repartidores) {
        this.context = context;
        this.repartidores = repartidores;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_repartidor, parent, false);

        return new AdaptadorRepartidor.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorRepartidor.MyViewHolder holder, int position) {
        Repartidor repartidor = repartidores.get(position);

        holder.nombreRepartidor.setText(repartidor.getNombre());
        holder.correoRepartidor.setText(repartidor.getCorreo());
        holder.iconRepartidor.setImageBitmap(repartidor.getImagen());
    }

    @Override
    public int getItemCount() {
        return repartidores.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView nombreRepartidor, correoRepartidor;
        ImageView iconRepartidor;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            nombreRepartidor = itemView.findViewById(R.id.nombreRepartidor);
            correoRepartidor = itemView.findViewById(R.id.correoRepartidor);

            iconRepartidor = itemView.findViewById(R.id.iconRepartidor);

        }
    }
}
