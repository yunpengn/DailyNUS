package ind.hailin.dailynus.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ind.hailin.dailynus.R;
import ind.hailin.dailynus.entity.Users;

public class FormActivity extends AbstractUserInfoActivity{
    public static final  String TAG = "FormActivity";

    private String inputUsername, inputPassword, inputNickname;
    private Users mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDataFromIntent();
        initUser();
        super.initToolBar(inputNickname);
        this.initView();
    }

    private void initDataFromIntent() {
        Intent signupIntent = getIntent();
        inputUsername = signupIntent.getStringExtra("username");
        inputPassword = signupIntent.getStringExtra("password");
        inputNickname = signupIntent.getStringExtra("name");
    }

    private void initUser(){
        mUser = new Users();
        mUser.setUsername(inputUsername);
        mUser.setPassword(inputPassword);
        mUser.setNickName(inputNickname);
        super.setUser(mUser);
    }

    @Override
    protected void initView() {
        super.initView();
        tvUsername.setText(inputUsername);
        if(mUser.getUsername().contains("@")){
            tvEmail.setText(mUser.getUsername());
        }
    }

    @Override
    public void onClick(View v) {
        super.doOnClick(v);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.signup_menu_next:
                //TODO
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
