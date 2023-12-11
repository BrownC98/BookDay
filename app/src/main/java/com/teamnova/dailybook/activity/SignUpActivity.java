package com.teamnova.dailybook.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.teamnova.dailybook.R;
import com.teamnova.dailybook.data.DataManager;
import com.teamnova.dailybook.dto.User;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * 회원가입 액티비티
 */
public class SignUpActivity extends AppCompatActivity {

    EditText et_email;
    EditText et_pw;
    EditText et_rePw;
    EditText et_nick;
    Button btn_submit;
    TextView tv_email;
    TextView tv_pw;
    TextView tv_rePw;
    TextView tv_nick;

    Toast toast;
    InputMethodManager imm;

    ArrayList<android.util.Pair<EditText, TextView>> checkList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        et_email = findViewById(R.id.editText_signup_email);
        et_pw = findViewById(R.id.editText_signup_password);
        et_rePw = findViewById(R.id.editText_signup_repassword);
        et_nick = findViewById(R.id.editText_signup_nickname);
        btn_submit = findViewById(R.id.button_signup_submit);

        tv_email = findViewById(R.id.textView_signup_emailMsg);
        tv_pw = findViewById(R.id.textView_signup_pwMsg);
        tv_rePw = findViewById(R.id.textView_signup_rePwMsg);
        tv_nick = findViewById(R.id.textView_signup_nickMsg);

        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        btn_submit.setOnClickListener(v -> {
            submit();
        });

        // 이메일 유효성검사
        et_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = et_email.getText().toString();
                TextView tv = tv_email;

                if (input.length() == 0) {
                    fail(tv, "필수입력 항목입니다.");
                    return;
                }

                boolean isMatch = Pattern.matches("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$", input);
                if (!isMatch) {
                    fail(tv, "올바른 이메일 형식이 아닙니다.");
                    return;
                }

                boolean isDuplicate = DataManager.getInstance().containsUser(input);
                if (isDuplicate) {
                    fail(tv, "이 이메일은 사용할 수 없습니다.");
                    return;
                }

                pass(tv, "사용할 수 있는 이메일입니다.");
            }
        });

        // 비밀번호는 입력여부만 확인함
        et_pw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = et_pw.getText().toString();
                TextView tv = tv_pw;

                if (input.length() == 0) {
                    fail(tv, "필수입력 항목입니다.");
                    return;
                }
                pass(tv, null);
            }
        });

        // 비밀번호 확인 유효성 검사
        et_rePw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = et_rePw.getText().toString();
                TextView tv = tv_rePw;

                if (input.length() == 0) {
                    fail(tv, "필수입력 항목입니다.");
                    return;
                }

                if (!input.equals(et_pw.getText().toString())) {
                    fail(tv, "비밀번호가 일치하지 않습니다.");
                    return;
                }

                pass(tv, "비밀번호 확인완료");
            }
        });

        // 닉네임 검사
        et_nick.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = et_nick.getText().toString();
                TextView tv = tv_nick;

                if (input.length() == 0) {
                    fail(tv, "필수입력 항목입니다.");
                    return;
                }

                boolean isDuplicate = DataManager.getInstance().containNickName(input);
                if (isDuplicate) {
                    fail(tv, "이 닉네임은 사용할 수 없습니다.");
                    return;
                }

                pass(tv, "사용할 수 있는 닉네임입니다.");
            }
        });

        checkList = new ArrayList<>();
        checkList.add(new Pair<>(et_email, tv_email));
        checkList.add(new Pair<>(et_pw, tv_pw));
        checkList.add(new Pair<>(et_rePw, tv_rePw));
        checkList.add(new Pair<>(et_nick, tv_nick));
    }

    // 유효성 검사 실패
    private void fail(TextView tv, String msg) {
        tv.setText(msg);
        tv.setTextColor(getColor(R.color.warn));
        tv.setVisibility(View.VISIBLE);
    }

    // 유효성 검사 통과
    private void pass(TextView tv, String msg) {
        tv.setText(msg);
        tv.setTextColor(getColor(R.color.ok));
        tv.setVisibility(View.VISIBLE);
    }

    // 입력정보 제출
    private void submit() {

        // 정보수정 요청
        for (Pair<EditText, TextView> p : checkList){
            EditText et = p.first;
            TextView tv = p.second;

            if (tv.getCurrentTextColor() == getColor(R.color.warn) || et.getText().toString().length() == 0) {
                String input = et.getText().toString();
                // et에 변화줘서 유효성 검사 실행되도록 함
                et.setText(input + " ");
                et.setText(input);
                et.requestFocus();
                imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
                return;
            }
        }

        String email = et_email.getText().toString();
        String pw = et_pw.getText().toString();
        String nick = et_nick.getText().toString();

        User newUser = new User(email, pw, nick);
        int result = DataManager.getInstance().createUser(newUser);

        if (result == 0) {
            Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "회원가입 실패", Toast.LENGTH_LONG).show();
        }
        finish();
    }
}