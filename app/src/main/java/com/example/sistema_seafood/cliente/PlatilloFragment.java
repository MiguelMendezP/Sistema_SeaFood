package com.example.sistema_seafood.cliente;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sistema_seafood.Platillo;
import com.example.sistema_seafood.ProductoOrdenado;
import com.example.sistema_seafood.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlatilloFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlatilloFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;

    public static Platillo platillo;

    private ImageView imageView;
    private GridView contenedorComentarios;
    private AdaptadorComentarios adaptadorComentarios;

    private Button btnMas,btnMenos;

    private ImageButton addCarrito;

    private TextView cantidad;

    private int cant=1;


    public PlatilloFragment() {
        // Required empty public constructor
    }

    public static void setPlatillo(Platillo platillo){
        PlatilloFragment.platillo =platillo;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlatilloFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlatilloFragment newInstance(String param1, String param2) {
        PlatilloFragment fragment = new PlatilloFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(HomeCliente.platillosFavoritos==null){
            HomeCliente.consultarFavoritos();
        }
        view= inflater.inflate(R.layout.fragment_platillo, container, false);
        imageView=view.findViewById(R.id.imgPlatillo);
        imageView.setImageBitmap(platillo.getImagen());
        CheckBox favorito=view.findViewById(R.id.checkFavorito);
        if(HomeCliente.platillosFavoritos.contains(platillo)){
            favorito.setChecked(true);
        }
        ((TextView)view.findViewById(R.id.nombre)).setText(platillo.getNombre());
        ((TextView)view.findViewById(R.id.puntuacion)).setText(platillo.getPuntuacion()+"");
        contenedorComentarios =view.findViewById(R.id.contenedorComentarios);
        adaptadorComentarios=new AdaptadorComentarios(getContext(), platillo.getValoraciones());
        contenedorComentarios.setAdapter(adaptadorComentarios);
        ((TextView)view.findViewById(R.id.precio)).setText("$ "+platillo.getPrecio()+"");
        cantidad=view.findViewById(R.id.cantidad);
        cantidad.setText("1");
        ((TextView)view.findViewById(R.id.descripcion)).setText(platillo.getDescripcion());
favorito.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            HomeCliente.cliente.getPlatillosFav().add(platillo.getNombre());
            HomeCliente.cliente.getDocumentReference().update("favoritos",HomeCliente.cliente.getPlatillosFav());
            HomeCliente.platillosFavoritos.add(platillo);
            Toast.makeText(getContext(),"Platillo añadido a favoritos",Toast.LENGTH_SHORT).show();
        }else {
            HomeCliente.cliente.getPlatillosFav().remove(platillo.getNombre());
            HomeCliente.cliente.getDocumentReference().update("favoritos",HomeCliente.cliente.getPlatillosFav());
            HomeCliente.platillosFavoritos.remove(platillo);
            Toast.makeText(getContext(),"Platillo eliminado de favoritos",Toast.LENGTH_SHORT).show();
        }
    }
});
        btnMenos=view.findViewById(R.id.btnMenos);
        btnMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cant>1){
                    cant--;
                    cantidad.setText(cant +"");
                }
            }
        });



        btnMas=view.findViewById(R.id.btnMas);
        btnMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cant++;
                    cantidad.setText(cant +"");
            }
        });

        addCarrito=view.findViewById(R.id.addCarrito);
        addCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeCliente.getCarrito().add(new ProductoOrdenado(platillo,cant));
                cant=1;
                Toast.makeText(getContext(),"Producto añadido al carrito",Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}