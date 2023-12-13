package com.teamnova.dailybook.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.teamnova.dailybook.R;
import com.teamnova.dailybook.adapter.EssayAdapter;
import com.teamnova.dailybook.data.DataManager;
import com.teamnova.dailybook.dto.Book;
import com.teamnova.dailybook.dto.Essay;

import java.util.ArrayList;

/**
 * 도서 상세페이지
 * 책 정보 수정, 에세이 추가 및 조회, 책 정보 삭제가 여기서 이루어진다.
 */
public class BookDetailActivity extends AppCompatActivity {

    private final int REQUEST_CODE = hashCode();
    ImageView iv;
    EditText et_title;
    EditText et_author;
    EditText et_publisher;
    RecyclerView recyclerView;
    boolean dataChanged = false;
    DataManager dm;
    Uri imgUri;
    Dialog dialog;
    ImageButton button_delete;
    FloatingActionButton fab;
    EssayAdapter adapter;
    String bookPK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        iv = findViewById(R.id.imageview_detail_img);
        et_title = findViewById(R.id.bookdetail_title_TextView);
        et_author = findViewById(R.id.bookdetail_author_TextView);
        et_publisher = findViewById(R.id.bookdetail_publisher_TextView);
        button_delete = findViewById(R.id.imageButton_detail_delete);
        fab = findViewById(R.id.floatingActionButton_detail_add_essay);
        recyclerView = findViewById(R.id.recyclerview_bookdetail_memolist);

        dm = DataManager.getInstance();

        // bookPK 전달
        Intent intent = getIntent();
        bookPK = intent.getStringExtra("BOOK_PK");
        Book book = dm.getBook(bookPK);

        iv.setOnClickListener(new OnClickListner());
        button_delete.setOnClickListener(new OnClickListner());
        fab.setOnClickListener(new OnClickListner());

        Glide.with(this).load(book.thumbnail).into(iv);
        imgUri = Uri.parse(book.thumbnail);
        et_title.setText(book.title);
        et_author.setText(book.getAuthors());
        et_publisher.setText(book.publisher);

        et_title.addTextChangedListener(new TextWatcher());
        et_author.addTextChangedListener(new TextWatcher());
        et_publisher.addTextChangedListener(new TextWatcher());

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        ArrayList<Essay> essays = dm.getEssayList(book);

        // 리사이클러 뷰 세팅
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new EssayAdapter(getApplicationContext(), essays);
        adapter.setOnItemClickListener(((v, pos) -> {
            Intent _intent = new Intent(getApplicationContext(), EssayActivity.class);

            // 책, 독후감 PK 담아서 전송
            _intent.putExtra("BOOK_PK", bookPK);
            _intent.putExtra("WHAT", "READ");
            String essayPK = adapter.mData.get(pos).getPK();
            _intent.putExtra("ESSAY_PK", essayPK);
            Log.d("TAG", " from 독후감 리사이클러뷰 key : BOOK_PK -> " + bookPK);
            startActivity(_intent);
        }));
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 업데이트된 book을 가져와야함
        Book book = dm.getBook(bookPK);
        adapter.setmData(dm.getEssayList(book));
        Log.d("TAG", "BookDetailActivity.java onResume: ");
    }

    @Override
    public void onBackPressed() {
        Log.d("BACK", "onBackPressed: 뒤로가기");
        if (dataChanged) {
            dialog = new AlertDialog.Builder(BookDetailActivity.this)
                    .setTitle("데이터 수정")
                    .setMessage("수정된 데이터가 있습니다. 저장합니까?")
                    .setNegativeButton("아니오", (dialog, which) -> {
                        finish();
                    })
                    .setPositiveButton("예", (dialog, which) -> {
                        Book book = dm.getBook(bookPK);
                        // 데이터 저장처리
                        book.title = et_title.getText().toString();
                        book.authors = new String[]{et_author.getText().toString()};
                        book.publisher = et_publisher.getText().toString();
                        book.thumbnail = imgUri.toString();
                        dm.putBook(book);

                        Toast.makeText(BookDetailActivity.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setCancelable(true)
                    .create();

            dialog.show();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) dialog.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Glide.with(this).load(uri).into(iv);
            imgUri = uri;
            dataChanged = true;
        }
    }

    class OnClickListner implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == iv) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE);
            } else if (v == button_delete) {
                dialog = new AlertDialog.Builder(BookDetailActivity.this)
                        .setTitle("삭제하기")
                        .setMessage("현재 도서의 정보를 삭제합니까?")
                        .setNegativeButton("아니오", (dialog, which) -> {
                        })
                        .setPositiveButton("예", (dialog, which) -> {
                            // 데이터 삭제
                            dm.removeBook(bookPK);
                            Toast.makeText(BookDetailActivity.this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .setCancelable(true)
                        .create();

                dialog.show();
            } else if (v == fab) {
                Intent intent = new Intent(BookDetailActivity.this, EssayActivity.class);
                intent.putExtra("WHAT", "CREATE");
                intent.putExtra("BOOK_PK", bookPK);
                Log.d("TAG", "bookdetailactivity, fab : 에세이 생성 화면으로 이동, WAHT - CREATE ");
                startActivity(intent);
            }
        }
    }

    class TextWatcher implements android.text.TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            dataChanged = true;
        }
    }


}