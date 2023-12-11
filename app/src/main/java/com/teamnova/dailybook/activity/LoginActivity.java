package com.teamnova.dailybook.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.teamnova.dailybook.R;
import com.teamnova.dailybook.data.DataManager;
import com.teamnova.dailybook.dto.User;

import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "TAG";

    ImageView iv_ad;
    EditText et_email;
    EditText et_pw;
    LinearLayout ll_rememberMe;
    CheckBox cb_rememberMe;
    TextView tv_forgotPw;
    Button btn_default_logIn;
    ImageView iv_kakao_Login;
    LinearLayout ll_signUp;
    Function2<OAuthToken, Throwable, Unit> kakaoCallback;

    Toast toast;

    int adIdx; // 현재 출력중인 광고 번호
    int DELAY_MS = 3000; // 광고 변경 딜레이 (millsec)
    ArrayList<Integer> adList; // 광고 리스트
    boolean adStop;
    Handler loginHandler;
    DataManager dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setContentView 하기전에 스플래시를 호출해야함(순서 중요)
        SplashScreen.installSplashScreen(this);
        setContentView(R.layout.activity_login);

        // DataManger 초기화
        DataManager.init(this);
        dm = DataManager.getInstance();

        loginHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                adIdx = msg.what;
            }
        };
        // 카카오 api 사용을 위한 디버그 키 해시 값 확인
