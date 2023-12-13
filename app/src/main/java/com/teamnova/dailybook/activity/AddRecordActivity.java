package com.teamnova.dailybook.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.teamnova.dailybook.OnDataPassedListener;
import com.teamnova.dailybook.R;
import com.teamnova.dailybook.data.DataManager;
import com.teamnova.dailybook.dto.Book;
import com.teamnova.dailybook.dto.ReadRecord;
import com.teamnova.dailybook.fragment.MyBooksFragment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 독서기록 등록 액티비티
 */
public class AddRecordActivity extends AppCompatActivity implements OnDataPassedListener {

    Button btn_save;
    Button btn_new;
    Button btn_mybook;
    ImageView iv;
    Uri imgUri;
    DataManager dm;
    FragmentContainerView fcv;
    String bookPK;
    LinearLayout ll_recored_book;
    View selectedBook;
    Button btn_reset;
    TextView tv_memo;

    String from;
    Book newBook; // 책 정보 추가하기를 해서 받은 book객체 셰어드에 추가할 예정인 데이터
    long elapsedTime;
    LocalDateTime startTime;
    LocalDateTime endTime;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        dm = DataManager.getInstance();

        TextView recordDay = findViewById(R.id.textview_add_record_day);
        TextView recordTime = findViewById(R.id.textview_add_record_time);
        TextView tv_elapsed = findViewById(R.id.textview_add_record_elapsed);
        fcv = findViewById(R.id.fragmentContainerView_record_mybooks);
        btn_save = findViewById(R.id.button_record_save);
        btn_new = findViewById(R.id.button_record_new);
        btn_mybook = findViewById(R.id.button_record_mybook);
        ll_recored_book = findViewById(R.id.ll_record_book);
        btn_reset = findViewById(R.id.button_record_reset);
        tv_memo = findViewById(R.id.textview_add_record_memo);

        Intent intent = getIntent();
        elapsedTime = intent.getLongExtra("elapsedTime", -1);
        startTime = LocalDateTime.parse(intent.getStringExtra("startDT"));
        endTime = LocalDateTime.parse(intent.getStringExtra("endDT"));

        LocalDate date = startTime.toLocalDate();
        LocalTime sTime = startTime.toLocalTime();
        LocalTime eTime = endTime.toLocalTime();

        int seconds = (int) (elapsedTime / 1000) % 60;
        int minutes = (int) ((elapsedTime / (1000 * 60)) % 60);
        int hours = (int) ((elapsedTime / (1000 * 60 * 60)) % 24);

        recordDay.setText(date.getYear() + "/" + date.getMonthValue() + "/" + date.getDayOfMonth());
        recordTime.setText(sTime.getHour() + ":" + sTime.getMinute() + "~" + eTime.getHour() + ":" + eTime.getMinute());
        tv_elapsed.setText("총 독서시간 : " + String.format("%02d:%02d:%02d", hours, minutes, seconds));

