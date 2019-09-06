package com.example.gs.acaiexpress.ui.main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gs.acaiexpress.MainActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



import com.example.gs.acaiexpress.R;

public class Dados extends AppCompatActivity {

    private Button btnSalvar;
    public EditText nPonto;
    public EditText preso;
    private FirebaseUser user;
    DatabaseReference databaseDoc;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados);
        databaseDoc = FirebaseDatabase.getInstance().getReference("Ponto");
        inicializarComponentes();
        eventoClicks();

    }

    private void eventoClicks() {
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
            ponto.setID(user.getEmail());
            user = FirebaseAuth.getInstance().getCurrentUser();
            databaseDoc.child(user.getEmail()).setValue(ponto);

            alert("Salvo");
        }else{
            alert("Erro");
        }

    }

    private  void alert (String msg){
        Toast.makeText(Dados.this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

}





