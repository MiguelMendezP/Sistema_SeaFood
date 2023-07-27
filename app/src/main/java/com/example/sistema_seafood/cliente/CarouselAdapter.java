package com.example.sistema_seafood.cliente;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.sistema_seafood.Platillo;
import com.example.sistema_seafood.R;

import java.util.List;

public class CarouselAdapter extends PagerAdapter {

    private Context context;
    private List<Platillo> platillos; // Array de imágenes para el carrusel

    public CarouselAdapter(Context context, List<Platillo> platillos) {
        this.context = context;
        this.platillos = platillos;
    }

    public void remove(int i){
        platillos.remove(i);
        this.notifyDataSetChanged();
    }

    public Platillo getPlatillo(int position){
        return platillos.get(position);
    }

    @Override
    public int getCount() {
        return platillos.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_favorito, container, false);
        Platillo aux= platillos.get(position);
        ImageView imageView=view.findViewById(R.id.imgValoracion);
        TextView title=view.findViewById(R.id.tituloCategoria);
        title.setText(aux.getNombre());
        imageView.setImageBitmap(aux.getImagen());
        ((TextView)view.findViewById(R.id.valoracion)).setText(""+aux.getPuntuacion());
        ((TextView)view.findViewById(R.id.precioPlatillo)).setText("$ "+aux.getPrecio());
        CheckBox checkBox=view.findViewById(R.id.checkFavorite);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    AlertDialog.Builder alert=new AlertDialog.Builder(context);
                    alert.setMessage("¿Desea remover el platillo de su lista de favoritos?")
                            .setCancelable(true)
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    checkBox.setChecked(true);
                                }
                            })
                            .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //HomeCliente.platillosFavoritos.remove(aux);
                                    FavoritosFragment.removeView(position);
                                    HomeCliente.cliente.getPlatillosFav().remove(aux.getNombre());
                                    HomeCliente.platillosFavoritos.remove(aux);
                                    HomeCliente.cliente.getDocumentReference().update("favoritos",HomeCliente.cliente.getPlatillosFav());
                                    Toast.makeText(context,"El platillo se ha eliminado de favoritos",Toast.LENGTH_SHORT).show();
                                }
                            });
                    AlertDialog titulo=alert.create();
                    titulo.setTitle("Eliminar Platillo");
                    titulo.show();
                }
                checkBox.setChecked(true);
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
