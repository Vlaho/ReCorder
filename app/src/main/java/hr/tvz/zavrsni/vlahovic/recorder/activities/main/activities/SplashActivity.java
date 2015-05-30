package hr.tvz.zavrsni.vlahovic.recorder.activities.main.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import hr.tvz.zavrsni.vlahovic.recorder.R;

/**
 * Created by Vlaho on 20.5.2015..
 */
public class SplashActivity extends Activity{

    private static int SPLASH_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, DeviceConnectionActivity.class));
                finish();
            }
        }, SPLASH_TIME);
    }
}
