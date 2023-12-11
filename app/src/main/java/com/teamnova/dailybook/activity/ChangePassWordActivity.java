package com.teamnova.dailybook.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.teamnova.dailybook.R;

/**
 * 비번 재발급 액티비티
 * 이메일 인증 후 비번 재발급
 */
public class ChangePassWordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass_word);
    }
}