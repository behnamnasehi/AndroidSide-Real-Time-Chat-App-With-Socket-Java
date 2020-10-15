package android.behnamnasehi.chatApp.activity;

import android.os.Bundle;
import android.os.Handler;
import android.behnamnasehi.chatApp.R;
import android.behnamnasehi.chatApp.databinding.ActivitySplashBinding;
import android.behnamnasehi.chatApp.dataholder.SessionManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class SplashActivity extends AppCompatActivity {
    ActivitySplashBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isLoggedIn = new SessionManager(SplashActivity.this).isLoggedIn();
                String publicKey = new SessionManager(SplashActivity.this).getPublicKey();
                if (isLoggedIn && publicKey != null) {
                    MainActivity.start(SplashActivity.this);
                } else {
                    AuthLoginActivity.start(SplashActivity.this);
                }
                finish();
            }
        }, 1500);
    }
}
