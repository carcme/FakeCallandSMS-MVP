package me.carc.fakecallandsms_mvp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by bamptonm on 7/4/17.
 */

public class SplashActivity extends Base {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int SPLASH_DISPLAY_LENGTH = 100;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainTabActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}