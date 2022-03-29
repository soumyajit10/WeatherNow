package com.example.weathernow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    private TextView app_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splah);
        app_name = findViewById(R.id.app_name);
        Typeface typeface = ResourcesCompat.getFont(this,R.font.blacklist);
        app_name.setTypeface(typeface);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.styleanim);
        app_name.setAnimation(animation);


        new Thread() {
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }.start();

    }
}