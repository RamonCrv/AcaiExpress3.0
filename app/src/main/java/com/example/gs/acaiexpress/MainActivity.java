package com.example.gs.acaiexpress;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.autofill.AutofillManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gs.acaiexpress.ui.main.Dados;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.example.gs.acaiexpress.ui.main.MainFragment;


public class MainActivity extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener {

    private SignInButton SignIn;
    private GoogleApiClient googleApiClient;
    private Button btnRegistrar;
    private Button btnLogar;
    private EditText editEmail;
    private EditText editSenha;
    private FirebaseAuth mAuth;
    public static final int SING_IN_CODE = 777;


    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        inicializarComponentes();
        eventoClicks();

        mAuth = FirebaseAuth.getInstance();
        botaoDoGoogle();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, MainFragment.newInstance()).commitNow();
        }
    }

    //EVENTOS DE BOTÕES
    private void eventoClicks() {
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String email = editEmail.getText().toString().trim();
                String senha = editSenha.getText().toString().trim();

                criarUser(email,senha);
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

    //CRIAR USUARIO TRADICIONAL
    private void criarUser(String email, String senha){

        if(email.contains("@") == false){
            alert("EMAIL INVALIDO");
        }else{
            if (senha.length() <=7 ){
                alert("SENHA MUITO PEQUENA");
            }else{
                mAuth.createUserWithEmailAndPassword(email,senha).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete( Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            mAuth.getCurrentUser();

                            alert("Cadastrado com sucesso ");

                            Intent i =new Intent(MainActivity.this, Dados.class);
                            startActivity(i);
                        }else{

                            alert("Email já Cadastrado");
                        }
                    }
                });
            }
        }

    }

    //FAZ O LOGIN TRADICIONAL
    private void login(String email, String senha) {

      if(TextUtils.isEmpty(email) || TextUtils.isEmpty(senha)){
        alert("prencha todos os campos");
      }else{
          mAuth.signInWithEmailAndPassword(email,senha).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {

              @Override
              public void onComplete(Task<AuthResult> task) {
                  if (task.isSuccessful()){

                      Intent i =new Intent(MainActivity.this, Dados.class);
                      startActivity(i);
                      //alert("Logou");

                  }else{
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

    //GOOGLE THINGS THAT WE ARE NOT USING YET
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        alert("nao deu");
    }

    public  void botaoDoGoogle(){
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();
        SignIn = (SignInButton) findViewById(R.id.bt_login);
        SignIn.setSize(SignInButton.SIZE_WIDE);
        SignIn.setColorScheme(SignInButton.COLOR_AUTO);
        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SING_IN_CODE);
                coisaQueVaiProDados();


            }
        });
    }

    private void goLoInScrean() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void handleSingInresulte(GoogleSignInResult result) {
        if (result.isSuccess()) {

            Intent intent = new Intent(this,Dados.class);
            startActivity(intent);

        }else{
            goLoInScrean();
        }

    }

    public  void coisaQueVaiProDados(){
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSingInresulte(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSingInresulte(googleSignInResult);
                }
            });
        }
    }
}


