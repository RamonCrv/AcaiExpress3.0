package com.exampl.gs.acaiexpress.ui.main;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.exampl.gs.acaiexpress.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.exampl.gs.acaiexpress.R;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Random;

public class Dados2 extends AppCompatActivity {

    public TextView nPonto;
    private Button button2;
    private TextView codAva;
    private TextView medAva;

    private FirebaseUser user;
    private ImageView mImagPhoto;
    private FirebaseAuth auth;
    private Uri mUri;
    private TextView QuantAva;
    private Button sair;
    DatabaseReference databaseDoc;
    DatabaseReference databaseDoc2;
    String url;

    private ImageView nNotas;
    private boolean priVezCriado;
    private String latAtual, longAtual;
    private static final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados2);
        pedirPermissao();
        databaseDoc = FirebaseDatabase.getInstance().getReference("Ponto");
        inicializarComponentes();
        eventoClicks();
        auth = FirebaseAuth.getInstance();
        BuscarDoc();
        BuscarImg();
        attNotaEmTR();

    }

    //AÇÕES DO BOTÕES
    private void eventoClicks() {


      button2.setOnClickListener(new View.OnClickListener() {
            @Override
           public void onClick(View view) {

                Intent i = new Intent(Dados2.this, Dados.class);
                startActivity(i);
            }
       });
        sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singOut();
                Intent i = new Intent(Dados2.this, MainActivity.class);
                startActivity(i);
            }
        });


   }public void singOut(){
        FirebaseAuth.getInstance().signOut();
    }


    //INICIA COMPONENTES
    private void inicializarComponentes() {
        FirebaseApp.initializeApp(Dados2.this);
        button2 = (Button) findViewById(R.id.button);
        sair = (Button)findViewById(R.id.button2);
        nPonto = (TextView) findViewById(R.id.editNponto);
        mImagPhoto = (ImageView) findViewById(R.id.imageView);
        codAva = (TextView) findViewById(R.id.codAvaView);
        medAva = (TextView) findViewById(R.id.txtMedAv);
        QuantAva = (TextView)findViewById(R.id.quantAva);
        nNotas = (ImageView)findViewById(R.id.txtAV);

    }

    //ACHO Q SALVA  A IMAGEM
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            mUri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mUri);
                mImagPhoto.setImageDrawable(new BitmapDrawable(bitmap));
            } catch (IOException e) {
            }
        }
    }

    //FUNÇÃO QUE ACHA A IMAGEM NO STORAGE E EXECUTA O GLIDE
    public void BuscarImg() {
        final String userID = auth.getCurrentUser().getUid();
        final StorageReference ref = FirebaseStorage.getInstance().getReference().child("/Imagens/Ponto/").child(userID);
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                url = uri.toString();
                glide(url, mImagPhoto);
            }

        });
    }

    //FUNÇÃO QUE BAIXA A IMAGEM E SALVA NO PONTO
    public void glide(String url, ImageView imagem) {
        Glide.with(this).load(url).into(imagem);
    }

    //RESGATA  DOCUMENTO NO DOC
    public void BuscarDoc() {

        databaseDoc2 = FirebaseDatabase.getInstance().getReference();
        final String userID = auth.getCurrentUser().getUid();
        databaseDoc2.child("Ponto").orderByChild("id").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    priVezCriado = false;

                    String contAcha = dataSnapshot.child(userID).child("id").getValue().toString();
                    if (contAcha == null) {
                        alert("NÃO ACHOU");
                    } else {
                        nPonto.setText(dataSnapshot.child(userID).child("nome").getValue().toString());

                        medAva.setText(dataSnapshot.child(userID).child("mediaAv").getValue().toString());
                        if (dataSnapshot.child(userID).child("codAva").getValue().toString() != null) {
                            codAva.setText(dataSnapshot.child(userID).child("codAva").getValue().toString());
                        }

                        String situacao = dataSnapshot.child(userID).child("aberto").getValue().toString();

                        latAtual = dataSnapshot.child(userID).child("latiT").getValue().toString();
                        longAtual = dataSnapshot.child(userID).child("longT").getValue().toString();
                        medAva.setText(dataSnapshot.child(userID).child("mediaAv").getValue().toString());
                        if(dataSnapshot.child(userID).child("verificado").getValue().toString().equals("F")){
                            nNotas.setBackgroundResource(R.mipmap.naopossui_background);

                        }else{
                            nNotas.setBackgroundResource(R.mipmap.selo_background);
                        }
                        if (situacao.equals("Aberto")) {

                        } else {

                        }
                    }
                } else {

                        button2.setText("Adicionar Informações");


                    priVezCriado = true;
                    Toast.makeText(Dados2.this, "Parece que você ainda não possui um ponto cadastrado, acesse Adicionar informações para fazer o cadastro.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //MOSTRA MSG
    private void alert(String msg) {
        Toast.makeText(Dados2.this, msg, Toast.LENGTH_SHORT).show();
    }

    //Pedri Permissao
    boolean pedirPermissao() {

        ActivityCompat.requestPermissions(Dados2.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        if (ContextCompat.checkSelfPermission(Dados2.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {

            return true;
        }

    }

    private static String getRandomString(final int sizeOfRandomString) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    void attNotaEmTR() {
        DatabaseReference databaseDoc5;
        databaseDoc5 = FirebaseDatabase.getInstance().getReference();

        databaseDoc5.child("Ponto/" + auth.getCurrentUser().getUid() + "/mediaAv").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    medAva.setText(dataSnapshot.getValue().toString());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseDoc5.child("Ponto/" + auth.getCurrentUser().getUid() + "/totalAv").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    QuantAva.setText("Total de Avaliações: "+dataSnapshot.getValue().toString());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }




}