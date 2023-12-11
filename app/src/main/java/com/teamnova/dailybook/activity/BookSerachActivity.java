package com.teamnova.dailybook.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.teamnova.dailybook.R;


/**
 * 책 제목 입력 후 해당 제목으로 등록할지 검색할지 결정하는 액티비티
 * 책 검색 및 검색결과 조회
 */
public class BookSerachActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_serach);
    }
}