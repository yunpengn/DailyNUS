package ind.hailin.dailynus.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ind.hailin.dailynus.R;
import ind.hailin.dailynus.utils.Constants;
import ind.hailin.dailynus.web.LoginManager;

public class SplashActivity extends AppCompatActivity {
    public static final String TAG = "SplashActivity";

    private SharedPreferences sp;
    private LoginManager loginManager;

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.LOGIN_SUCCESS:
                    String requestBody = (String) msg.obj;
                    queryUserData(requestBody);
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    break;
                default:
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    break;
            }
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        checkRememberMe();
    }


    private void checkRememberMe() {
        String storedUsername = sp.getString("username", null);
        String encryptedPassword = sp.getString("password", null);

        if (storedUsername == null || storedUsername.isEmpty()) {
            Message message = Message.obtain();
            message.what = Constants.NO_REMEMBER_ME;
            handler.sendMessage(message);
            return;
        }
        loginManager = new LoginManager();
        loginManager.queryLogin(this, handler, storedUsername, encryptedPassword, true);
    }

    private void queryUserData(String requestBody) {
        //TODO
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //if(loginManager!=null)loginManager.stop();
    }
}
