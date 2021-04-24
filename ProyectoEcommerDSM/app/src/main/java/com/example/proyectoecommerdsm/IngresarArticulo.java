package com.example.proyectoecommerdsm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class IngresarArticulo extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    //Para tomar la informacion del correo
    private GoogleApiClient googleApiClient;
    // Realizamos una referencia a la base de datos
    DatabaseReference mRootReference;
    // Realizamos la referencia al Storage
    private StorageReference mStorage;
    private static final int GALLERY_INTENT =1;//creamos un codigo
    //Componentes :::::::::::::::::::::::::::::::
    private ImageView fotoperfil;
    private TextView nameText;
    private EditText mEditTextArticulo,mEditTextDescripcion,mEditTextPrecio;
    private Button mGuardarDatos,SubirImagen;
    private ImageButton FotoArticulo;
    public String Articulo="",Descripcion="",Precio="",FotoImagen="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar_articulo);
        //::::::::::: Enlazando Componentes:::::::::::::::::::::::::::::
        fotoperfil  =   (ImageView)findViewById(R.id.FotoCorreo);
        nameText    =   (TextView)findViewById(R.id.nombreCorreo);
        mEditTextArticulo       =   (EditText)findViewById(R.id.editArticulo);
        mEditTextDescripcion    =   (EditText)findViewById(R.id.editDescripcion);
        mEditTextPrecio         =   (EditText)findViewById(R.id.editPrecio);
        mGuardarDatos           =   (Button)findViewById(R.id.registrarartbtn);
        SubirImagen             =   (Button)findViewById(R.id.subirfotobtn);
        FotoArticulo            =   (ImageButton) findViewById(R.id.Articuloimage);
        //:::::::::::::::::::: CONEXION CON GOOGLE ::::::::::::::::::::::::::::::::::::::::::::::
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        //Esto es para referenciar a la base de datos FIREBASE
        mRootReference  = FirebaseDatabase.getInstance().getReference();
        //Metodo para acceder a la galeria de mi cel :::::::..........
        referencenciaStoreFirebase();
        //Al darle click al boton guardar mandaria todo esto al metodo
        mGuardarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Articulo     =   mEditTextArticulo.getText().toString();
                Descripcion  =   mEditTextDescripcion.getText().toString();
                Precio       =   mEditTextPrecio.getText().toString();
                //Guardo todo en FIREBASE ::::::::::::::
                SubiendoDatos(Articulo, Descripcion, Precio,FotoImagen);
            }
        });
        //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    }
    private void referencenciaStoreFirebase() {
        //Referenciamos el Store de firebase
        mStorage    = FirebaseStorage.getInstance().getReference();
        SubirImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");//Para que agarre todos los formatos
                startActivityForResult(intent,GALLERY_INTENT);
            }
        });
    }
    //:::::::::::::::::::::::::: Metodo para subir la informacion a la BD :::::::::::::::::::::::::
    private void SubiendoDatos(String Articulo, String Descripcion, String Precio, String Foto) {
        //Creando ID randon
        Random rand = new Random();
        int n = rand.nextInt(1000000000); // Gives n such that 0 <= n < 1,000,000,000
        //Para cargar estos valores
        Map<String, Object> datosUsuario   =   new HashMap<>();
        datosUsuario.put("Articulos",Articulo);
        datosUsuario.put("Descripcion",Descripcion);
        datosUsuario.put("Precio",Precio);
        datosUsuario.put("Imagen",Foto);
        datosUsuario.put("ID",n);
        mRootReference.child("Articulos").push().setValue(datosUsuario);
        //Notificacion de guardado
        Toast toast = Toast.makeText(getApplicationContext(), "Datos guardados exitosamente!", Toast.LENGTH_LONG);
        toast.show();
        //Limpiando campos
        mEditTextArticulo.getText().clear();
        mEditTextDescripcion.getText().clear();
        mEditTextPrecio.getText().clear();
    }

    //::::::::::::::::::::::: Metodo para Subir la imagen ::::::::::::::::::::::::::::::::::::
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==GALLERY_INTENT && resultCode == RESULT_OK)
        {
            Uri uri = data.getData();
            //Aqui creamos la carpeta dentro del storage
            final StorageReference destinofoto = mStorage.child("Articulos").child(uri.getLastPathSegment());
            destinofoto.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    destinofoto.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final Uri downloadUrl = uri;

                            Glide.with(IngresarArticulo.this)
                                    .load(downloadUrl).fitCenter().centerCrop().into(FotoArticulo);
                            FotoImagen  =   downloadUrl.toString();
                            /*Glide.with(subir_imagen.this)
                                    .load(downloadUrl).fitCenter().centerCrop().into(botonimagen);*/
                            Toast.makeText(IngresarArticulo.this,"Se subio la foto ",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
        }
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
            nameText.setText(account.getDisplayName());
            //emailTextView.setText(account.getEmail());
            //account.getId()
            //idGoogle.setText(account.getId());
            try{
                Glide.with(this).load(account.getPhotoUrl()).into(fotoperfil);
            }catch (NullPointerException e){
                Toast.makeText(getApplicationContext(),"Imagen no encontrada", Toast.LENGTH_LONG).show();
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
}