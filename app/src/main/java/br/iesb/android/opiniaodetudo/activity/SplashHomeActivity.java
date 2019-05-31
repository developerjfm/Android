package br.iesb.android.opiniaodetudo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import br.iesb.android.opiniaodetudo.R;

public class SplashHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_home);

        Thread timer = new Thread(){

            @Override
            public void run() {
                try {
                    sleep(3000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {
                    Intent intent = new Intent(SplashHomeActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };

        timer.start();


    }


    @Override
    protected void onPause() {
        super.onPause();

        finish();
    }
}
