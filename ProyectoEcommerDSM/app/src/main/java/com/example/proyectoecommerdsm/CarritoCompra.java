package com.example.proyectoecommerdsm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarritoCompra extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    // Realizamos una referencia a la base de datos
    DatabaseReference mRootReference;
    //Para tomar la informacion del correo
    private GoogleApiClient googleApiClient;
    private int IDARTICULO=0;
    private ImageView FotoArticulo2,ComprarArt,FotoCorreo;
    private TextView Articulo,Descripcions,precio;
    private Spinner opciones ;
    private String IDUSUARIO,Foto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito_compra);
        FotoArticulo2   =   (ImageView)findViewById(R.id.imageView);
        Articulo        =   (TextView)findViewById(R.id.textView2);
        Descripcions    =   (TextView)findViewById(R.id.textView3);
        precio          =   (TextView)findViewById(R.id.textView4);
        opciones        =   (Spinner) findViewById(R.id.spinner);
        FotoCorreo      =   (ImageView)findViewById(R.id.imageView3);
        //:::::::::::::::::::: CONEXION CON GOOGLE ::::::::::::::::::::::::::::::::::::::::::::::
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
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
                               Foto             =   articulos.getImagen();
                        String Cantidad         =   articulos.getCantidad();
                        //Toast.makeText(CarritoCompra.this,Cantidad,Toast.LENGTH_SHORT).show();
                        List<String> lista = new ArrayList<String>();
                        for(int i=0; i<=Integer.parseInt(Cantidad);i++)
                        {
                            lista.add(Integer.toString(i));
                            ArrayAdapter<String> adapter =  new ArrayAdapter<String>(CarritoCompra.this, android.R.layout.simple_spinner_item,lista);
                            opciones.setAdapter(adapter);
                        }
                        IDARTICULO              =   bundle.getInt("ID");
                        Articulo.setText(ArticuloNombre);
                        Descripcions.setText(Descripcion);
                        precio.setText(Precio);
                        Glide.with(CarritoCompra.this)
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
    public void AgregarProductos(View view) {
        Intent intent   = new Intent(this,IngresarArticulo.class);
        startActivity(intent);
    }

    public void Inicio(View view) {
        Intent intent   = new Intent(this,home.class);
        startActivity(intent);
    }
    //::::::::::::::::::::: fUNCION PARA TOMAR LA INFORMACION DE LA CUENTA ::::::::::::::::::::::::::::::::
    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr= Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if(opr.isDone())
        {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        }
        else
        {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }
    private void handleSignInResult(GoogleSignInResult result) {
        if(result.isSuccess())
        {
            GoogleSignInAccount account = result.getSignInAccount();
           // nameText.setText(account.getDisplayName());
            //emailTextView.setText(account.getEmail());
            IDUSUARIO= account.getId();
            //idGoogle.setText(account.getId());
            try{
                Glide.with(this).load(account.getPhotoUrl()).into(FotoCorreo);
            }catch (NullPointerException e){
                Toast.makeText(getApplicationContext(),"image no encontrada", Toast.LENGTH_LONG).show();
            }
            //Glide.with(this).load(account.getPhotoUrl()).into(photoImagenView);
            //Log.d("MIAPP",account.getPhotoUrl().toString());
        }
        else {
            //  goLogInScreen();
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void ComprarH(View view) {
        Map<String, Object> datosUsuario   =   new HashMap<>();
        datosUsuario.put("Articulos",Articulo.toString());
        datosUsuario.put("IDUSER",IDUSUARIO.toString());
        datosUsuario.put("Precio",precio.toString());
        datosUsuario.put("Cantidad",opciones.getSelectedItem().toString());
        datosUsuario.put("Imagen",Foto.toString());
        datosUsuario.put("ID",IDARTICULO);
        mRootReference.child("Historial").push().setValue(datosUsuario);
        Toast toast = Toast.makeText(getApplicationContext(), "Datos guardados exitosamente!", Toast.LENGTH_LONG);
        toast.show();
    }
}