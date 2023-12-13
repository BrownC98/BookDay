package com.teamnova.dailybook.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.teamnova.dailybook.R;
import com.teamnova.dailybook.data.DataManager;
import com.teamnova.dailybook.dto.Book;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 도서 정보 추가 액티비티
 */
public class AddBookActivity extends AppCompatActivity {

    private final int REQUEST_CODE = this.hashCode();
    Button btn_custom;
    Button btn_search;
    Dialog dialog;
    DataManager dm;
    Uri imgUri;
    ImageView iv;
    String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        dm = DataManager.getInstance();

        from = getIntent().getStringExtra("from");

        btn_custom = findViewById(R.id.button_addbook_custom);
        btn_search = findViewById(R.id.button_addbook_search);

        btn_custom.setOnClickListener(new OnClickListener());
        btn_search.setOnClickListener(new OnClickListener());

        // 다이얼로그 세팅
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.dialog_select_custom_add_book);
        iv = dialog.findViewById(R.id.imageView_select_dialog_img_c);
        EditText et_title = dialog.findViewById(R.id.edittext_select_dialog_title_c);
        EditText et_author = dialog.findViewById(R.id.edittext_select_dialog_authors_c);
        EditText et_translator = dialog.findViewById(R.id.edittext_select_dialog_translator_c);
        EditText et_publisher = dialog.findViewById(R.id.edittext_select_dialog_publisher_c);
        EditText et_pubdate = dialog.findViewById(R.id.edittext_select_dialog_pubdate_c);
        EditText et_content = dialog.findViewById(R.id.edittext_select_dialog_contents_c);
        Button btn_yes = dialog.findViewById(R.id.button_select_dialog_yes_c);
        Button btn_no = dialog.findViewById(R.id.button_select_dialog_no_c);
        imgUri = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.drawable.splashimage);
        iv.setImageURI(imgUri); // 초기값
        iv.setOnClickListener(new OnClickListener());
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(params);

        // 등록하기 누르면
        btn_yes.setOnClickListener(v -> {

            // 책 정보를 인텐트에 담아서 전송 (book을  json으로 바꿔서)
            Book book = new Book(dm.getCurrentId(), "custom");
            book.title = et_title.getText().toString();
            book.authors = new String[]{et_author.getText().toString()};
            book.translators = new String[]{et_translator.getText().toString()};
            book.publisher = et_publisher.getText().toString();

            String dateString = et_pubdate.getText().toString();
            LocalDateTime localDateTime;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                localDateTime = LocalDateTime.parse(dateString, formatter);
            } catch (Exception e) {
                localDateTime = null;
            }
            book.dateTime = localDateTime;
            book.contents = et_content.getText().toString();
            book.thumbnail = imgUri.toString();

            // 독서결과를 등록하기 위해 여기로 왔다면
            if (from != null && from.equals("AddRecord")) {
                Intent result = new Intent();
                result.putExtra("title", book.title);
                result.putExtra("authors", book.authors);
                result.putExtra("translators", book.translators);
                result.putExtra("publisher", book.publisher);
                String strDate = book.dateTime == null ? null : book.dateTime.toString();
                result.putExtra("dateTime", strDate);
                result.putExtra("contents", book.contents);
                result.putExtra("thumbnail", book.thumbnail);
                setResult(Activity.RESULT_OK, result);
                dialog.dismiss();
                finish();
            } else {
                dm.createBook(book);
                Log.d("TAG", "사용자 지정 저장 책 : " + book);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 목적지에서 onCreate 호출 됨

                startActivity(intent);
                Toast.makeText(this, "도서정보가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btn_no.setOnClickListener(v -> dialog.dismiss());

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent receiveIntent = getIntent();
        from = receiveIntent.getStringExtra("from");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 프로필 사진 지정
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            iv.setImageURI(uri);
            imgUri = uri;
        }else if(requestCode == 555 && resultCode == RESULT_OK && data != null){ // 기록등록
           String lFrom = data.getStringExtra("from");
            String title = data.getStringExtra("title");
            String authors = data.getStringExtra("authors");
            String trans = data.getStringExtra("translators");
            String pub = data.getStringExtra("publisher");
            String date = data.getStringExtra("dateTime");
            String cont = data.getStringExtra("contents");
            String th = data.getStringExtra("thumbnail");

            Intent result = new Intent();
            result.putExtra("from", lFrom);
            result.putExtra("title", title);
            result.putExtra("authors", authors);
            result.putExtra("translators", trans);
            result.putExtra("publisher", pub);
            //String strDate = book.dateTime == null ? null : book.dateTime.toString();
            result.putExtra("dateTime", date);
            result.putExtra("contents", cont);
            result.putExtra("thumbnail", th);
            setResult(Activity.RESULT_OK, result);
            dialog.dismiss();
            finish();
        }
    }

    class OnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v == btn_custom) { // 다이얼로그 켜짐
                dialog.show();
            } else if (v == btn_search) {
                Intent intent = new Intent(AddBookActivity.this, BookSerachActivity.class);
                intent.putExtra("from", from);
                startActivityForResult(intent, 555);
            } else if (v == iv) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE);
            }
        }
    }

}