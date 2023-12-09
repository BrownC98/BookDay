package com.teamnova.bookday.kakao;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;
import com.teamnova.bookday.BuildConfig;

/**
 * 카카오 sdk를 초기화 해주기 위한 클래스
 */
public class KakaoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_API_KEY);
    }
}
