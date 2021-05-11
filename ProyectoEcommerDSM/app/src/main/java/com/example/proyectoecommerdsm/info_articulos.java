package com.example.proyectoecommerdsm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class info_articulos extends AppCompatActivity {
    // Realizamos una referencia a la base de datos
    DatabaseReference mRootReference;
    private ImageView FotoArticulo2,ComprarArt;
    private TextView Articulo,Descripcions,precio;
    private int IDARTICULO=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_articulos);
        FotoArticulo2   =   (ImageView)findViewById(R.id.imageView2);
        Articulo        =   (TextView)findViewById(R.id.tvNombreProducto);
        Descripcions    =   (TextView)findViewById(R.id.tvDescripcion);
        precio          =   (TextView)findViewById(R.id.tvPrecio2);
        ComprarArt      =   (ImageView)findViewById(R.id.ComprarcOMPRAR);
        ComprarArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AgregarCarrito();
            }
        });
        /*Bundle : Los paquetes se utilizan generalmente para pasar datos entre varias actividades de Android*/
        final Bundle bundle = getIntent().getExtras();//creando un objeto bundle para traer los datos de la actividad mendiante Intent
        //Esto es para referenciar al nodo principal
        mRootReference  = FirebaseDatabase.getInstance().getReference();
        mRootReference.child("Articulos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 :snapshot.getChildren())
                {
                    pojo articulos  =   snapshot1.getValue(pojo.class);
                    if(bundle.getInt("ID") == articulos.getID() )
                    {
                        String ArticuloNombre   =   articulos.getArticulos();
                        String Imagen           =   articulos.getImagen();
                        String Descripcion      =   articulos.getDescripcion();
                        String Precio           =   articulos.getPrecio();
                        IDARTICULO              =   bundle.getInt("ID");
                        Articulo.setText(ArticuloNombre);
                        Descripcions.setText(Descripcion);
                        precio.setText(Precio);
                        Glide.with(info_articulos.this)
                                .load(Imagen).fitCenter().centerCrop().into(FotoArticulo2);
                        Log.e("Datos",""+snapshot1.getValue());
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void AgregarCarrito ()
    {
        Bundle bundle = new Bundle();
        bundle.putInt("ID",IDARTICULO);
        Intent intent = new Intent(this,CarritoCompra.class);
        intent.putExtras(bundle);
        startActivity(intent);
        //Toast.makeText(this,"ID = "+IDARTICULO,Toast.LENGTH_SHORT).show();
    }
}