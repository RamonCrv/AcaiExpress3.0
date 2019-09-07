package com.example.gs.acaiexpress.ui.main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gs.acaiexpress.MainActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



import com.example.gs.acaiexpress.R;
import com.google.firebase.database.ValueEventListener;

public class Dados extends AppCompatActivity {

    private Button btnSalvar;
    private Button verif;
    public EditText nPonto;
    public EditText preso;
    private TextView vnome;
    private TextView vpreco;
    private FirebaseUser user;
    private  FirebaseAuth auth;
    DatabaseReference databaseDoc;
    DatabaseReference databaseDoc2;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados);
        databaseDoc = FirebaseDatabase.getInstance().getReference("Ponto");

        inicializarComponentes();
        eventoClicks();
        auth = FirebaseAuth.getInstance();

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
            user = FirebaseAuth.getInstance().getCurrentUser();
            databaseDoc.child(ponto.getID()).setValue(ponto);

            alert("Salvo");

        }else{
            alert("Erro");
        }

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
                        vnome.setText(dataSnapshot.child(userID).child("nome").getValue().toString());
                        vpreco.setText(dataSnapshot.child(userID).child("preso").getValue().toString());
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





