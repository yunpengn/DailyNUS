package ind.hailin.dailynus.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import ind.hailin.dailynus.R;
import ind.hailin.dailynus.application.DataApplication;
import ind.hailin.dailynus.entity.Users;
import ind.hailin.dailynus.handler.QueryJsonHandler;
import ind.hailin.dailynus.handler.UploadHandler;
import ind.hailin.dailynus.utils.CacheUtils;
import ind.hailin.dailynus.utils.Constants;
import ind.hailin.dailynus.utils.MyPicUtils;
import ind.hailin.dailynus.utils.MyUtils;
import ind.hailin.dailynus.web.QueryJsonManager;
import ind.hailin.dailynus.web.UploadManager;

public class AvatarActivity extends AppCompatActivity {
    public static final String TAG = "AvatarActivity";

    private ImageView imageView;

    private Bitmap scaledBitmap;
    private Uri mCropImageUri;
    private int resultFlag;
    private UploadManager picUploadManager;
    private QueryJsonManager queryAvatar;

    private Handler picUploadHandler = new UploadHandler() {
        @Override
        public void success(Message msg) {
            ByteArrayOutputStream outputStream = null;
            try {
                imageView.setImageBitmap(scaledBitmap);
                outputStream = MyPicUtils.convertBitmapToOutputStream(scaledBitmap);
                CacheUtils.cacheAvatarById(AvatarActivity.this,
                        DataApplication.getApplication().getUser().getId(), outputStream.toByteArray());

                Toast.makeText(AvatarActivity.this, "upload success", Toast.LENGTH_SHORT).show();
                resultFlag = Constants.CROP_AND_UPLOAD_SUCCESS;
            } catch (IOException e) {
                e.printStackTrace();
                failure(msg);
            } finally {
                try{
                    if(outputStream!=null)
                        outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void failure(Message msg) {
            Toast.makeText(AvatarActivity.this, "Upload Picture Failed", Toast.LENGTH_SHORT).show();
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
                CacheUtils.cacheAvatarById(AvatarActivity.this,
                        DataApplication.getApplication().getUser().getId(), outputStream.toByteArray());
                imageView.setImageBitmap(bitmap);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);

        initHttpManager();
        initToolBar();
        initView();
    }

    private void initHttpManager() {
        picUploadManager = new UploadManager(Constants.UPLOAD_TYPE_USER);
        queryAvatar = new QueryJsonManager(3000, Constants.JSON_TYPE_USER);
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("resultFlag", resultFlag);
                setResult(Constants.RESULT_AVATAR_TO_USERINFO, intent);
                finish();
            }
        });
    }

    private void initView() {
        imageView = (ImageView) findViewById(R.id.avatar_iv);
        avatarIvLoadPic();

        findViewById(R.id.avatar_tv_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultFlag = Constants.CROP_NO_CHANGE;
                onSelectImage();
            }
        });
    }

    private void avatarIvLoadPic() {
        try {
            Bitmap bitmap = CacheUtils.getAvatarFromCache(this);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            Users user = DataApplication.getApplication().getUser();
            queryAvatar.query(this, avatarHandler, user.getUsername(), "avatar.png");
        }
    }

    private void onSelectImage(){
        CropImage.startPickImageActivity(this);
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setMinCropResultSize(72,72)
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                .setAspectRatio(1,1)
                .setFixAspectRatio(true)
                .start(this);
    }

    private void scaleAndUploadPic(Uri resultUri) {
        ByteArrayOutputStream outputStream = null;
        try {
            // scale bitmap
            Bitmap bm = MyPicUtils.getBitmapFromUri(this, resultUri);
            scaledBitmap = MyPicUtils.scaleBitmapForCache(bm);
            bm = null;
            // convert to outputStream
            outputStream = MyPicUtils.convertBitmapToOutputStream(scaledBitmap);
            // upload
            picUploadManager.queryUploadStream(this, picUploadHandler,
                    DataApplication.getApplication().getUser().getUsername(), "avatar.png", outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(outputStream!=null)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},   CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                scaleAndUploadPic(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                startCropImageActivity(mCropImageUri);
            } else {
                Snackbar.make(getWindow().getDecorView(), "Cancelling, required permissions are not granted", Snackbar.LENGTH_LONG).show();
            }
        }

    }
}
