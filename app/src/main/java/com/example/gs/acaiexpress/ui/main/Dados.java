package com.example.gs.acaiexpress.ui.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gs.acaiexpress.Cadastro;
import com.example.gs.acaiexpress.MainActivity;
import com.example.gs.acaiexpress.Um;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



import com.example.gs.acaiexpress.R;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.IOException;


public class Dados extends AppCompatActivity {

    private Button btnSalvar;
    private Button ediimge;
    private Button verif;
    public EditText nPonto;
    public EditText preso;
    private TextView vnome;
    private TextView vpreco;
    private FirebaseUser user;
    private ImageView mImagPhoto;
    private  FirebaseAuth auth;
    private Uri mUri;
    private CheckBox abertoCheck;
    private boolean trocouImagem = false;
    private Button btlocal;
    DatabaseReference databaseDoc;
    DatabaseReference databaseDoc2;
    String url;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados);
        Intent b = new Intent(Dados.this, Um.class);
        startActivity(b);
        pedirPermissao();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        databaseDoc = FirebaseDatabase.getInstance().getReference("Ponto");
        inicializarComponentes();
        eventoClicks();
        auth = FirebaseAuth.getInstance();
        BuscarDoc();
        BuscarImg();
    }
    //AÇÕES DO BOTÕES
    private void eventoClicks() {
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomePonto = nPonto.getText().toString().trim();
                String preco = preso.getText().toString().trim();
                AddDoc();
                trocouImagem = false;
            }
        });
        ediimge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trocouImagem = true;
                selectfoto();
            }
        });
        btlocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pedirPermissao()) {
                    Intent i = new Intent(Dados.this, ActivityLocation.class);
                    startActivity(i);
                }else{

                    alert("É necessario a permissão do GPS para usar está função");
                }

            }
        });


    }
    //INICIA COMPONENTES
    private void inicializarComponentes() {
        FirebaseApp.initializeApp(Dados.this);
        btlocal = (Button) findViewById(R.id.btnlocal);
        nPonto = (EditText) findViewById(R.id.editNponto);
        preso = (EditText) findViewById(R.id.editPreso);
        btnSalvar = (Button) findViewById(R.id.btSalvar);
        ediimge = (Button) findViewById(R.id.ediimg);
        mImagPhoto = (ImageView) findViewById(R.id.imageView);
        abertoCheck = (CheckBox) findViewById(R.id.abertoBox);
    }
    //ACHO Q SALVA  A IMAGEM
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0){
            mUri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap =  MediaStore.Images.Media.getBitmap(getContentResolver(),mUri);
                mImagPhoto.setImageDrawable(new BitmapDrawable(bitmap));
            } catch (IOException e) {
            }
        }
    }
    //ESCOLHER FOTO NO CELULAR
    private void selectfoto(){
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
            startActivityForResult(intent,0);
    }
    //ADICIONAR DOC NO BANCO
    public void AddDoc(){
        String nomePonto = nPonto.getText().toString().trim();
        String preco = preso.getText().toString().trim();
        if (!TextUtils.isEmpty(nomePonto)||!TextUtils.isEmpty(preco)){
            Ponto ponto = new Ponto();
            ponto.setNome(nPonto.getText().toString());
            ponto.setPreso(preso.getText().toString());
            ponto.setID(auth.getCurrentUser().getUid());
            ponto.setLatiT("0");
            ponto.setLongT("0");
            ponto.setVerificado("F");
            if (abertoCheck.isChecked()){
                ponto.setAberto("Aberto");
            }else{
                ponto.setAberto("Fechado");
            }
            user = FirebaseAuth.getInstance().getCurrentUser();
            databaseDoc.child(ponto.getID()).setValue(ponto);
            if (trocouImagem){
                saveUserInFirebase();
                alert("Trocou imagem = true");
            }
            alert("Salvo");
        }else{
            alert("Erro");
        }
    }
    //SALVA A IMAGEM NO STORAGE COM O ID DO USUARIO
    private void saveUserInFirebase() {
        String userID = auth.getCurrentUser().getUid();
        final StorageReference ref = FirebaseStorage.getInstance().getReference().child("/images/"+userID);
        ref.putFile(mUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.i("teste",uri.toString());
                    }
                });
            }
        }) .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("test", e.getMessage(), e);
            }
        });
    }
    //FUNÇÃO QUE ACHA A IMAGEM NO STORAGE E EXECUTA O GLIDE
    public void BuscarImg(){
        final String userID = auth.getCurrentUser().getUid();
        final StorageReference ref = FirebaseStorage.getInstance().getReference().child("/images/").child(userID);
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
               url = uri.toString();
               glide(url,mImagPhoto);
            }

        });
    }
    //FUNÇÃO QUE BAIXA A IMAGEM E SALVA NO PONTO
    public void glide(String url,ImageView imagem){
    Glide.with(this).load(url).into(imagem);
    }
    //RESGATA  DOCUMENTO NO DOC
    public void BuscarDoc(){
        databaseDoc2 = FirebaseDatabase.getInstance().getReference();
        final String userID = auth.getCurrentUser().getUid();
        databaseDoc2.child("Ponto").orderByChild("id").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                   String contAcha = dataSnapshot.child(userID).child("id").getValue().toString();
                    if(contAcha == null){
                        alert("NÃO ACHOU");
                    }else{
                        nPonto.setText(dataSnapshot.child(userID).child("nome").getValue().toString());
                        preso.setText(dataSnapshot.child(userID).child("preso").getValue().toString());
                       String situacao = dataSnapshot.child(userID).child("aberto").getValue().toString();
                        if (situacao.equals("Aberto")){
                            abertoCheck.setChecked(true);
                        }else{
                            abertoCheck.setChecked(false);
                        }
                    }
                }else {
                    alert("NÃO ACHOU2");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    //MOSTRA MSG
    private  void alert (String msg){
        Toast.makeText(Dados.this,msg,Toast.LENGTH_SHORT).show();
    }

    //Pedri Permissao
    boolean pedirPermissao(){

        ActivityCompat.requestPermissions(Dados.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        if (ContextCompat.checkSelfPermission(Dados.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }else {

            return true;
        }

    }



}