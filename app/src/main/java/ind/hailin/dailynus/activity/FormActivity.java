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
import android.widget.Toast;

import ind.hailin.dailynus.R;
import ind.hailin.dailynus.application.DataApplication;
import ind.hailin.dailynus.entity.Users;
import ind.hailin.dailynus.fragment.UserInfoFragment;

public class FormActivity extends AbstractUserInfoActivity {
    public static final String TAG = "FormActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signup_menu_next:
                checkUserValidity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkUserValidity() {
        Users user = DataApplication.getApplication().getUser();

        if (user.getGender() == null || user.getGender().isEmpty()) {
            sendShortToast("gender");
            return;
        }
        if (user.getBirthday() == null) {
            sendShortToast("birthday");
            return;
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            sendShortToast("email");
            return;
        }
        if (user.getFaculty() == null || user.getFaculty().isEmpty()) {
            sendShortToast("faculty");
            return;
        }
        if (user.getMajor() == null || user.getMajor().isEmpty()) {
            sendShortToast("major");
            return;
        }

        super.uploadUser();
    }

    private void sendShortToast(String str) {
        Toast.makeText(this, "Please enter " + str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChange() {
    }
}
