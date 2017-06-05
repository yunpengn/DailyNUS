package ind.hailin.dailynus.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import ind.hailin.dailynus.R;
import ind.hailin.dailynus.entity.SignupBean;
import ind.hailin.dailynus.fragment.LoadingPageFragment;
import ind.hailin.dailynus.fragment.SignupFragment;
import ind.hailin.dailynus.handler.QueryJsonHandler;
import ind.hailin.dailynus.handler.UploadHandler;
import ind.hailin.dailynus.utils.Constants;
import ind.hailin.dailynus.utils.MyJsonParsers;
import ind.hailin.dailynus.web.QueryJsonManager;
import ind.hailin.dailynus.web.UploadManager;

public class SignupActivity extends AppCompatActivity implements LoadingPageFragment.OnFragmentInteractionListener {
    public static final String TAG = "SignupActivity";

    private Toolbar toolbar;

    private QueryJsonManager queryJsonManager;
    private UploadManager uploadManager;
    private FragmentManager fragmentManager;
    private Map<String, List<String>> map;
    private SignupBean signupBean;

    private final Handler loadInfoHandler = new QueryJsonHandler() {
        @Override
        public void success(Message msg) {
            InputStream inputStream = (InputStream) msg.obj;
            try {
                map = MyJsonParsers.getAllUsersName(inputStream);
                Log.d(TAG, map.toString());

                SignupFragment signupFragment = SignupFragment.newInstance();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(fragmentManager.findFragmentByTag(LoadingPageFragment.TAG));
                fragmentTransaction.add(R.id.signup_coordinatorLayout, signupFragment, SignupFragment.TAG);
                fragmentTransaction.commit();
            } catch (Exception e) {
                e.printStackTrace();
                failure(msg);
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
        }
    };

    private final Handler uploadHandler = new UploadHandler() {
        @Override
        public void success(Message msg) {
            Toast.makeText(SignupActivity.this, "Successfully Sign up", Toast.LENGTH_LONG).show();
            finish();
        }

        @Override
        public void failure(Message msg) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initHttpManager();
        initToolBar();
        initFragment();
    }

    private void initHttpManager() {
        queryJsonManager = new QueryJsonManager(3000, Constants.JSON_TYPE_ALL_USERSNAME);
        uploadManager = new UploadManager(Constants.UPLOAD_TYPE_SIGNUP);
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

    private void initFragment() {
        fragmentManager = getFragmentManager();

        LoadingPageFragment loadingPageFragment = LoadingPageFragment.newInstance();
        loadingPageFragment.setOnFragmentInteractionListener(this, Constants.ACTION_DOWNLOAD);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.signup_coordinatorLayout, loadingPageFragment, LoadingPageFragment.TAG);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(int actionCode) {
        switch (actionCode) {
            case Constants.ACTION_DOWNLOAD:
                queryJsonManager.query(this, loadInfoHandler);
                break;
            case Constants.ACTION_UPLOAD:
                Log.d(TAG, "ACTION_UPLOAD");
                uploadManager.queryUploadObject(this, uploadHandler, signupBean);
                break;
        }
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
                SignupFragment signupFragment = (SignupFragment) fragmentManager.findFragmentByTag(SignupFragment.TAG);
                if(signupFragment == null) {
                    break;
                }
                SignupBean result = signupFragment.goNext();
                if (result != null) {
                    signupBean = result;
                    Log.d(TAG, "result"+result.toString());

                    LoadingPageFragment loadingPageFragment = LoadingPageFragment.newInstance();
                    loadingPageFragment.setOnFragmentInteractionListener(this, Constants.ACTION_UPLOAD);

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.remove(signupFragment);
                    fragmentTransaction.add(R.id.signup_coordinatorLayout, loadingPageFragment, LoadingPageFragment.TAG);
                    fragmentTransaction.commit();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public Map<String, List<String>> getUsersNameMap() {
        return map;
    }

    /**
     * EditText loses its focus when touching outside
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

}
