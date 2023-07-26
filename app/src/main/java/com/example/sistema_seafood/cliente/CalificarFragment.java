package com.example.sistema_seafood.cliente;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sistema_seafood.Categoria;
import com.example.sistema_seafood.Platillo;
import com.example.sistema_seafood.Producto;
import com.example.sistema_seafood.R;
import com.example.sistema_seafood.Valoracion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalificarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalificarFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;
    private LinearLayout linearLayout;

    public static List<Producto> productos;

    private int calificacion=0;

    public CalificarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalificarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalificarFragment newInstance(String param1, String param2) {
        CalificarFragment fragment = new CalificarFragment();
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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_calificar, container, false);
        linearLayout=view.findViewById(R.id.contenedorProductosCalf);
        for (Producto producto:productos){
            if(producto instanceof Platillo){
                View card=inflater.inflate(R.layout.card_valoracion,null);
                ((ImageView)card.findViewById(R.id.imgValoracion)).setImageBitmap(producto.getImagen());
                ((TextView)card.findViewById(R.id.tituloProducto)).setText(producto.getNombre());
                linearLayout.addView(card);

                ArrayList<ImageButton> list=new ArrayList<>();
                list.add((ImageButton) card.findViewById(R.id.starV1));
                list.add((ImageButton) card.findViewById(R.id.starV2));
                list.add((ImageButton) card.findViewById(R.id.starV3));
                list.add((ImageButton) card.findViewById(R.id.starV4));
                list.add((ImageButton) card.findViewById(R.id.starV5));
                ColorFilter colorFilter = new PorterDuffColorFilter(getResources().getColor(R.color.amarillo), PorterDuff.Mode.SRC_IN);
                ColorFilter colorFilter1 = new PorterDuffColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
                EditText comentario=card.findViewById(R.id.editComentario);
                list.get(0).setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onClick(View v) {
                        list.get(0).setColorFilter(colorFilter);
                        list.get(1).setColorFilter(colorFilter1);
                        list.get(2).setColorFilter(colorFilter1);
                        list.get(3).setColorFilter(colorFilter1);
                        list.get(4).setColorFilter(colorFilter1);
                        calificacion=1;
                    }
                });

                list.get(1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list.get(0).setColorFilter(colorFilter);
                        list.get(1).setColorFilter(colorFilter);
                        list.get(2).setColorFilter(colorFilter1);
                        list.get(3).setColorFilter(colorFilter1);
                        list.get(4).setColorFilter(colorFilter1);
                        calificacion=2;
                    }
                });
                list.get(2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list.get(0).setColorFilter(colorFilter);
                        list.get(1).setColorFilter(colorFilter);
                        list.get(2).setColorFilter(colorFilter);
                        list.get(3).setColorFilter(colorFilter1);
                        list.get(4).setColorFilter(colorFilter1);
                        calificacion=3;
                    }
                });
                list.get(3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list.get(0).setColorFilter(colorFilter);
                        list.get(1).setColorFilter(colorFilter);
                        list.get(2).setColorFilter(colorFilter);
                        list.get(3).setColorFilter(colorFilter);
                        list.get(4).setColorFilter(colorFilter1);
                        calificacion=4;
                    }
                });
                list.get(4).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list.get(0).setColorFilter(colorFilter);
                        list.get(1).setColorFilter(colorFilter);
                        list.get(2).setColorFilter(colorFilter);
                        list.get(3).setColorFilter(colorFilter);
                        list.get(4).setColorFilter(colorFilter);
                        calificacion=5;
                    }
                });

                ((ImageView)card.findViewById(R.id.btnComentar)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Categoria categoria=HomeCliente.getCategoria(((Platillo) producto).getCategoria());
                        if(categoria!=null){
                            ((Platillo) producto).getValoraciones().add(new Valoracion(HomeCliente.cliente.getNombre(),"nuevo comentario",3,new Date()));
                            categoria.getDocumentReference().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    List<Map> lista=(List<Map>) task.getResult().get("platillos");
                                    for (Map map:lista){
                                        if(map.get("nombre").equals(producto.getNombre())){
                                            List<Map> valoraciones= (List<Map> )map.get("valoraciones");
                                            Map map1=new HashMap();
                                            map1.put("comentario",comentario.getText().toString());
                                            map1.put("fecha",new Date());
                                            map1.put("puntuacion",calificacion);
                                            map1.put("usuario",HomeCliente.cliente.getNombre());
                                            ((Platillo) producto).getValoraciones().add(new Valoracion(HomeCliente.cliente.getNombre(),comentario.getText().toString(),calificacion,new Date()));
                                            valoraciones.add(map1);
                                            task.getResult().getReference().update("platillos",lista);
                                            Toast.makeText(getContext(),"Su ha registrado su calificación",Toast.LENGTH_SHORT).show();
                                            comentario.setEnabled(false);
                                            break;
                                        }
                                    }
                                }
                            });
                        }

                    }
                });

            }
            }
        return view;
    }
}