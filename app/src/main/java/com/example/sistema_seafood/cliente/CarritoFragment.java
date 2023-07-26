package com.example.sistema_seafood.cliente;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.sistema_seafood.ProductoOrdenado;
import com.example.sistema_seafood.R;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CarritoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarritoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
private Spinner spinner;

public static TextView subtotal,total;
private View view;
    private GridView contenedorProductosOrdenados;

    private boolean isSpinnerOpened = false;

    private boolean first=true;
    private AdaptadorProductosOrdenados adaptadorProductosOrdenados;
    public CarritoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CarritoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CarritoFragment newInstance(String param1, String param2) {
        CarritoFragment fragment = new CarritoFragment();
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
        view= inflater.inflate(R.layout.fragment_carrito, container, false);
        HomeCliente.setTitulo("Carrito");
        spinner=view.findViewById(R.id.spinner);
        contenedorProductosOrdenados =view.findViewById(R.id.contenedorProductosOrd);
        adaptadorProductosOrdenados=new AdaptadorProductosOrdenados(getContext());
        contenedorProductosOrdenados.setAdapter(adaptadorProductosOrdenados);
        ArrayAdapter adapter= new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, HomeCliente.extras);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isSpinnerOpened = true;
                return false;
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               if(isSpinnerOpened){
                   HomeCliente.getCarrito().add(new ProductoOrdenado(HomeCliente.extras.get(position),1));
                   adaptadorProductosOrdenados.notifyDataSetChanged();
                   CarritoFragment.actualizar();
               }
               else {
                   // Marcar que el Spinner ya se ha cargado por primera vez
                   isSpinnerOpened= false;
               }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ((TextView)view.findViewById(R.id.envio)).setText("30.0");
        total=((TextView) view.findViewById(R.id.total));
        subtotal=((TextView) view.findViewById(R.id.subtotalCarrito));
        total.setText((HomeCliente.getCarrito().getTotal()+30)+"");
        subtotal.setText(HomeCliente.getCarrito().getTotal()+"");

        ((Button)view.findViewById(R.id.btnPedir)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.nav_confirmar);
            }
        });
        return view;
    }

    public static void actualizar(){
        subtotal.setText(HomeCliente.getCarrito().getTotal()+"");
        total.setText(HomeCliente.getCarrito().getTotal()+30 +"");
    }
}