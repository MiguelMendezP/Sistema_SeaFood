package com.example.sistema_seafood.cliente;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.constraintlayout.helper.widget.Carousel;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sistema_seafood.Platillo;
import com.example.sistema_seafood.Producto;
import com.example.sistema_seafood.ProductoOrdenado;
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
    private Carousel carouselView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static CarouselAdapter adapter;
    private static ViewPager viewPager;


    private View view;

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
                HomeCliente.navController.popBackStack(R.id.nav_home, false);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((HomeCliente) getActivity()).setTitulo("Favoritos");
        // Inflate the layout for this fragment
        if(HomeCliente.platillosFavoritos==null){
            HomeCliente.consultarFavoritos();
        }
        view = inflater.inflate(R.layout.fragment_favoritos, container, false);
        ((Button) view.findViewById(R.id.btnComprar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeCliente.carrito.add(new ProductoOrdenado(adapter.getPlatillo(viewPager.getCurrentItem()), 1));
                Toast.makeText(getContext(), "El producto se ha añadido a su carrito", Toast.LENGTH_SHORT).show();
            }
        });

        viewPager = view.findViewById(R.id.viewPager);
        adapter = new CarouselAdapter(getContext(), HomeCliente.platillosFavoritos);
        viewPager.setAdapter(adapter);
        return view;
    }

    public static void removeView(int i) {
        adapter.remove(i);
        adapter.notifyDataSetChanged();
        viewPager.setAdapter(adapter);
    }
}

