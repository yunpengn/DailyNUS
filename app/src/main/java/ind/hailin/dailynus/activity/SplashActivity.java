package ind.hailin.dailynus.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import ind.hailin.dailynus.R;
import ind.hailin.dailynus.application.DataApplication;
import ind.hailin.dailynus.entity.Users;
import ind.hailin.dailynus.exception.DesException;
import ind.hailin.dailynus.handler.QueryJsonHandler;
import ind.hailin.dailynus.utils.Constants;
import ind.hailin.dailynus.utils.MyJsonParsers;
import ind.hailin.dailynus.web.LoginManager;
import ind.hailin.dailynus.web.QueryJsonManager;

public class SplashActivity extends AppCompatActivity {
    public static final String TAG = "SplashActivity";

    private SharedPreferences sp;
    private LoginManager loginManager;
    private QueryJsonManager queryJsonManager;

    private final Handler loginHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.LOGIN_SUCCESS:
                    String requestBody = (String) msg.obj;
                    queryUserData(requestBody);
                    break;
                default:
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                    break;
            }
        }
    };

    private final Handler userInfoHandler = new QueryJsonHandler() {
        @Override
        public void success(Message msg) {
            InputStream inputStream = (InputStream) msg.obj;
            try {
                Users user = MyJsonParsers.getEncryptedUserInfo(inputStream);
                DataApplication.getApplication().setUser(user);
                Log.d(TAG, "user: "+user.toString());

                if (user.getGender() == null || user.getGender().isEmpty()){
                    startActivity(new Intent(SplashActivity.this, FormActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                }
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            } catch (IOException e) {
                e.printStackTrace();
                failure(msg);
            } catch (DesException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null)
                        inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void failure(Message msg) {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        initHttpManager();
        checkRememberMe();
    }

    private void initHttpManager(){
        loginManager = new LoginManager();
        queryJsonManager = new QueryJsonManager(3000, Constants.JSON_TYPE_USER);
    }

    private void checkRememberMe() {
        String storedUsername = sp.getString("username", null);
        String encryptedPassword = sp.getString("password", null);

        if (storedUsername == null || storedUsername.isEmpty()) {
            Message message = Message.obtain();
            message.what = Constants.NO_REMEMBER_ME;
            loginHandler.sendMessage(message);
            return;
        }
        loginManager.queryLogin(this, loginHandler, storedUsername, encryptedPassword, true);
    }

    private void queryUserData(String requestBody) {
        String username = requestBody.substring(0, requestBody.indexOf(":"));
        queryJsonManager.query(this, userInfoHandler, username);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loginManager != null) loginManager.stop();
        if (queryJsonManager != null) queryJsonManager.stop();
    }
}
