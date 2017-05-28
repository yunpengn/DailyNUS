package ind.hailin.dailynus.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import ind.hailin.dailynus.R;
import ind.hailin.dailynus.utils.Constants;

public class SignupActivity extends AppCompatActivity {
    public static final String TAG = "SignupActivity";

    private Toolbar toolbar;
    private EditText etUsername, etPassword, etRepeat, etName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initToolBar();
        initView();
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                builder.setMessage("Give up signing up?");
                builder.setPositiveButton("Give up", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    private void initView() {
        etUsername = (EditText) findViewById(R.id.signup_et_username);
        etPassword = (EditText) findViewById(R.id.signup_et_password);
        etRepeat = (EditText) findViewById(R.id.signup_et_repeatpassword);
        etName = (EditText) findViewById(R.id.signup_et_name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_signup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.signup_menu_next:
                goNext();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goNext(){
        String inputUsername = etUsername.getText().toString().trim();
        String inputPassword = etPassword.getText().toString().trim();
        String repeatPassword = etRepeat.getText().toString().trim();
        String inputNickname = etRepeat.getText().toString().trim();

        if (inputUsername == null || inputUsername.isEmpty()){
            Snackbar.make(getWindow().getDecorView(), "Please enter username", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (inputPassword == null || inputPassword.isEmpty()){
            Snackbar.make(getWindow().getDecorView(), "Please enter password", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (inputNickname == null || inputNickname.isEmpty()){
            Snackbar.make(getWindow().getDecorView(), "Please enter name", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (inputUsername.length() > 18) {
            Snackbar.make(getWindow().getDecorView(), "Sorry, username must less than 19 characters", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (inputUsername.length() < 6) {
            Snackbar.make(getWindow().getDecorView(), "Sorry, username must more than 5 characters", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (checkUsernameRepeat(inputUsername)){
            Snackbar.make(getWindow().getDecorView(), "Sorry, this username is used by others", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (checkNicknameRepeat(inputNickname)){
            Snackbar.make(getWindow().getDecorView(), "Sorry, this name is used by others", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (inputPassword.length() > 16) {
            Snackbar.make(getWindow().getDecorView(), "Sorry, password must less than 17 characters", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (inputPassword.length() < 6) {
            Snackbar.make(getWindow().getDecorView(), "Sorry, password must more than 5 characters", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if(!inputPassword.equals(repeatPassword)){
            Snackbar.make(getWindow().getDecorView(), "Sorry, two password are not the same", Snackbar.LENGTH_SHORT).show();
            etRepeat.setText("");
            return;
        }

        Log.d(TAG, "new Intent");
        Intent intent = new Intent(SignupActivity.this, FormActivity.class);
        intent.putExtra("username", inputUsername);
        intent.putExtra("password", inputPassword);
        intent.putExtra("name", inputNickname);
        Log.d(TAG, "start");
        startActivityForResult(intent, Constants.REQUEST_SIGNUP_TO_FORM);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private boolean checkUsernameRepeat(String inputUsername) {
        //TODO
        return false;
    }

    private boolean checkNicknameRepeat(String inputNickname) {
        //TODO
        return false;
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
