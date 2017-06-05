package ind.hailin.dailynus.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import ind.hailin.dailynus.R;
import ind.hailin.dailynus.application.DataApplication;
import ind.hailin.dailynus.entity.Users;
import ind.hailin.dailynus.fragment.LoadingPageFragment;
import ind.hailin.dailynus.handler.UploadHandler;
import ind.hailin.dailynus.utils.Constants;
import ind.hailin.dailynus.web.UploadManager;

public class WebViewActivity extends AppCompatActivity implements LoadingPageFragment.OnFragmentInteractionListener{
    public static final String TAG = "WebViewActivity";

    private FragmentManager fragmentManager;
    private UploadManager uploadCourseManager;

    private WebView webView;

    private String courseUrl;
    private String basicUrl = "https://nusmods.com/timetable/2016-2017/sem2?X=";
    private String urlCheck = "https://nusmods.com/timetable/2016-2017/sem2";

    private Handler uploadCourseHandler = new UploadHandler() {
        @Override
        public void success(Message msg) {
            DataApplication.getApplication().getUser().setCourse(webView.getUrl());
            Toast.makeText(WebViewActivity.this, "Change success", Toast.LENGTH_SHORT).show();
            finish();
        }
        @Override
        public void failure(Message msg) {
            Toast.makeText(WebViewActivity.this, "Failed, pls try later", Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        courseUrl = DataApplication.getApplication().getUser().getCourse();
        if (courseUrl == null || courseUrl.isEmpty()) courseUrl = basicUrl;

        uploadCourseManager = new UploadManager(Constants.UPLOAD_TYPE_MODULE_GROUP);

        initToolBar();
        initWebView();
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initWebView() {
        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (!url.contains(urlCheck))
                    view.loadUrl(courseUrl);
            }
        });

        webView.loadUrl(courseUrl);
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
                uploadCourse();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadCourse() {
        fragmentManager = getFragmentManager();

        LoadingPageFragment loadingPageFragment = LoadingPageFragment.newInstance();
        loadingPageFragment.setOnFragmentInteractionListener(this, Constants.ACTION_UPLOAD);
        loadingPageFragment.setStyle(LoadingPageFragment.STYLE_TRANSPARENT);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.webview_container, loadingPageFragment, LoadingPageFragment.TAG_TRANSPARENT);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(int actionCode) {
        switch (actionCode) {
            case Constants.ACTION_UPLOAD:
                String requestParam = webView.getUrl();
                Log.d(TAG, "requestParam: "+requestParam);
                uploadCourseManager.queryUploadObject(this, uploadCourseHandler,
                        DataApplication.getApplication().getUser().getUsername(), requestParam);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
