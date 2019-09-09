package com.example.gs.acaiexpress;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

public class tela1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela1);







            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                      Intent i = new Intent(tela1.this,MainActivity.class);
                      startActivity(i);
                    finish();
                }
            },3000);
        }

}



