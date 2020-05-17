package com.sky21.booze_vendor.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.sky21.booze_vendor.MainActivity;
import com.sky21.booze_vendor.R;
import com.sky21.booze_vendor.SharedHelper;

public class SplashActivity extends AppCompatActivity implements Runnable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);



        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        Handler handler=new Handler();
        handler.postDelayed(this,3000);
    }

    @Override
    public void run() {

        if(SharedHelper.getKey(SplashActivity.this,"loggedin").equals("true")){
            Intent intent=new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            Intent intent1 = new Intent(SplashActivity.this, WelcomeActivity.class);
            startActivity(intent1);
            finish();
        }
    }
}