        btn_save.setOnClickListener(new OnClickListener());
        btn_new.setOnClickListener(new OnClickListener());
        btn_mybook.setOnClickListener(new OnClickListener());
        btn_reset.setOnClickListener(new OnClickListener());
    }

    // 프래그먼트로부터 값을 받아 처리하는 메소드
    @Override
    public void onDataPassed(String data) {
        bookPK = data;
        fcv.setVisibility(View.GONE);

        Book book = dm.getBook(bookPK);

        makeBookView(book);
    }

    class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == btn_save) {
                Dialog mdialog = new AlertDialog.Builder(AddRecordActivity.this)
                        .setTitle("독서 결과 저장")
                        .setMessage("해당 기록을 저장합니까?")
                        .setNegativeButton("아니오", (dialog, which) -> {
                        })
                        .setPositiveButton("예", (dialog, which) -> {
                            // 책정보 추가하기를 했으면 책정보 생성
                            String newBookPK = null;
                            if (newBook != null) {
                                dm.createBook(newBook);
                                newBookPK = newBook.getPK();
                            }

                            String memo = tv_memo.getText().toString();
                            long elapsed = elapsedTime;
                            LocalDateTime start = startTime;
                            LocalDateTime end = endTime;

                            ReadRecord readRecord = new ReadRecord(
                                    newBookPK,
                                    memo,
                                    elapsed,
                                    start,
                                    end
                            );

                            dm.createRecord(readRecord);
                            // 기록객체 생성하고 스톱워치로 복귀
                            dialog.dismiss();
                            finish();
                        })
                        .setCancelable(true)
                        .create();

                mdialog.show();


            } else if (v == btn_new) {
                // AddBookAcitivy를 호출
                Intent intent = new Intent(AddRecordActivity.this, AddBookActivity.class);
                intent.putExtra("from", "AddRecord");
                startActivityForResult(intent, 333);

            } else if (v == btn_mybook) {
                // mybooks로 번들을 쏜다.
                Fragment fragment = new MyBooksFragment();
                Bundle bundle = new Bundle();
                bundle.putString("from", "AddRecord");
                fragment.setArguments(bundle);

                // 컨테이너에 프래그먼트 장착
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainerView_record_mybooks, fragment);
                fragmentTransaction.commit();

                // 컨테이너 출력
                fcv.setVisibility(View.VISIBLE);
            } else if (v == btn_reset) { // 선택한 책 제거
                ll_recored_book.removeAllViews();
                selectedBook = null;
                newBook = null;
            }
        }
    }

    public Dialog getBookAddDialog() {
        // 다이얼로그 세팅
        Dialog dialog = new Dialog(AddRecordActivity.this);
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

        btn_yes.setText("추가하기");

        imgUri = Uri.parse("android.resource://" + AddRecordActivity.this.getPackageName() + "/" + R.drawable.splashimage);
        iv.setImageURI(imgUri); // 초기값
        iv.setOnClickListener(v1 -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, AddRecordActivity.this.hashCode());
        });
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(params);
        // 등록하기 누르면
        btn_yes.setOnClickListener(v2 -> {

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

            // 새로운 책정보 저장은 결과장츼 저장하기 버튼을 누른 시점에서 해야함
            dm.createBook(book);
            Log.d("TAG", "사용자 지정 저장 책 : " + book);
            Toast.makeText(AddRecordActivity.this, "도서정보가 추가되었습니다.", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btn_no.setOnClickListener(v2 -> dialog.dismiss());

        return dialog;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 333 && data != null) {
            String lFrom = data.getStringExtra("from");

            Book book;
            String source;
            if (lFrom != null && lFrom.equals("BookSearch")) {
                source = "kakako";
            } else {
                source = "custom";
            }
            book = new Book(dm.getCurrentId(), source);
            String title = data.getStringExtra("title");
            String authors = data.getStringExtra("authors");
            String trans = data.getStringExtra("translators");
            String pub = data.getStringExtra("publisher");
            String date = data.getStringExtra("dateTime");
            String cont = data.getStringExtra("contents");
            String th = data.getStringExtra("thumbnail");

            book.title = title;
            book.authors = new String[]{authors};
            book.translators = new String[]{trans};
            book.publisher = pub;
            book.dateTime = date == null ? null : LocalDateTime.parse(date);
            book.contents = cont;
            book.thumbnail = th;

            makeBookView(book);
            newBook = book;
        }
    }

    public void makeBookView(Book book) {

        if (selectedBook == null) {
            selectedBook = LayoutInflater.from(AddRecordActivity.this)
                    .inflate(R.layout.recyclerview_book_item, ll_recored_book, true);
        }

        ImageView iv = selectedBook.findViewById(R.id.imageview_bookitem_bookimage);
        TextView tv_title = selectedBook.findViewById(R.id.textview_bookitem_title);
        TextView tv_author = selectedBook.findViewById(R.id.textview_bookitem_author);
        TextView tv_publisher = selectedBook.findViewById(R.id.textview_bookitem_publisher);

        Glide.with(AddRecordActivity.this).load(book.thumbnail).into(iv);
        tv_title.setText(book.title);
        String author = book.getAuthors();
        tv_author.setText(author);
        tv_publisher.setText(book.publisher);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        from = intent.getStringExtra("from");

        if (from != null && from.equals("BookSearch")) {
            onActivityResult(333, RESULT_OK, intent);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed(); // 기본 행동을 막아야함

        Dialog mdialog = new AlertDialog.Builder(AddRecordActivity.this)
                .setTitle("저장되지 않은 데이터")
                .setMessage("지금 나가면 독서기록이 사라집니다. 그래도 나갑니까?")
                .setNegativeButton("아니오", (dialog, which) -> {
                })
                .setPositiveButton("예", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .setCancelable(true)
                .create();

        mdialog.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}