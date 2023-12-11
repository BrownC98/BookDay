package com.teamnova.dailybook.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.teamnova.dailybook.R;
import com.teamnova.dailybook.data.DataManager;
import com.teamnova.dailybook.dto.User;

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
    TextView tv_rePw;
    TextView tv_nick;

    Toast toast;
    boolean isOk; // 입력값 유효성 플래그

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
        tv_rePw = findViewById(R.id.textView_signup_rePwMsg);
        tv_nick = findViewById(R.id.textView_signup_nickMsg);

        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);

        btn_submit.setOnClickListener(v -> {
            // 닉네임 중복검사는 여기서도 해줘야 한다.
            String nickName = et_nick.getText().toString();

            boolean isDuplicate = DataManager.getInstance().containNickName(nickName);
            String msg;
            if (isDuplicate) {    // 경고글 on
                msg = "중복된 닉네임입니다.";
                tv_nick.setTextColor(getColor(R.color.warn));
                isOk = false;
            } else {
                msg = "사용가능한 닉네임입니다.";
                tv_nick.setTextColor(getColor(R.color.ok));
                isOk = true;
            }
            tv_nick.setText(msg);
            tv_nick.setVisibility(View.VISIBLE);

            submit();
        });

        // 이메일 중복검사
        et_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText et = (EditText) v;
                String email = et.getText().toString();

                // 입력창 포커스가 풀렸는데 값이 입력 되었다면 검사
                if (!hasFocus && email.length() != 0) {
                    boolean isDuplicate = DataManager.getInstance().containsUser(email);
                    String msg;
                    if (isDuplicate) {    // 경고글 on
                        msg = "중복된 이메일 입니다.";
                        tv_email.setTextColor(getColor(R.color.warn));
                        isOk = false;
                    } else {
                        msg = "사용가능한 이메일 입니다.";
                        tv_email.setTextColor(getColor(R.color.ok));
                        isOk = true;
                    }
                    tv_email.setText(msg);
                    tv_email.setVisibility(View.VISIBLE);
                } else {
                    tv_email.setVisibility(View.INVISIBLE);
                }
            }
        });

        // 비밀번호 재확인
        et_rePw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText et = (EditText) v;
                String pw = et_pw.getText().toString();
                String rePw = et.getText().toString();

                if (!hasFocus && rePw.length() != 0) {
                    String msg;

                    // pw와 일치하는지 검사
                    if (pw.equals(rePw)) {
                        msg = "";
                        tv_rePw.setTextColor(getColor(R.color.ok));
                        isOk = false;
                    } else {
                        msg = "비밀번호가 일치하지 않습니다.";
                        tv_rePw.setTextColor(getColor(R.color.warn));
                        isOk = true;
                    }
                    tv_rePw.setText(msg);
                    tv_rePw.setVisibility(View.VISIBLE);
                }else {
                    tv_rePw.setVisibility(View.INVISIBLE);
                }
            }
        });

        // 닉네임 중복검사
        et_nick.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText et = (EditText) v;
                String nickName = et.getText().toString();

                // 입력창 포커스가 풀렸는데 값이 입력 되었다면 검사
                if (!hasFocus && nickName.length() != 0) {
                    boolean isDuplicate = DataManager.getInstance().containNickName(nickName);
                    String msg;
                    if (isDuplicate) {    // 경고글 on
                        msg = "중복된 닉네임입니다.";
                        tv_nick.setTextColor(getColor(R.color.warn));
                        isOk = false;
                    } else {
                        msg = "사용가능한 닉네임입니다.";
                        tv_nick.setTextColor(getColor(R.color.ok));
                        isOk = true;
                    }
                    tv_nick.setText(msg);
                    tv_nick.setVisibility(View.VISIBLE);
                }else {
                    tv_nick.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void submit() {
        // TODO 계정 중복검사는 et에 값 입력하는 시점에 할 것
        String email = et_email.getText().toString();
        String pw = et_pw.getText().toString();
        String rePw = et_rePw.getText().toString();
        String nick = et_nick.getText().toString();

        if (!isValid(email, pw, rePw, nick)) {
            toast.setText("입력값이 유효하지 않습니다.");
            toast.show();
            return;
        }

        User newUser = new User(email, pw, nick);
        int result = DataManager.getInstance().createUser(newUser);

        if (result == 0) {
            Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "회원가입 실패", Toast.LENGTH_LONG).show();
        }

    }

    // 입력값 유효성 검사
    private boolean isValid(String email, String pw, String rePw, String nick) {
        if (email.length() == 0
                || pw.length() == 0
                || rePw.length() == 0
                || nick.length() == 0
                || !isOk
        ) {
            return false;
        }
        return true;
    }

}