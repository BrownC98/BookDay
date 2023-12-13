package com.teamnova.dailybook.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.teamnova.dailybook.R;
import com.teamnova.dailybook.data.DataManager;
import com.teamnova.dailybook.dto.Essay;

/**
 * 독후감 CRUD 액티비티
 */
public class EssayActivity extends AppCompatActivity {

    ImageButton btn_delete;
    Button btn_save;
    EditText et_title;
    EditText et_content;
    Dialog dialog;
    DataManager dm;
    Essay essay;
    String bookPK;
    String what;
    String essayPK;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_essay);

        dm = DataManager.getInstance();

        btn_delete = findViewById(R.id.imageButton_essay_delete);
        btn_save = findViewById(R.id.button_essay_save);
        et_title = findViewById(R.id.edittext_essay_title);
        et_content = findViewById(R.id.editText_essay_content);

        Intent intent = getIntent();
        what = intent.getStringExtra("WHAT");
        bookPK = intent.getStringExtra("BOOK_PK");

        if (what != null && what.equals("CREATE")) {
            btn_delete.setVisibility(View.INVISIBLE);
        } else if (what != null && what.equals("READ")) {
            essayPK = intent.getStringExtra("ESSAY_PK");
            essay = dm.getEssay(essayPK);
            btn_delete.setVisibility(View.VISIBLE);

            et_title.setText(essay.title);
            et_content.setText(essay.content);
        }

        btn_save.setOnClickListener(new OnClickListener());
        btn_delete.setOnClickListener(new OnClickListener());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) dialog.dismiss();
    }

    class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == btn_save) {
                // 현재내용을 저장합니까?
                dialog = new AlertDialog.Builder(EssayActivity.this)
                        .setTitle("데이터 저장")
                        .setMessage("현재 내용을 저장합니까?")
                        .setNegativeButton("아니오", (dialog, which) -> {
                        })
                        .setPositiveButton("예", (dialog, which) -> {
                            if (what != null && what.equals("CREATE")){ // 생성
                                essay = new Essay(dm.getCurrentId(), bookPK);
                                essay.title = et_title.getText().toString();
                                essay.content = et_content.getText().toString();
                                dm.createEssay(essay);
                            } else if(what != null && what.equals("READ")){ // 수정
                                essay.title = et_title.getText().toString();
                                essay.content = et_content.getText().toString();
                                dm.putEssay(essay);
                            }
                            Toast.makeText(EssayActivity.this, "현재내용이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .setCancelable(true)
                        .create();

                dialog.show();
            } else if (v == btn_delete) {
                // 독후감이 삭제됩니다.
                dialog = new AlertDialog.Builder(EssayActivity.this)
                        .setTitle("데이터 삭제")
                        .setMessage("현재 독후감을 삭제합니까?")
                        .setNegativeButton("아니오", (dialog, which) -> {
                        })
                        .setPositiveButton("예", (dialog, which) -> {
                            // 데이터 삭제
                            dm.removeEssay(essay.getPK());
                            Toast.makeText(EssayActivity.this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .setCancelable(true)
                        .create();

                dialog.show();
            }

        }
    }
}