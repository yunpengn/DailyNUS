package ind.hailin.dailynus.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.List;

import ind.hailin.dailynus.R;
import ind.hailin.dailynus.activity.FormActivity;
import ind.hailin.dailynus.activity.SignupActivity;
import ind.hailin.dailynus.entity.SignupBean;
import ind.hailin.dailynus.utils.Constants;
import ind.hailin.dailynus.utils.Md5Encryption;

/**
 * Created by hailin on 2017/5/31.
 * Fragment which shows at sign up page
 */

public class SignupFragment extends Fragment {
    public static final String TAG = "SignupFragment";

    private EditText etUsername, etPassword, etRepeat, etName;

    public SignupFragment() {
    }

    public static SignupFragment newInstance() {
        SignupFragment fragment = new SignupFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_signup, container, false);

        initView(view);
        return view;
    }

    private void initView(View view) {
        etUsername = (EditText) view.findViewById(R.id.signup_et_username);
        etPassword = (EditText) view.findViewById(R.id.signup_et_password);
        etRepeat = (EditText) view.findViewById(R.id.signup_et_repeatpassword);
        etName = (EditText) view.findViewById(R.id.signup_et_name);
    }

    public SignupBean goNext(){
        String inputUsername = etUsername.getText().toString().trim();
        String inputPassword = etPassword.getText().toString().trim();
        String repeatPassword = etRepeat.getText().toString().trim();
        String inputNickname = etName.getText().toString().trim();

        if (inputUsername == null || inputUsername.isEmpty()){
            Snackbar.make(getActivity().getWindow().getDecorView(), "Please enter username", Snackbar.LENGTH_SHORT).show();
            return null;
        }
        if (inputPassword == null || inputPassword.isEmpty()){
            Snackbar.make(getActivity().getWindow().getDecorView(), "Please enter password", Snackbar.LENGTH_SHORT).show();
            return null;
        }
        if (inputNickname == null || inputNickname.isEmpty()){
            Snackbar.make(getActivity().getWindow().getDecorView(), "Please enter name", Snackbar.LENGTH_SHORT).show();
            return null;
        }
        if (inputUsername.length() > 18) {
            Snackbar.make(getActivity().getWindow().getDecorView(), "Sorry, username must less than 19 characters", Snackbar.LENGTH_SHORT).show();
            return null;
        }
        if (inputUsername.length() < 6) {
            Snackbar.make(getActivity().getWindow().getDecorView(), "Sorry, username must more than 5 characters", Snackbar.LENGTH_SHORT).show();
            return null;
        }
        if (checkUsernameRepeat(inputUsername)){
            Snackbar.make(getActivity().getWindow().getDecorView(), "Sorry, this username is used by others", Snackbar.LENGTH_SHORT).show();
            return null;
        }
        if (checkNicknameRepeat(inputNickname)){
            Snackbar.make(getActivity().getWindow().getDecorView(), "Sorry, this nickname is used by others", Snackbar.LENGTH_SHORT).show();
            return null;
        }
        if (inputPassword.length() > 16) {
            Snackbar.make(getActivity().getWindow().getDecorView(), "Sorry, password must less than 17 characters", Snackbar.LENGTH_SHORT).show();
            return null;
        }
        if (inputPassword.length() < 6) {
            Snackbar.make(getActivity().getWindow().getDecorView(), "Sorry, password must more than 5 characters", Snackbar.LENGTH_SHORT).show();
            return null;
        }
        if(!inputPassword.equals(repeatPassword)){
            Snackbar.make(getActivity().getWindow().getDecorView(), "Sorry, two password are not the same", Snackbar.LENGTH_SHORT).show();
            etRepeat.setText("");
            return null;
        }

        return new SignupBean(inputUsername, inputPassword, inputNickname);
    }

    private boolean checkUsernameRepeat(String inputUsername) {
        String encrypStr = Md5Encryption.encryption(inputUsername);
        List<String> list = ((SignupActivity)getActivity()).getUsersNameMap().get("username");
        return list.contains(encrypStr);
    }

    private boolean checkNicknameRepeat(String inputNickname) {
        String encrypStr = Md5Encryption.encryption(inputNickname);
        List<String> list = ((SignupActivity)getActivity()).getUsersNameMap().get("nickname");
        Log.d(TAG, encrypStr);
        return list.contains(encrypStr);
    }

}
