package com.teamnova.dailybook.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.teamnova.dailybook.R;

/**
 * 도서 상세페이지
 */
public class BookDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
    }
}