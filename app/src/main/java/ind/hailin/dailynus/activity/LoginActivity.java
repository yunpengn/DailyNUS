package ind.hailin.dailynus.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import ind.hailin.dailynus.R;
import ind.hailin.dailynus.application.DataApplication;
import ind.hailin.dailynus.entity.Users;
import ind.hailin.dailynus.exception.DesException;
import ind.hailin.dailynus.handler.QueryJsonHandler;
import ind.hailin.dailynus.utils.Constants;
import ind.hailin.dailynus.utils.DesEncryption;
import ind.hailin.dailynus.utils.MyJsonParsers;
import ind.hailin.dailynus.web.LoginManager;
import ind.hailin.dailynus.web.QueryJsonManager;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "LoginActivity";

    private SharedPreferences sp;
    private LoginManager loginManager;
    private QueryJsonManager queryJsonManager;

    private EditText etUsername, etPassword;
    private CheckBox checkBox;
    private Button btnLogin;
    private TextView tvForgort, tvSignup;

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.LOGIN_SUCCESS:
                    String requestBody = (String) msg.obj;
                    storeUsernameAndPassword(requestBody);
                    queryUserData(requestBody);
                    break;
                case Constants.LOGIN_AUTHENTICATION_ERROR:
                    Snackbar.make(getWindow().getDecorView(), "Sorry, wrong username/password", Snackbar.LENGTH_LONG).show();
                    clearStoredUsernameAndPassword();
                    break;
                case Constants.LOGIN_EXCEPTION:
                    Snackbar.make(getWindow().getDecorView(), "Sorry, there are some small problems, please try again", Snackbar.LENGTH_LONG).show();
                    clearStoredUsernameAndPassword();
                    break;
                case Constants.NO_INTERNET_CONNECTION:
                    Snackbar.make(getWindow().getDecorView(), "Please connect to the Internet", Snackbar.LENGTH_LONG).show();
                    break;
                case Constants.SERVER_ERROR:
                    Snackbar.make(getWindow().getDecorView(), "Sorry, our server is in maintenance, please try later", Snackbar.LENGTH_LONG).show();
                    break;
            }
        }
    };

    private final Handler userInfoHandler = new QueryJsonHandler() {
        @Override
        public void success(Message msg) {
            Log.d(TAG, "userInfoHandler success");
            InputStream inputStream = (InputStream) msg.obj;
            try {
                Users user = MyJsonParsers.getEncryptedUserInfo(inputStream);
                DataApplication.getApplication().setUser(user);
                Log.d(TAG, "user: "+user.toString());

                if (user.getGender() == null || user.getGender().isEmpty()){
                    startActivity(new Intent(LoginActivity.this, FormActivity.class));
                } else {
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
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
            Snackbar.make(getWindow().getDecorView(), "Sorry, there are some small problems, please try again", Snackbar.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        initHttpManager();
        initView();
    }

    private void initHttpManager(){
        loginManager = new LoginManager();
        queryJsonManager = new QueryJsonManager(3000, Constants.JSON_TYPE_USER);
    }

    private void initView() {
        etUsername = (EditText) findViewById(R.id.login_et_username);
        etPassword = (EditText) findViewById(R.id.login_et_password);
        checkBox = (CheckBox) findViewById(R.id.login_checkBox);
        btnLogin = (Button) findViewById(R.id.login_btn_login);
        tvForgort = (TextView) findViewById(R.id.login_tv_forgot);
        tvSignup = (TextView) findViewById(R.id.login_tv_signup);

        btnLogin.setOnClickListener(this);
        tvForgort.setOnClickListener(this);
        tvSignup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn_login:
                // check the validity of username and password
                String inputUsername = etUsername.getText().toString().trim();
                String inputPassword = etPassword.getText().toString().trim();

                if (inputUsername == null || inputUsername.isEmpty()){
                    Snackbar.make(getWindow().getDecorView(), "Please enter username", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (inputPassword == null || inputPassword.isEmpty()){
                    Snackbar.make(getWindow().getDecorView(), "Please enter password", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (inputUsername.length() > 18) {
                    Snackbar.make(getWindow().getDecorView(), "Sorry, username too long", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (inputPassword.length() > 16) {
                    Snackbar.make(getWindow().getDecorView(), "Sorry, password too long", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (checkBox.isChecked()) { /*use nus email*/
                    if (!inputUsername.endsWith("@u.nus.edu")) {
                        Snackbar.make(getWindow().getDecorView(), "Sorry, invalid nus email address", Snackbar.LENGTH_SHORT).show();
                        return;
                    } else {
                        // Login with NUS email (identify by NUS api)
                    }
                }
                // login use my own server
                loginManager.queryLogin(this, handler, inputUsername, inputPassword, false);
                break;
            case R.id.login_tv_forgot:
                Snackbar.make(getWindow().getDecorView(), "Sorry, this function does not finish yet", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.login_tv_signup:
                startActivity(new Intent(this, SignupActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }
    }

    private void queryUserData(String requestBody) {
        Log.d(TAG, "requestBody: "+requestBody);
        String username = requestBody.substring(0, requestBody.indexOf(":"));
        queryJsonManager.query(this, userInfoHandler, username);
    }

    private void storeUsernameAndPassword(String requestBody){
        try {
            String storedUsername = requestBody.substring(0, requestBody.indexOf(":"));
            String storedPassword = requestBody.substring(requestBody.indexOf(":") + 1);
            storedPassword = new String(DesEncryption.encryption(storedPassword), "utf-8");
            sp.edit().putString("username", storedUsername)
                    .putString("password", storedPassword)
                    .apply();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "store username and password error");
        }
    }

    private void clearStoredUsernameAndPassword(){
        sp.edit().remove("username").remove("password").apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //if(loginManager!=null)loginManager.stop();
    }

    /**
     * EditText loses its focus when touching outside
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
