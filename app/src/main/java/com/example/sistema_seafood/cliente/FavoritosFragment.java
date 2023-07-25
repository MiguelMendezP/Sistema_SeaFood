package com.example.sistema_seafood.cliente;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sistema_seafood.Platillo;
import com.example.sistema_seafood.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoritosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritosFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;

    private LinearLayout productosFav;

    public FavoritosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoritosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoritosFragment newInstance(String param1, String param2) {
        FavoritosFragment fragment = new FavoritosFragment();
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
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                HomeCliente.navController.popBackStack(R.id.nav_home,false);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((HomeCliente)getActivity()).setTitulo("Favoritos");
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_favoritos, container, false);
        productosFav=view.findViewById(R.id.listProductosFavoritos);
        for(String platilloFav:HomeCliente.getCliente().getPlatillosFav()){
            Platillo aux=getPlatillo(platilloFav);
            LayoutInflater layoutInflater=LayoutInflater.from(getContext());
            View v=layoutInflater.inflate(R.layout.card,null);
            ImageView imageView=v.findViewById(R.id.imgCategoria);
            imageView.setImageBitmap(aux.getImagen());
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
                        mMesa.setText(aux.getNombre());
                        productosFav.addView(v);
        }
        return view;
    }

    public Platillo getPlatillo(String platillo){
        for(Platillo plat:HomeCliente.platillos){
            if(plat.getNombre().equalsIgnoreCase(platillo)){
                return plat;
            }
        }
        return null;
    }
}