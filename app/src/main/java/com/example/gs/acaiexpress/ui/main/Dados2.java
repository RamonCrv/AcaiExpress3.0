package com.example.gs.acaiexpress.ui.main;
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
import android.provider.MediaStore;
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
import com.example.gs.acaiexpress.R;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.IOException;
import java.util.Random;

public class Dados2 extends AppCompatActivity {
    private Button btnSalvar;
    private Button ediimge;
    private Button verif;
    public TextView nPonto;
    public TextView preso;
    private TextView vnome;
    private Button button2;
    private TextView vpreco;
    private TextView codAva;
    private TextView medAva;
    private FirebaseUser user;
    private ImageView mImagPhoto;
    private FirebaseAuth auth;
    private Uri mUri;
    private CheckBox abertoCheck;
    private boolean trocouImagem = false;
    private Button btlocal;
    DatabaseReference databaseDoc;
    DatabaseReference databaseDoc2;
    String url;
    private boolean priVezCriado;
    private String latAtual, longAtual;
    private static final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados2);
        Intent b = new Intent(Dados2.this, Um.class);
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

      /*  button2.setOnClickListener(new View.OnClickListener() {
            @Override
           public void onClick(View view) {
                Intent i = new Intent(Dados2.this, Dados.class);
                startActivity(i);
            }
       });


    */}

    //INICIA COMPONENTES
    private void inicializarComponentes() {
        FirebaseApp.initializeApp(Dados2.this);
        button2 = (Button) findViewById(R.id.button);
        nPonto = (TextView) findViewById(R.id.editNponto);
        preso = (TextView) findViewById(R.id.editPreso);
        btnSalvar = (Button) findViewById(R.id.btSalvar);
        ediimge = (Button) findViewById(R.id.ediimg);
        mImagPhoto = (ImageView) findViewById(R.id.imageView);
        abertoCheck = (CheckBox) findViewById(R.id.abertoBox);
        codAva = (TextView) findViewById(R.id.codAvaView);
        medAva = (TextView) findViewById(R.id.txtMedAv);


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

    //ESCOLHER FOTO NO CELULAR
    private void selectfoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    //ADICIONAR DOC NO BANCO
    public void AddDoc() {
        String nomePonto = nPonto.getText().toString().trim();
        String preco = preso.getText().toString().trim();
        if (!TextUtils.isEmpty(nomePonto) || !TextUtils.isEmpty(preco)) {
            Ponto ponto = new Ponto();
            ponto.setNome(nPonto.getText().toString());
            ponto.setPreso(preso.getText().toString());
            ponto.setID(auth.getCurrentUser().getUid());
            ponto.setCodAva(getRandomString(6));
            if (priVezCriado) {
                ponto.setLatiT("0");
                ponto.setLongT("0");
                ponto.setTotalAv("0");
                ponto.setSomaAv("0");
                ponto.setMediaAv("0");
            } else {
                ponto.setLatiT(latAtual);
                ponto.setLongT(longAtual);
            }
            ponto.setVerificado("F");
            if (abertoCheck.isChecked()) {
                ponto.setAberto("Aberto");
            } else {
                ponto.setAberto("Fechado");
            }
            user = FirebaseAuth.getInstance().getCurrentUser();
            databaseDoc.child(ponto.getID()).setValue(ponto);
            if (trocouImagem) {
                saveUserInFirebase();
                alert("Trocou imagem = true");
            }
            alert("Salvo");
        } else {
            alert("Erro");
        }
    }

    //SALVA A IMAGEM NO STORAGE COM O ID DO USUARIO
    private void saveUserInFirebase() {
        String userID = auth.getCurrentUser().getUid();
        final StorageReference ref = FirebaseStorage.getInstance().getReference().child("/images/" + userID);
        ref.putFile(mUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.i("teste", uri.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("test", e.getMessage(), e);
            }
        });
    }

    //FUNÇÃO QUE ACHA A IMAGEM NO STORAGE E EXECUTA O GLIDE
    public void BuscarImg() {
        final String userID = auth.getCurrentUser().getUid();
        final StorageReference ref = FirebaseStorage.getInstance().getReference().child("/images/").child(userID);
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
                        preso.setText(dataSnapshot.child(userID).child("preso").getValue().toString());
                        medAva.setText(":" + dataSnapshot.child(userID).child("mediaAv").getValue().toString());
                        if (dataSnapshot.child(userID).child("codAva").getValue().toString() != null) {
                            codAva.setText(dataSnapshot.child(userID).child("codAva").getValue().toString());
                        }

                        String situacao = dataSnapshot.child(userID).child("aberto").getValue().toString();

                        latAtual = dataSnapshot.child(userID).child("latiT").getValue().toString();
                        longAtual = dataSnapshot.child(userID).child("longT").getValue().toString();
                        medAva.setText(dataSnapshot.child(userID).child("mediaAv").getValue().toString());
                        if (situacao.equals("Aberto")) {
                            abertoCheck.setChecked(true);
                        } else {
                            abertoCheck.setChecked(false);
                        }
                    }
                } else {
                    priVezCriado = true;
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


}