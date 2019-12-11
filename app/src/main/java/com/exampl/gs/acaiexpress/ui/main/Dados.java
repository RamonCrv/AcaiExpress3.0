package com.exampl.gs.acaiexpress.ui.main;
import androidx.annotation.NonNull;
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
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.exampl.gs.acaiexpress.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.UploadTask;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Dados extends AppCompatActivity {
    private Button btnSalvar;
    private Button ediimge;
    private Button voltar;
    public EditText nPonto;
    public EditText preso;
    private FirebaseUser user;
    private ImageView mImagPhoto;
    private ImageView bandeiraPt;
    private  FirebaseAuth auth;
    private Uri mUri;
    private Button abertoCheck;
    private boolean trocouImagem = false;
    private Button btlocal;
    DatabaseReference databaseDoc;
    DatabaseReference databaseDoc2;
    String url;
    boolean escolheuImg;

    private boolean priVezCriado;
    private boolean aberto;
    private String latAtual, longAtual, TotalDeAv, SomaTdeAv, MedAv, CodAv, NomePt,nNota,Data, verificado;

    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados);
        pedirPermissao();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        databaseDoc = FirebaseDatabase.getInstance().getReference("Ponto");
        inicializarComponentes();
        eventoClicks();
        auth = FirebaseAuth.getInstance();
        BuscarDoc();
        BuscarImg();
        attNotaEmTR();
        click();

        if (priVezCriado == false){
            //nPonto.setKeyListener(null);
        }
    }
    //AÇÕES DO BOTÕES
    private void eventoClicks() {
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (priVezCriado && trocouImagem == false ){
                    alert("É necessario escolher uma foto para salvar");
                }else{
                    if (preso.getText().toString().equals("") ||  nPonto.getText().toString().equals("") || preso.getText() == null  || nPonto.getText() == null){
                        alert("É obrigatorio preencher todos os campos");
                    }else{

                        AddDoc();
                        //SalvarData();
                        trocouImagem = false;
                        tempo();

                    }

                }



            }
        });
        ediimge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trocouImagem = true;
                selectfoto();

            }
        });

    }
    public void tempo(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i = new Intent(Dados.this, Dados2.class);
                startActivity(i);
                alert("Salvo");
                finish();
            }
        },3000);

    }


    //INICIA COMPONENTES
    private void inicializarComponentes() {
        FirebaseApp.initializeApp(Dados.this);
        nPonto = (EditText) findViewById(R.id.editNponto);
        preso = (EditText) findViewById(R.id.editPreso);
        btnSalvar = (Button) findViewById(R.id.btSalvar);
        ediimge = (Button) findViewById(R.id.ediimg);
        mImagPhoto = (ImageView) findViewById(R.id.imageView);
        abertoCheck = (Button) findViewById(R.id.abertoBox);
        bandeiraPt =  (ImageView) findViewById(R.id.imgbandeira);
        voltar =  (Button) findViewById(R.id.btnVoltar);



    }

    void click(){
        abertoCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              aberto = !aberto;
              if(aberto){
                  bandeiraPt.setBackgroundResource(R.mipmap.bandeiraon2_background);
                  abertoCheck.setText("Aberto!");
                  abertoCheck.setBackgroundResource(R.drawable.btn2);
              }else {
                  bandeiraPt.setBackgroundResource(R.mipmap.bandeiraoff2_background);
                  abertoCheck.setText("Fechado!");
                  abertoCheck.setBackgroundResource(R.drawable.btn3);


              }
            }
        });

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Dados.this, Dados2.class);
                startActivity(i);
                finish();
            }
        });


    }


    //ACHO Q SALVA  A IMAGEM
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            if (requestCode == 0){
                mUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap =  MediaStore.Images.Media.getBitmap(getContentResolver(),mUri);
                    mImagPhoto.setImageDrawable(new BitmapDrawable(bitmap));
                } catch (IOException e) {
                }
            }
        }else{
            trocouImagem = false;
        }

    }
    //ESCOLHER FOTO NO CELULAR
    private void selectfoto(){

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 0);

    }
    //ADICIONAR DOC NO BANCO
    public void AddDoc(){

        String nomePonto = nPonto.getText().toString().trim();
        String preco = preso.getText().toString().trim();

            Ponto ponto = new Ponto();
            ponto.setNome(nPonto.getText().toString());
            ponto.setPreso(preso.getText().toString());
            ponto.setID(auth.getCurrentUser().getUid());

            if (priVezCriado){
                ponto.setLatiT("0");
                ponto.setLongT("0");
                ponto.setTotalAv("0");
                ponto.setSomaAv("0");
                ponto.setMediaAv("0");
                ponto.setCodAva(getRandomString(6));
                ponto.setVerificado("F");

            }else{

                ponto.setLatiT(latAtual);
                ponto.setLongT(longAtual);
                ponto.setMediaAv(MedAv);
                ponto.setCodAva(CodAv);
                ponto.setSomaAv(SomaTdeAv);
                ponto.setTotalAv(TotalDeAv);
                ponto.setVerificado(verificado);

            }

            if (aberto){
                ponto.setAberto("Aberto");


            }else{
                ponto.setAberto("Fechado");

            }
            user = FirebaseAuth.getInstance().getCurrentUser();
            databaseDoc.child(ponto.getID()).setValue(ponto);
            FirebaseDatabase.getInstance().getReference().child("Ponto/"+user.getUid()+"/nNota").setValue(nNota);
            if (trocouImagem){
                saveUserInFirebase();
                alert("Trocando Imagem...");
            }
            SalvarData();
        Toast.makeText(Dados.this, "Salvando..", Toast.LENGTH_LONG).show();

    }
    //SALVA A IMAGEM NO STORAGE COM O ID DO USUARIO
    private void saveUserInFirebase() {
        String userID = auth.getCurrentUser().getUid();
        final StorageReference ref = FirebaseStorage.getInstance().getReference().child("/Imagens/Ponto/"+userID);
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
        final StorageReference ref = FirebaseStorage.getInstance().getReference().child("/Imagens/Ponto/").child(userID);
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
                    priVezCriado = false;


                   String contAcha = dataSnapshot.child(userID).child("id").getValue().toString();
                    if(contAcha == null){
                        alert("NÃO ACHOU");
                    }else{
                        nPonto.setText(dataSnapshot.child(userID).child("nome").getValue().toString());
                        NomePt =dataSnapshot.child(userID).child("nome").getValue().toString();
                        preso.setText(dataSnapshot.child(userID).child("preso").getValue().toString());
                        MedAv = dataSnapshot.child(userID).child("mediaAv").getValue().toString();
                        Data = dataSnapshot.child(userID).child("data").getValue().toString();
                        if (dataSnapshot.child(userID).child("nNota").getValue() != null){
                            nNota = dataSnapshot.child(userID).child("nNota").getValue().toString();
                        }

                        if (dataSnapshot.child(userID).child("codAva").getValue().toString() != null){
                            CodAv = dataSnapshot.child(userID).child("codAva").getValue().toString();
                        }

                        String situacao = dataSnapshot.child(userID).child("aberto").getValue().toString();

                        latAtual = dataSnapshot.child(userID).child("latiT").getValue().toString();
                        longAtual =  dataSnapshot.child(userID).child("longT").getValue().toString();

                        TotalDeAv = dataSnapshot.child(userID).child("totalAv").getValue().toString();
                        SomaTdeAv = dataSnapshot.child(userID).child("somaAv").getValue().toString();
                        verificado = dataSnapshot.child(userID).child("verificado").getValue().toString();



                        if (situacao.equals("Aberto")){
                            aberto =true;
                            abertoCheck.setText("Aberto!");
                            bandeiraPt.setBackgroundResource(R.mipmap.bandeiraon2_background);
                        }else{
                            aberto =false;
                            abertoCheck.setText("Fechado!");
                            bandeiraPt.setBackgroundResource(R.mipmap.bandeiraoff2_background);
                        }
                    }
                }else {
                   priVezCriado = true;

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

    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    void attNotaEmTR(){
        DatabaseReference databaseDoc5;
        databaseDoc5 = FirebaseDatabase.getInstance().getReference();

        databaseDoc5.child("Ponto/"+auth.getCurrentUser().getUid()+"/mediaAv").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                   MedAv = dataSnapshot.getValue().toString();
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference databaseDoc6;
        databaseDoc6 = FirebaseDatabase.getInstance().getReference();

        databaseDoc6.child("Ponto/"+auth.getCurrentUser().getUid()+"/totalAv").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    TotalDeAv =dataSnapshot.getValue().toString();
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference databaseDoc7;
        databaseDoc7 = FirebaseDatabase.getInstance().getReference();

        databaseDoc7.child("Ponto/"+auth.getCurrentUser().getUid()+"/somaAv").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    SomaTdeAv =dataSnapshot.getValue().toString();
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    void SalvarData(){

        DatabaseReference databaseDoc3;
        databaseDoc3 = FirebaseDatabase.getInstance().getReference();
        databaseDoc3.child("Ponto/"+auth.getCurrentUser().getUid()+"/data").setValue(Printdate());
    }

    String Printdate(){
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy - hh:mm a");
        String dateToStr = format.format(today);
        return "atualizado em : "+dateToStr;
    }









}