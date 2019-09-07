package com.example.gs.acaiexpress.ui.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import com.example.gs.acaiexpress.MainActivity;
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
import java.net.URL;
import java.util.UUID;

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
    DatabaseReference databaseDoc;
    DatabaseReference databaseDoc2;
    String url;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados);
        databaseDoc = FirebaseDatabase.getInstance().getReference("Ponto");

        inicializarComponentes();

        eventoClicks();
        auth = FirebaseAuth.getInstance();
        BuscarDoc();
        BuscarImg();

    }

    private void eventoClicks() {
        verif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               BuscarDoc();
            }
        });
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomePonto = nPonto.getText().toString().trim();
                String preco = preso.getText().toString().trim();
                AddDoc();

            }
        });
    }


    private void inicializarComponentes() {
        FirebaseApp.initializeApp(Dados.this);
        verif = (Button) findViewById(R.id.veri);
        vnome = (TextView) findViewById(R.id.txnome);
        vpreco = (TextView) findViewById(R.id.txpreco);
        nPonto = (EditText) findViewById(R.id.editNponto);
        preso = (EditText) findViewById(R.id.editPreso);
        btnSalvar = (Button) findViewById(R.id.btSalvar);
        ediimge = (Button) findViewById(R.id.ediimg);
        mImagPhoto = (ImageView) findViewById(R.id.imageView);
        abertoCheck = (CheckBox) findViewById(R.id.abertoBox);

        ediimge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectfoto();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0){
            mUri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap =  MediaStore.Images.Media.getBitmap(getContentResolver(),mUri);
                mImagPhoto.setImageDrawable(new BitmapDrawable(bitmap));
               // ediimge.setAlpha(0);

            } catch (IOException e) {

            }

        }

    }

    private void selectfoto(){
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent,0);

        }

    public void AddDoc(){
        String nomePonto = nPonto.getText().toString().trim();
        String preco = preso.getText().toString().trim();
        if (!TextUtils.isEmpty(nomePonto)||!TextUtils.isEmpty(preco)){
            String id = databaseDoc.push().getKey();

            Ponto ponto = new Ponto();

            ponto.setNome(nPonto.getText().toString());
            ponto.setPreso(preso.getText().toString());
            ponto.setID(auth.getCurrentUser().getUid());
            if (abertoCheck.isChecked()){
                ponto.setAberto("Aberto");
            }else{
                ponto.setAberto("Fechado");
            }

            user = FirebaseAuth.getInstance().getCurrentUser();
            databaseDoc.child(ponto.getID()).setValue(ponto);
                saveUserInFirebase();
            alert("Salvo");

        }else{
            alert("Erro");
        }

    }

    private void saveUserInFirebase() {
        String fileEmail = auth.getCurrentUser().getUid();
        final StorageReference ref = FirebaseStorage.getInstance().getReference().child("/images/"+fileEmail);
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

    public void BuscarImg(){
        //databaseDoc2 = FirebaseDatabase.getInstance().getReference();
        final String userID = auth.getCurrentUser().getUid();
        final StorageReference ref = FirebaseStorage.getInstance().getReference().child("/images/").child(userID);

        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

               url = uri.toString();
                alert(url);
               glide(url,mImagPhoto);
            }

        });



    }

public void glide(String url,ImageView imagem){
    Glide.with(this).load(url).into(imagem);
}


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
                        if (dataSnapshot.child(userID).child("aberto").getValue().toString() == "Aberto"){
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

    private  void alert (String msg){
        Toast.makeText(Dados.this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

}





