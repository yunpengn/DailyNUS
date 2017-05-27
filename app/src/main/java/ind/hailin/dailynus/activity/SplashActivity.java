package ind.hailin.dailynus.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;

import ind.hailin.dailynus.R;
import ind.hailin.dailynus.application.DataApplication;
import ind.hailin.dailynus.utils.Constants;
import ind.hailin.dailynus.utils.MyUtils;
import ind.hailin.dailynus.web.HttpManager;

public class SplashActivity extends AppCompatActivity {
    public static final String TAG = "SplashActivity";

    private SharedPreferences sp;

    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.LOGIN_SUCCESS:
                    queryUserData();
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    break;
                case Constants.LOGIN_AUTHENTICATION_ERROR:
                case Constants.LOGIN_EXCEPTION:
                case Constants.NO_REMEMBER_ME:
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

        checkRememberMe();
    }


    private void checkRememberMe() {
        if (sp == null)
            sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        String storedUsername = sp.getString("username", null);
        String storedPassword = sp.getString("password", null);

        if(storedUsername == null || storedUsername.isEmpty()){
            Message message = Message.obtain();
            message.what = Constants.NO_REMEMBER_ME;
            handler.sendMessage(message);
        }
        queryLogin(storedUsername, storedPassword);
    }

    private void queryLogin(String username, String password) {
        if(!MyUtils.isInternetConnected(this)){
            Snackbar.make(getWindow().getDecorView(), "Please connect to the Internet", Snackbar.LENGTH_SHORT);
            // wait 2 second for showing SnackBar
            Message message = Message.obtain();
            message.what = Constants.NO_INTERNET_CONNECTION;
            handler.sendMessageDelayed(message, 2000);
            return;
        }

        final String responseBody = username+":"+password;
        DataApplication application= (DataApplication) getApplication();
        try {
            String urlString = application.getProperties("url.properties", "loginUrl");
            // Login
            HttpManager.queryLogin(handler, urlString, responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void queryUserData(){
        //TODO
    }

}
