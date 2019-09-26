package com.example.gs.acaiexpress;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

public class tela1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tela1);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                      Intent i = new Intent(tela1.this,MainActivity.class);
                      startActivity(i);
                    finish();
                }
            },4500);
        }
}



