package ind.hailin.dailynus.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ind.hailin.dailynus.R;
import ind.hailin.dailynus.entity.Users;
import ind.hailin.dailynus.utils.Constants;
import ind.hailin.dailynus.utils.MyJsonParsers;
import ind.hailin.dailynus.utils.MyUtils;
import ind.hailin.dailynus.web.QueryJsonManager;

/**
 * Created by hailin on 2017/5/29.
 */

public abstract class AbstractUserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "Abs_UserInfoActivity";

    protected ImageView ivAvatar;
    protected TextView tvNickname;
    protected TextView tvUsername, tvGender, tvBirthday, tvEmail;
    protected TextView tvFaculty, tvMajor;
    protected TextView tvFirstname, tvLastname, tvPhone;

    private Users user;
    private int handler_flag;

    protected Map<String, List<String>> facultyMajorMap;

    protected final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.JSON_QUERY_SUCCESS:
                    Log.d(TAG, "get facultyMajorMap");
                    facultyMajorMap = (Map<String, List<String>>) msg.obj;
                    Log.d(TAG, facultyMajorMap.toString());
                    dialogWithActv();
                    break;
                case Constants.NO_INTERNET_CONNECTION:
                    Snackbar.make(getWindow().getDecorView(), "Please connect to the Internet", Snackbar.LENGTH_LONG).show();
                    break;
                case Constants.NO_JSON_RETURN:
                case Constants.TARGET_SERVER_ERROR:
                case Constants.JSON_QUERY_EXCEPTION:
                case Constants.JSON_QUERY_URL_ERROR:
                    Snackbar.make(getWindow().getDecorView(), "Sorry, our server is in maintenance, please try later", Snackbar.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
    }

    protected void initToolBar(final String inputNickname) {
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
                    collapsingToolbarLayout.setTitle(inputNickname);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    protected void initView() {
        ivAvatar = (ImageView) findViewById(R.id.userinfo_avatar);
        tvNickname = (TextView) findViewById(R.id.userinfo_nickname);

        findViewById(R.id.userinfo_username).setOnClickListener(this);
        findViewById(R.id.userinfo_gender).setOnClickListener(this);
        findViewById(R.id.userinfo_birthday).setOnClickListener(this);
        findViewById(R.id.userinfo_email).setOnClickListener(this);
        findViewById(R.id.userinfo_faculty).setOnClickListener(this);
        findViewById(R.id.userinfo_major).setOnClickListener(this);
        findViewById(R.id.userinfo_firstname).setOnClickListener(this);
        findViewById(R.id.userinfo_lastname).setOnClickListener(this);
        findViewById(R.id.userinfo_phone).setOnClickListener(this);

        tvUsername = (TextView) findViewById(R.id.userinfo_tv_username);
        tvGender = (TextView) findViewById(R.id.userinfo_tv_gender);
        tvBirthday = (TextView) findViewById(R.id.userinfo_tv_birthday);
        tvEmail = (TextView) findViewById(R.id.userinfo_tv_email);
        tvMajor = (TextView) findViewById(R.id.userinfo_tv_major);
        tvFaculty = (TextView) findViewById(R.id.userinfo_tv_faculty);
        tvFirstname = (TextView) findViewById(R.id.userinfo_tv_firstname);
        tvLastname = (TextView) findViewById(R.id.userinfo_tv_lastname);
        tvPhone = (TextView) findViewById(R.id.userinfo_tv_phone);
    }

    protected void doOnClick(View v) {
        switch (v.getId()) {
            case R.id.userinfo_username:
                break;
            case R.id.userinfo_gender:
                int checkedItem = -1;
                if (user.getGender() != null) { /*change default choose*/
                    if (user.getGender().equals(Constants.gender_choice[0]))
                        checkedItem = 0;
                    else checkedItem = 1;
                }
                AlertDialog.Builder genderBuilder = new AlertDialog.Builder(this);
                genderBuilder.setSingleChoiceItems(Constants.gender_choice, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.setGender(Constants.gender_choice[which]);
                        tvGender.setText(Constants.gender_choice[which]);
                        dialog.dismiss();
                    }
                });
                genderBuilder.show();
                break;
            case R.id.userinfo_birthday:
                Calendar calendar = Calendar.getInstance();
                Date date = user.getBirthday();
                if (date != null)
                    calendar.setTime(date);

                DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        if (!MyUtils.isDateValid(year, month, dayOfMonth)) {
                            Snackbar.make(getWindow().getDecorView(), "Date is invalid", Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        int realMonth = month + 1; // java's month starts from 0
                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
                            Date pickedDate = dateFormat.parse(year + "." + realMonth + "." + dayOfMonth);
                            user.setBirthday(pickedDate);
                            SimpleDateFormat sgFormat = new SimpleDateFormat("dd-MM-yyyy");
                            tvBirthday.setText(sgFormat.format(pickedDate));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                break;
            case R.id.userinfo_email:
                EditText etEmail = new EditText(this);
                etEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                etEmail.setText(tvEmail.getText() != null ? tvEmail.getText() : "");
                dialogWithEditText(etEmail, v.getId(), "E-mail");
                break;
            case R.id.userinfo_firstname:
                EditText etFirstname = new EditText(this);
                etFirstname.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                dialogWithEditText(etFirstname, v.getId(), "First Name");
                break;
            case R.id.userinfo_lastname:
                EditText etLastname = new EditText(this);
                etLastname.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                dialogWithEditText(etLastname, v.getId(), "Last Name");
                break;
            case R.id.userinfo_phone:
                EditText etPhone = new EditText(this);
                etPhone.setInputType(InputType.TYPE_CLASS_NUMBER);
                dialogWithEditText(etPhone, v.getId(), "Phone Number");
                break;
            case R.id.userinfo_faculty:
            case R.id.userinfo_major:
                handler_flag = v.getId();
                queryFacultyDepartments();
                break;
        }
    }

    private void dialogWithEditText(final EditText editText, final int viewId, String titleName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titleName);
        builder.setView(editText);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inputString = editText.getText().toString();
                switch (viewId) {
                    case R.id.userinfo_email:
                        user.setEmail(inputString);
                        tvEmail.setText(inputString);
                        break;
                    case R.id.userinfo_firstname:
                        user.setFirstName(inputString);
                        tvFirstname.setText(inputString);
                        break;
                    case R.id.userinfo_lastname:
                        user.setLastName(inputString);
                        tvLastname.setText(inputString);
                        break;
                    case R.id.userinfo_phone:
                        user.setPhone(inputString);
                        tvPhone.setText(inputString);
                        break;
                }
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

    private void queryFacultyDepartments() {
        if (facultyMajorMap != null) {
            dialogWithActv();
        } else {
            QueryJsonManager queryJsonManager = new QueryJsonManager(2000, Constants.JSON_TYPE_FACULTY_MAJOR);
            queryJsonManager.queryFacultyDepartments(this, handler);
        }
    }

    private void dialogWithActv() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actvLayout = layoutInflater.inflate(R.layout.dialog_item_actv, null);
        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) actvLayout.findViewById(R.id.dialog_item_actv);

        String[] dataArray = null;
        String dialog_title = null;
        if (handler_flag == R.id.userinfo_faculty) {
            Set<String> keySet = facultyMajorMap.keySet();
            dataArray = keySet.toArray(new String[keySet.size()]);
            dialog_title = "Faculty";
        } else if (handler_flag == R.id.userinfo_major) {
            if (user.getFaculty() == null || user.getFaculty().isEmpty()) {
                Snackbar.make(getWindow().getDecorView(), "Please enter faculty first", Snackbar.LENGTH_SHORT).show();
                return;
            }
            List<String> majorList = facultyMajorMap.get(user.getFaculty());
            dataArray = majorList.toArray(new String[majorList.size()]);
            dialog_title = "Major";
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, dataArray);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ((AutoCompleteTextView) v).showDropDown();
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(dialog_title);
        builder.setView(actvLayout);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (handler_flag == R.id.userinfo_faculty) {
                    String facultyName = autoCompleteTextView.getText().toString().trim();
                    user.setFaculty(facultyName);
                    tvFaculty.setText(facultyName);
                } else {
                    String majorName = autoCompleteTextView.getText().toString().trim();
                    user.setMajor(majorName);
                    tvMajor.setText(majorName);
                }
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

    protected void setUser(Users user) {
        this.user = user;
    }
}
