package com.example.gs.acaiexpress;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gs.acaiexpress.ui.main.Dados;

public class Um extends AppCompatActivity {
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_um);
        startt();
                progressBar.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        // Intent i = new Intent(Um.this, Dados.class);
                       //  startActivity(i);
                       finish();
                    }
                },3000);
            }
            private void startt(){
                this.progressBar = findViewById(R.id.progressBar);
            }
        }




