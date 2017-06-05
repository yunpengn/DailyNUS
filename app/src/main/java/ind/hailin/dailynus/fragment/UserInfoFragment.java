package ind.hailin.dailynus.fragment;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ind.hailin.dailynus.R;
import ind.hailin.dailynus.activity.AbstractUserInfoActivity;
import ind.hailin.dailynus.application.DataApplication;
import ind.hailin.dailynus.entity.Users;
import ind.hailin.dailynus.utils.Constants;
import ind.hailin.dailynus.utils.MyUtils;

/**
 * Created by hailin on 2017/6/1.
 * Fragment which shows userinfo
 */

public class UserInfoFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "UserInfoFragment";

    private TextView tvUsername, tvGender, tvBirthday, tvEmail;
    private TextView tvFaculty, tvMajor;
    private TextView tvFirstname, tvLastname, tvPhone;

    private Users user;
    private Map<String, List<String>> facultyMajorMap;
    private OnProfileChangeListener listener;

    public UserInfoFragment() {
    }

    public static UserInfoFragment newInstance() {
        UserInfoFragment fragment = new UserInfoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = DataApplication.getApplication().getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_user_info, container, false);

        initView(view);
        return view;
    }

    public void initView(View v) {
        v.findViewById(R.id.userinfo_username).setOnClickListener(this);
        v.findViewById(R.id.userinfo_gender).setOnClickListener(this);
        v.findViewById(R.id.userinfo_birthday).setOnClickListener(this);
        v.findViewById(R.id.userinfo_email).setOnClickListener(this);
        v.findViewById(R.id.userinfo_faculty).setOnClickListener(this);
        v.findViewById(R.id.userinfo_major).setOnClickListener(this);
        v.findViewById(R.id.userinfo_firstname).setOnClickListener(this);
        v.findViewById(R.id.userinfo_lastname).setOnClickListener(this);
        v.findViewById(R.id.userinfo_phone).setOnClickListener(this);

        tvUsername = (TextView) v.findViewById(R.id.userinfo_tv_username);
        tvGender = (TextView) v.findViewById(R.id.userinfo_tv_gender);
        tvBirthday = (TextView) v.findViewById(R.id.userinfo_tv_birthday);
        tvEmail = (TextView) v.findViewById(R.id.userinfo_tv_email);
        tvMajor = (TextView) v.findViewById(R.id.userinfo_tv_major);
        tvFaculty = (TextView) v.findViewById(R.id.userinfo_tv_faculty);
        tvFirstname = (TextView) v.findViewById(R.id.userinfo_tv_firstname);
        tvLastname = (TextView) v.findViewById(R.id.userinfo_tv_lastname);
        tvPhone = (TextView) v.findViewById(R.id.userinfo_tv_phone);

        tvUsername.setText(user.getUsername());
        tvGender.setText(user.getGender());
        if (user.getBirthday() != null) {
            DateFormat dateFormat = DateFormat.getDateInstance();
            tvBirthday.setText(dateFormat.format(user.getBirthday()));
        }
        if (user.getEmail() == null && user.getUsername().contains("@"))
            tvEmail.setText(user.getUsername());
        else
            tvEmail.setText(user.getEmail());
        tvMajor.setText(user.getMajor());
        tvFaculty.setText(user.getFaculty());
        tvFirstname.setText(user.getFirstName());
        tvLastname.setText(user.getLastName());
        tvPhone.setText(user.getPhone());
    }

    @Override
    public void onClick(View v) {
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
                AlertDialog.Builder genderBuilder = new AlertDialog.Builder(getActivity());
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        if (!MyUtils.isDateValid(year, month, dayOfMonth)) {
                            Snackbar.make(getActivity().getWindow().getDecorView(), "Date is invalid", Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        int realMonth = month + 1; // java's month starts from 0
                        try {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
                            Date pickedDate = simpleDateFormat.parse(year + "." + realMonth + "." + dayOfMonth);
                            user.setBirthday(pickedDate);
                            DateFormat dateFormat = DateFormat.getDateInstance();
                            tvBirthday.setText(dateFormat.format(pickedDate));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                break;
            case R.id.userinfo_email:
                EditText etEmail = new EditText(getActivity());
                etEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                etEmail.setText(tvEmail.getText() != null ? tvEmail.getText() : "");
                dialogWithEditText(etEmail, v.getId(), "E-mail");
                break;
            case R.id.userinfo_firstname:
                EditText etFirstname = new EditText(getActivity());
                etFirstname.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                dialogWithEditText(etFirstname, v.getId(), "First Name");
                break;
            case R.id.userinfo_lastname:
                EditText etLastname = new EditText(getActivity());
                etLastname.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                dialogWithEditText(etLastname, v.getId(), "Last Name");
                break;
            case R.id.userinfo_phone:
                EditText etPhone = new EditText(getActivity());
                etPhone.setInputType(InputType.TYPE_CLASS_NUMBER);
                dialogWithEditText(etPhone, v.getId(), "Phone Number");
                break;
            case R.id.userinfo_faculty:
            case R.id.userinfo_major:
                if (facultyMajorMap != null) {
                    dialogWithActv(v.getId());
                }
                break;
        }
        DataApplication.getApplication().setUser(user);
        if (listener != null) {
            listener.onChange();
            Log.d(TAG, "onchange");
        }
    }

    private void dialogWithEditText(final EditText editText, final int viewId, String titleName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

    private void dialogWithActv(final int viewId) {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actvLayout = layoutInflater.inflate(R.layout.dialog_item_actv, null);
        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) actvLayout.findViewById(R.id.dialog_item_actv);

        String[] dataArray = null;
        String dialog_title = null;
        if (viewId == R.id.userinfo_faculty) {
            Set<String> keySet = facultyMajorMap.keySet();
            dataArray = keySet.toArray(new String[keySet.size()]);
            dialog_title = "Faculty";
        } else if (viewId == R.id.userinfo_major) {
            if (user.getFaculty() == null || user.getFaculty().isEmpty()) {
                Snackbar.make(getActivity().getWindow().getDecorView(), "Please enter faculty first", Snackbar.LENGTH_SHORT).show();
                return;
            }
            List<String> majorList = facultyMajorMap.get(user.getFaculty());
            dataArray = majorList.toArray(new String[majorList.size()]);
            dialog_title = "Major";
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, dataArray);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(dialog_title);
        builder.setView(actvLayout);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (viewId == R.id.userinfo_faculty) {
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

    public void setFacultyMajorMap(Map<String, List<String>> facultyMajorMap) {
        this.facultyMajorMap = facultyMajorMap;
    }

    public void setOnProfileChangeListener(OnProfileChangeListener listener) {
        this.listener = listener;
    }

    public interface OnProfileChangeListener {
        void onChange();
    }
}
