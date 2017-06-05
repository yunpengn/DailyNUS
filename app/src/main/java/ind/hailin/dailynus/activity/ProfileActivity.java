package ind.hailin.dailynus.activity;


import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import ind.hailin.dailynus.R;
import ind.hailin.dailynus.application.DataApplication;
import ind.hailin.dailynus.entity.Users;
import ind.hailin.dailynus.fragment.UserInfoFragment;
import ind.hailin.dailynus.utils.Constants;

public class ProfileActivity extends AbstractUserInfoActivity {
    public static final String TAG = "ProfileActivity";

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onChange() {
        menu.getItem(0).setEnabled(true);
        menu.getItem(0).setVisible(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_done, menu);
        menu.getItem(0).setEnabled(false);
        menu.getItem(0).setVisible(false);
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

        if (!user.toString().equals(userUnchanged))
            super.uploadUser();
        else
            finish();
    }

    private void sendShortToast(String str) {
        Toast.makeText(this, "Please enter " + str, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
