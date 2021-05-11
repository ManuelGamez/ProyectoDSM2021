package com.example.proyectoecommerdsm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class home extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    //Para tomar la informacion del correo
    private GoogleApiClient googleApiClient;
    private StorageReference mStorage;
    // Realizamos una referencia a la base de datos
    DatabaseReference mRootReference;
    //Componentes :::::::::::::::::::::::::::::::
    private ImageButton botonimagen;
    private LinearLayout layout,contenedorBoton;
    private ImageView fotoperfil;
    private TextView nameText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //::::::::::: Enlazando Componentes:::::::::::::::::::::::::::::
        fotoperfil  =   (ImageView)findViewById(R.id.FotoCorreo);
        nameText    =   (TextView)findViewById(R.id.nombreCorreo);
        layout      =   (LinearLayout)findViewById(R.id.llBotonera);
        //:::::::::::::::::::: CONEXION CON GOOGLE ::::::::::::::::::::::::::::::::::::::::::::::
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        mRootReference  = FirebaseDatabase.getInstance().getReference();
        mRootReference.child("Articulos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 :snapshot.getChildren())
                {
                    pojo articulos  =   snapshot1.getValue(pojo.class);
                    int ID                  =   articulos.getID();
                    String ArticuloNombre   =   articulos.getArticulos();
                    String Imagen           =   articulos.getImagen();
                    //Creando Botones Dinamicos :::::::::::::::::::::::::::
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(546, 523);
                    botonimagen = new ImageButton(home.this);
                    //Asignamos propiedades de layout al boton
                    botonimagen.setLayoutParams(lp);
                    botonimagen.setMaxWidth(746);
                    botonimagen.setMaxHeight(766);
                    botonimagen.setId(ID);
                    botonimagen.setOnClickListener(misEventos);
                    //Asignamos Texto al botón
                    //boton.setText("Boton "+String.format("%02d", i ));
                    //Añadimos el botón a la botonera
                    layout.addView(botonimagen);
                    Glide.with(home.this)
                            .load(Imagen).fitCenter().centerCrop().into(botonimagen);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public View.OnClickListener misEventos = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putInt("ID",view.getId());
            Intent intent = new Intent(home.this, info_articulos.class);
            intent.putExtras(bundle);
            startActivity(intent);
            //Toast.makeText(home.this,"Hola Mundo"+ view.getId(),Toast.LENGTH_SHORT).show();
        }
    };

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
            nameText.setText(account.getDisplayName());
            //emailTextView.setText(account.getEmail());
            //account.getId()
            //idGoogle.setText(account.getId());
            try{
                Glide.with(this).load(account.getPhotoUrl()).into(fotoperfil);
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

    public void AgregarProductos(View view) {
        Intent intent   = new Intent(this,IngresarArticulo.class);
        startActivity(intent);
    }

    public void Inicio(View view) {
        Intent intent   = new Intent(this,home.class);
        startActivity(intent);
    }

    public void Historial(View view) {
        Intent intent   =   new Intent(this,Historial.class);
        startActivity(intent);
    }
}