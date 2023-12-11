package com.teamnova.dailybook.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.teamnova.dailybook.BuildConfig;
import com.teamnova.dailybook.R;
import com.teamnova.dailybook.data.DataManager;
import com.teamnova.dailybook.dto.User;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * 비번 재발급 액티비티
 * 이메일 인증 후 비번 재발급
 */
public class ChangePassWordActivity extends AppCompatActivity {
    String mCode;
    String sendTo;
    Toast toast;
    Handler mHandler;

    final int OK = 0;
    final int FAIL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass_word);

        Button button = findViewById(R.id.button_chage_pw_send);
        EditText et_first = findViewById(R.id.edit_text_change_pw_email);
        EditText et_second = findViewById(R.id.edit_text_change_pw_email2);
        TextView tv_first = findViewById(R.id.textview_change_pw_first);
        TextView tv_second = findViewById(R.id.textview_change_pw_check);
        LinearLayout ll_check = findViewById(R.id.linearlayout_change_pw_check);

        toast = Toast.makeText(this, "", Toast.LENGTH_LONG);


        // 인증코드 받기 -> 인증코드 입력 -> 비밀번호 변경
        button.setOnClickListener(v -> {
            String txt = ((Button) v).getText().toString();

            // 이메일 발송결과 처리
            mHandler = new Handler(Looper.myLooper()) {
                String code;

                @Override
                public void handleMessage(@NonNull android.os.Message msg) {
                    code = (String) msg.obj;
                    mCode = code;
                    if (msg.what == OK) { // 발송성공
                        Log.d("TAG", "전송된 코드 :" + code);
                        showToast("코드 발송 성공");
                        button.setText("인증코드 입력");
                        et_first.setText("");
                        et_first.setInputType(InputType.TYPE_CLASS_TEXT);
                        et_first.setHint("인증코드를 입력해주세요");
                    } else if (msg.what == FAIL) { // 발송실패
                        showToast("코드 발송 실패");
                        return;
                    }
                }
            };

            if (txt.equals("인증코드 받기")) {    // 입력받은 이메일로 코드 발송
                sendTo = et_first.getText().toString();
                if (!DataManager.getInstance().containsUser(sendTo)) {
                    showToast("가입된 이메일이 아닙니다.");
                    return;
                }
                sendEmail(sendTo);

            } else if (txt.equals("인증코드 입력")) { // 입력받은 코드 검증
                if (mCode.equals(et_first.getText().toString())) {
                    // 밑에 et 하나 더 생겨서 비번입력, 확인입력 하고 종료
                    ll_check.setVisibility(View.VISIBLE);
                    tv_first.setVisibility(View.INVISIBLE);
                    et_first.setText("");
                    et_first.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    et_first.requestFocus();
                    et_first.setHint("새로운 비밀번호 입력");
                    button.setText("비밀번호 변경");
                } else {
                    tv_first.setText("인증번호가 일치하지 않습니다.");
                    tv_first.setVisibility(View.VISIBLE);
                    showToast("인증번호가 일치하지 않습니다.");
                }
            } else if (txt.equals("비밀번호 변경")) {   // 입력한 비밀번호 검증 후 변경
                String first = et_first.getText().toString();
                String second = et_second.getText().toString();

                if (!first.equals(second)) {
                    showToast("비밀번호 입력값이 일치하지 않습니다.");
                    return;
                }

                if (first.length() == 0) {
                    showToast("비밀번호를 입력해주세요");
                    return;
                }

                User user = DataManager.getInstance().getUser(sendTo);
                user.password = first;
                DataManager.getInstance().updateUser(user);
                Toast.makeText(this, "비밀번호를 변경했습니다.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void showToast(String msg) {
        toast.setText(msg);
        toast.show();
    }

    // 자바 비동기 처리를 위해 핸들러 를 사용함
    public void sendEmail(String toEmail) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String fromEmail = BuildConfig.GOOGLE_EMAIL;
                String password = BuildConfig.GOOGLE_APP_KEY;
                String code = ((int) (Math.random() * 1000000)) + "";   // 인증코드는 랜덤한 6자리 숫자

                Properties props = new Properties();
                props.setProperty("mail.transport.protocol", "smtp");
                props.setProperty("mail.host", "smtp.gmail.com");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.port", "465");
                props.put("mail.smtp.socketFactory.port", "465");
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.socketFactory.fallback", "false");
                props.put("mail.smtp.ssl.enable", "true");
                props.setProperty("mail.smtp.quitwait", "false");

                Session session = Session.getDefaultInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(fromEmail, password);
                    }
                });

                try {
                    MimeMessage message = new MimeMessage(session);
                    message.setSender(new InternetAddress(fromEmail));
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
                    message.setSubject("데일리북 이메일 인증 코드");    // 제목
                    message.setText("데일리북에서 발송한 이메일 인증 코드입니다. 아래 비밀번호를 인증창에 입력해주세요\n" + "<" + code + ">"); // 내용

                    Transport.send(message);

                    android.os.Message msg = mHandler.obtainMessage(OK, code);
                    mHandler.sendMessage(msg);
                } catch (MessagingException e) {
                    e.printStackTrace();
                    android.os.Message msg = mHandler.obtainMessage(FAIL, code);
                    mHandler.sendMessage(msg);
                }
            }
        });
        thread.start();
    }
}
