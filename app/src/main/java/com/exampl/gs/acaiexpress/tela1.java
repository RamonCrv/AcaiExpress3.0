package com.exampl.gs.acaiexpress;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class tela1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        super.onCreate(savedInstanceState);
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



