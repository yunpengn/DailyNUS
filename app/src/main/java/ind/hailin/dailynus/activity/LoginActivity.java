package ind.hailin.dailynus.activity;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import ind.hailin.dailynus.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etUsername, etPassword;
    private CheckBox checkBox;
    private Button btnLogin;
    private TextView tvForgort, tvSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
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
        switch (v.getId()){
            case R.id.login_btn_login:
                break;
            case R.id.login_tv_forgot:
                Snackbar.make(getWindow().getDecorView(),"Sorry, this function does not finish yet", Snackbar.LENGTH_SHORT);
                break;
            case R.id.login_tv_signup:
                //TODO
                break;
        }
    }
}
