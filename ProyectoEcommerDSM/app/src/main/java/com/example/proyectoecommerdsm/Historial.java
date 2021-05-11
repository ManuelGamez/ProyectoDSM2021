package com.example.proyectoecommerdsm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class Historial extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    //Para tomar la informacion del correo
    private GoogleApiClient googleApiClient;
    private StorageReference mStorage;
    // Realizamos una referencia a la base de datos
    DatabaseReference mRootReference;
    public String IDART="";
    private ImageView ArticuloFoto;
    private TextView IDarti;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);
        ArticuloFoto    =   (ImageView)findViewById(R.id.imageView5);
        IDarti          =   (TextView)findViewById(R.id.textView10);
        //IDART+="Ariculos"+'\n';
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
        mRootReference.child("Historial").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 :snapshot.getChildren())
                {
                    SacandoHistorial history  =   snapshot1.getValue(SacandoHistorial.class);
                    int ID                  =   history.getID();
                    IDART   += "Buenas Chele"+'\n';

                    //String ArticuloNombre   =   articulos.getArticulos();
                    //String Imagen           =   articulos.getImagen();

                    //Glide.with(Historial.this).load(Imagen).fitCenter().centerCrop().into(ArticuloFoto);
                }
                IDarti.setText(IDART);
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}