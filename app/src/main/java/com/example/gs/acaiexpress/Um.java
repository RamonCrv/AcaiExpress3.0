package com.example.gs.acaiexpress;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gs.acaiexpress.ui.main.Dados;

public class Um extends AppCompatActivity {
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_um);
        caregando();
        startt();
                progressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                       //  Intent i = new Intent(Um.this,MainActivity.class);
                       //  startActivity(i);
                       finish();
                    }
                },2000);
            }

    private void caregando() {
        AlertDialog.Builder megaBox = new AlertDialog.Builder(this);
        megaBox.setTitle("Entrando");
        megaBox.setMessage("Caregando...");

    }
            private void startt(){
                this.progressBar = findViewById(R.id.progressBar);
            }
        }




