package ind.hailin.dailynus.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ind.hailin.dailynus.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //get data from server
        queryServer();
        //intent to HomeActivity
        jumpHomeActivity();
    }

    private void queryServer() {
        //TODO
    }

    private void jumpHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        //TODO bundle info
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


}
