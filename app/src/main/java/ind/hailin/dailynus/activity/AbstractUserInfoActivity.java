package ind.hailin.dailynus.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import ind.hailin.dailynus.R;
import ind.hailin.dailynus.application.DataApplication;
import ind.hailin.dailynus.entity.Users;
import ind.hailin.dailynus.fragment.LoadingPageFragment;
import ind.hailin.dailynus.fragment.UserInfoFragment;
import ind.hailin.dailynus.handler.QueryJsonHandler;
import ind.hailin.dailynus.handler.UploadHandler;
import ind.hailin.dailynus.utils.CacheUtils;
import ind.hailin.dailynus.utils.Constants;
import ind.hailin.dailynus.utils.MyJsonParsers;
import ind.hailin.dailynus.utils.MyPicUtils;
import ind.hailin.dailynus.web.QueryJsonManager;
import ind.hailin.dailynus.web.UploadManager;

/**
 * Created by hailin on 2017/5/29.
 * Abstract activity for FormActivity and ProfileActivity
 */

public abstract class AbstractUserInfoActivity extends AppCompatActivity implements View.OnClickListener,
        LoadingPageFragment.OnFragmentInteractionListener, UserInfoFragment.OnProfileChangeListener {
    public static final String TAG = "Abs_UserInfoActivity";

    private FragmentManager fragmentManager;
    private QueryJsonManager queryFacultyMajor, queryAvatar;
    private UploadManager uploadUserManager;

    private ImageView ivAvatar;
    private TextView tvNickname;

    private Map<String, List<String>> facultyMajorMap;
    protected String userUnchanged;
    protected boolean isProfileChange;

    private Handler facultyMajorHandler = new QueryJsonHandler() {
        @Override
        public void success(Message msg) {
            InputStream inputStream = (InputStream) msg.obj;
            try {
                facultyMajorMap = MyJsonParsers.getFacultyMajor(inputStream);
                Log.d(TAG, facultyMajorMap.toString());

                showUserInfoFragment();
            } catch (IOException e) {
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
            String checkStringFaculty = DataApplication.getApplication().getUser().getFaculty();
            if (checkStringFaculty != null && !checkStringFaculty.isEmpty()) {
                showUserInfoFragment();
            } else {
                Snackbar.make(getWindow().getDecorView(), "Sorry, there are some small problems, please try again", Snackbar.LENGTH_LONG).show();
            }
        }
    };

    private Handler avatarHandler = new QueryJsonHandler() {
        @Override
        public void success(Message msg) {
            InputStream inputStream = null;
            ByteArrayOutputStream outputStream = null;
            try {
                inputStream = (InputStream) msg.obj;
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                outputStream = MyPicUtils.convertBitmapToOutputStream(bitmap);
                CacheUtils.cacheAvatarById(AbstractUserInfoActivity.this,
                        DataApplication.getApplication().getUser().getId(), outputStream.toByteArray());
                ivAvatar.setImageBitmap(MyPicUtils.getCroppedBitmap(bitmap));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null)
                        inputStream.close();
                    if (outputStream != null)
                        outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void failure(Message msg) {
        }
    };

    private Handler uploadUserHandler = new UploadHandler() {
        @Override
        public void success(Message msg) {
            if (AbstractUserInfoActivity.this instanceof FormActivity){
                startActivity(new Intent(AbstractUserInfoActivity.this, HomeActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            } else {
                Toast.makeText(AbstractUserInfoActivity.this, "update success", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        @Override
        public void failure(Message msg) {
            Snackbar.make(getWindow().getDecorView(), "Sorry, there are some small problems, please try again", Snackbar.LENGTH_LONG).show();
            closeUploadingFragment();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        userUnchanged = DataApplication.getApplication().getUser().toString();
        isProfileChange = false;

        initHttpManager();
        initToolBar();
        initView();
        initFragment();
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
                if (AbstractUserInfoActivity.this instanceof ProfileActivity && isProfileChange){
                    setResult(Constants.RESULT_PROFILE_TO_HOME_CHANGE);
                }
                Users curUser = DataApplication.getApplication().getUser();
                if (curUser.toString().equals(userUnchanged)) { /*nothing changed*/
                    finish();
                } else {
                    goBackDialog();
                }
            }
        });
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.userinfo_collapsingtoolbarlayout);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.userinfo_appbarlayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    String nickname = DataApplication.getApplication().getUser().getNickName();
                    collapsingToolbarLayout.setTitle(nickname);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private void initView() {
        ivAvatar = (ImageView) findViewById(R.id.userinfo_avatar);
        tvNickname = (TextView) findViewById(R.id.userinfo_nickname);
        avatarIvLoadPic();
        tvNickname.setText(DataApplication.getApplication().getUser().getNickName());

        ivAvatar.setOnClickListener(this);
//        tvNickname.setOnClickListener(this); // nickname cannot be changed
    }

    private void initHttpManager() {
        queryFacultyMajor = new QueryJsonManager(3000, Constants.JSON_TYPE_FACULTY_MAJOR);
        queryAvatar = new QueryJsonManager(3000, Constants.JSON_TYPE_USER);

        uploadUserManager = new UploadManager(Constants.UPLOAD_TYPE_USER);
    }

    private void initFragment() {
        fragmentManager = getFragmentManager();

        LoadingPageFragment loadingPageFragment = LoadingPageFragment.newInstance();
        loadingPageFragment.setOnFragmentInteractionListener(this, Constants.ACTION_DOWNLOAD);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.userinfo_fragment_container, loadingPageFragment, LoadingPageFragment.TAG);
        fragmentTransaction.commit();
    }

    private void goBackDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AbstractUserInfoActivity.this);
        builder.setMessage("You may lose your setting");
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userinfo_avatar:
                startActivityForResult(new Intent(this, AvatarActivity.class), Constants.REQUEST_USERINFO_TO_AVATAR);
                break;
//            case R.id.userinfo_nickname:
//                dialogWithEditText();
//                isProfileChange = true;
//                break;
        }
    }

    @Override
    public void onFragmentInteraction(int actionCode) {
        switch (actionCode) {
            case Constants.ACTION_DOWNLOAD:
                queryFacultyMajor.query(this, facultyMajorHandler);
                break;
            case Constants.ACTION_UPLOAD:
                Users user = DataApplication.getApplication().getUser();
                uploadUserManager.queryUploadObject(this, uploadUserHandler, user.getUsername(), user);
                break;
        }
    }

    public void showUserInfoFragment() {
        Fragment fragment = fragmentManager.findFragmentByTag(LoadingPageFragment.TAG);
        UserInfoFragment userInfoFragment = UserInfoFragment.newInstance();
        userInfoFragment.setFacultyMajorMap(facultyMajorMap);
        userInfoFragment.setOnProfileChangeListener(this);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.add(R.id.userinfo_fragment_container, userInfoFragment, UserInfoFragment.TAG);
        fragmentTransaction.commit();
    }

    private void avatarIvLoadPic() {
        try {
            Bitmap bitmap = CacheUtils.getAvatarFromCache(this);
            ivAvatar.setImageBitmap(MyPicUtils.getCroppedBitmap(bitmap));
        } catch (Exception e) {
            e.printStackTrace();
            Users user = DataApplication.getApplication().getUser();
            queryAvatar.query(this, avatarHandler, user.getUsername(), "avatar.png");
        }
    }

//    private void dialogWithEditText() {
//        final EditText etNickname = new EditText(this);
//        etNickname.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Nickname");
//        builder.setView(etNickname);
//        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String nickname = etNickname.getText().toString();
//
//                Users user = DataApplication.getApplication().getUser();
//                user.setNickName(nickname);
//                DataApplication.getApplication().setUser(user);
//
//                tvNickname.setText(nickname);
//                dialog.dismiss();
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.show();
//    }

    protected void uploadUser() {
        LoadingPageFragment loadingPageFragment = LoadingPageFragment.newInstance();
        loadingPageFragment.setStyle(LoadingPageFragment.STYLE_TRANSPARENT);
        loadingPageFragment.setOnFragmentInteractionListener(this, Constants.ACTION_UPLOAD);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.userinfo_fragment_loading_container,
                loadingPageFragment, LoadingPageFragment.TAG_TRANSPARENT);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void closeUploadingFragment() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragmentManager.findFragmentByTag(LoadingPageFragment.TAG_TRANSPARENT));
        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_USERINFO_TO_AVATAR) {
            int resultFlag = data.getIntExtra("resultFlag", Constants.CROP_NO_CHANGE);
            if (resultFlag == Constants.CROP_AND_UPLOAD_SUCCESS) {
                avatarIvLoadPic();
                isProfileChange = true;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