//        Log.d("TAG", "onCreate: " + Utility.INSTANCE.getKeyHash(this));

        et_email = findViewById(R.id.editText_login_email);
        et_pw = findViewById(R.id.editText_login_password);
        ll_rememberMe = findViewById(R.id.linearLayout_login_rememberMe);
        tv_forgotPw = findViewById(R.id.textView_login_forgotPassword);
        btn_default_logIn = findViewById(R.id.button_login_defaultLogin);
        iv_kakao_Login = findViewById(R.id.imageView_login_kakaoLogin);
        ll_signUp = findViewById(R.id.linearLayout_login_signUp);
        iv_ad = findViewById(R.id.iv_login_ad);
        cb_rememberMe = findViewById(R.id.checkBox_login_rememberMe);

        ll_rememberMe.setOnClickListener(this);
        tv_forgotPw.setOnClickListener(this);
        btn_default_logIn.setOnClickListener(this);
        iv_kakao_Login.setOnClickListener(this);
        ll_signUp.setOnClickListener(this);
        iv_ad.setOnClickListener(this);

        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        // rememberMe 처리
        if (dm.isRememberMe() && dm.getCurrentId() != null) {
            String curPw = dm.getCurrentUser().password;
            cb_rememberMe.setChecked(true);
            logIn(dm.getCurrentId(), curPw);
        }

        // 로고 세팅
        adList = new ArrayList<>();
        adList.add(R.drawable.bookstore_aladin);
        adList.add(R.drawable.bookstore_kyobo);
        adList.add(R.drawable.bookstore_yes24);

        // 카카오 로그인 처리 콜백
        kakaoCallback = new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                Log.d(TAG, "카카오 콜백");
                if (oAuthToken != null) {
                    Log.d(TAG, "카카오 로그인 성공");
                    processingKakaoLoginResult();
                } else {
                    Log.d(TAG, "카카오 로그인 실패");
                    toast.setText("카카오 로그인 실패");
                    toast.show();
                }
                return null;
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TAG", "onResume: 광고시작");
        adStop = false;
        getAdThreadInstance().start();
    }

    // cpu 자원을 아끼기 위해 onPause에서 광고 출력을 종료한다.
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("TAG", "onPause: 광고정지");
        adStop = true;
    }

    @Override
    public void onClick(View v) {
        if (v == ll_rememberMe) {
            cb_rememberMe.toggle();
        } else if (v == tv_forgotPw) {
            // TODO : 비번찾기 구현
        } else if (v == btn_default_logIn) {
            logIn(et_email.getText().toString(), et_pw.getText().toString());
        } else if (v == iv_kakao_Login) {
            kakaoLogIn();
        } else if (v == ll_signUp) {
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(intent);
        } else if (v == iv_ad) {
            moveToLink();
        }
    }

    private void logIn(String email, String pw) {
        // 회원여부 검증
        User account = dm.getUser(email);

        if (account == null || !account.password.equals(pw)) {
            toast.setText("회원정보가 일치하지 않습니다.");
            toast.show();
            return;
        }

        // 회원이면 로그인 처리
        dm.setCurrentUser(account, cb_rememberMe.isChecked());

        toast.setText("로그인 되었습니다.");
        toast.show();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void moveToLink() {
        String url = "";
        // 현재 보여주는 배너 이미지에 따라 이동할 URL 지정
        switch (adIdx) {
            case 0:
                url = "https://www.aladin.co.kr";
                break;
            case 1:
                url = "https://www.kyobobook.co.kr";
                break;
            case 2:
                url = "https://www.yes24.com";
                break;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    /**
     * 카카오 로그인 진행
     */
    private void kakaoLogIn() {
        // 카카오톡 설치여부 확인
        if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(getApplicationContext())) {
            Log.d(TAG, "카카오톡 설치됨");
            // 카카오톡 로그인 수행, 콜백에는 로그인 결과에 따라 필요한 처리 구현
            UserApiClient.getInstance().loginWithKakaoTalk(getApplicationContext(), kakaoCallback);
        } else { // 카카오톡 미설치
            Log.d(TAG, "카카오톡 미설치");
            UserApiClient.getInstance().loginWithKakaoAccount(getApplicationContext(), kakaoCallback);
        }
    }

    /**
     * 카카오 로그인 결과처리 메소드
     */
    private void processingKakaoLoginResult() {
        // 로그인한 사용자 정보 가져오기
        UserApiClient.getInstance().me(new Function2<com.kakao.sdk.user.model.User, Throwable, Unit>() {
            @Override
            public Unit invoke(com.kakao.sdk.user.model.User user, Throwable throwable) {
                // email은 앱에서 id 로 쓰임
                String email = user.getKakaoAccount().getEmail();
                String nickname = user.getKakaoAccount().getProfile().getNickname();
                // 이미지 외부 링크를 이미지 뷰로 띄우는 법을 알아야함
                // 이미지를 백그라운드 스레드에서 다운받아 ui쓰레드에서 이미지뷰에 출력을 해줘야 한다.
                // 직접 구현하는 방법도 있지만, picasso, gilde 같은 이미지 라이브러리를 통해 간단히 구현할 수 있다.
                // 직접 구현하는 방법은 https://ititit1.tistory.com/68 를 참고할 것''''''''''''''
                Uri imgUrl = Uri.parse(user.getKakaoAccount().getProfile().getThumbnailImageUrl());

                // 회원여부 검증
                User account = dm.getUser(email);
                String pw;
                if (account == null) {
                    // 비회원이면 회원가입 처리
                    // 카카오 로그인시 비밀번호는 kakao_ + 랜덤한 6자리 숫자로 결정됨
                    pw = "kakao_" + (int) (Math.random() * 1000000);
                    dm.createUser(new User(email, pw, nickname, imgUrl));
                    toast.setText("회원가입 완료");
                    toast.show();
                } else {
                    pw = account.password;
                }
                logIn(email, pw);
                return null;
            }
        });
    }

    /**
     * 코드 정리를 위한 메소드
     *
     * @return
     */
    private Thread getAdThreadInstance() {
        // 백그라운드 쓰레드에서 광고 실행
        return new Thread(() -> {

            // 백그라운드 쓰레드는 post 하고 종료된다.
            // 이후에는 메인쓰레드에서 재귀적으로 메시지큐에 runnable이 push된다.
            loginHandler.post(new Runnable() {
                int idx = 0;

                @Override
                public void run() {
                    if (adStop) return;

//                    Log.d("TAG", "logoIdx: " + idx);
                    iv_ad.setImageResource(adList.get(idx));
                    loginHandler.sendEmptyMessage(idx); // 현재 어떤 광고를 출력하는지 메시지를 보내 링크를 설정할 때 참고함
                    idx++;
                    idx %= adList.size();

                    // 자기자신(postDelayed의 인자로 주어진 Runnable)을 인자로 넘겨준다
                    loginHandler.postDelayed(this, DELAY_MS);
                }
            });
        });
    }
}