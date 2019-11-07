package com.example.gs.acaiexpress;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.gs.acaiexpress.ui.main.Dados;
import com.example.gs.acaiexpress.ui.main.Dados2;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.example.gs.acaiexpress.ui.main.MainFragment;

public class MainActivity extends AppCompatActivity   {
    private Button btnRegistrar;
    private Button btnLogar;
    private EditText editEmail;
    private EditText editSenha;
    private FirebaseAuth mAuth;
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        inicializarComponentes();
        eventoClicks();

        mAuth = FirebaseAuth.getInstance();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, MainFragment.newInstance()).commitNow();
        }

    }


       //EVENTOS DE BOTÕES
    private void eventoClicks() {
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Cadastro.class);
                startActivity(i);
            }
        });
        btnLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString().trim();
                String senha = editSenha.getText().toString().trim();
                login(email, senha);
            }
        });


    }


    //FAZ O LOGIN TRADICIONAL
    private void login(String email, String senha) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(senha)) {
            alert("prencha todos os campos");
        } else {
            mAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent i = new Intent(MainActivity.this, Dados.class);
                        startActivity(i);
                        //alert("Logou");
                    } else {
                        alert("Email ou senha incorreto");
                    }
                }
            });
        }
    }
    //INICIALIZA COMPONENTES
    private void inicializarComponentes() {
        btnRegistrar = (Button) findViewById(R.id.btCadastrar);
        btnLogar = (Button) findViewById(R.id.btLogar);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editSenha = (EditText) findViewById(R.id.editSenha);
    }
    //MOSTRA MSG
    private  void alert (String msg){
        final  Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0,0);
        toast.setDuration(Toast.LENGTH_SHORT);
        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
    }



}