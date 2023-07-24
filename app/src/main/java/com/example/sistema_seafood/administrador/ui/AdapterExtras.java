package com.example.sistema_seafood.administrador.ui;

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

import java.util.ArrayList;

public class AdapterExtras extends RecyclerView.Adapter<AdapterExtras.MyViewHolder> {

    Context context;
    ArrayList<Extra> extras;

    public AdapterExtras(Context context, ArrayList<Extra> extras) {
        this.context = context;
        this.extras = extras;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_extras,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterExtras.MyViewHolder holder, int position) {
        Extra extra = extras.get(position);

        holder.tv_nombre.setText(extra.getNombre());
        holder.tv_precio.setText("Precio: $"+extra.getPrecio());
        holder.tv_descripcion.setText(extra.getDescripcion());
    }

    @Override
    public int getItemCount() {
        return extras.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView imageButtonEliminar, tv_nombre, tv_precio, tv_descripcion;
        ImageView imageButtonEditar;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            tv_nombre = itemView.findViewById(R.id.tv_nombre);
            tv_precio = itemView.findViewById(R.id.tv_precio);
            tv_descripcion = itemView.findViewById(R.id.tv_descripcion);

            imageButtonEliminar = itemView.findViewById(R.id.imageButtonEliminar);
            imageButtonEditar = itemView.findViewById(R.id.imageButtonEditar);

        }
    }
}