package com.teamnova.dailybook.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    ReadRecord record;

    Book loadedBook;
    ImageButton btn_delete;
    String recordPK;
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
        btn_delete = findViewById(R.id.imageButton_record_delete);
        btn_delete.setVisibility(View.GONE);

        btn_delete.setOnClickListener(new OnClickListener());

        Intent intent = getIntent();
        recordPK = intent.getStringExtra("recordPK");
        if (recordPK != null) {   // 기록조회에서 온 것
            btn_delete.setVisibility(View.VISIBLE);
            record = dm.getRecord(recordPK);
            recordDay.setText(record.getRecordDay());
            recordTime.setText(record.getRecordTime());
            tv_elapsed.setText("총 독서시간 : " + record.getTotalElapsed());
            tv_memo.setText(record.memo);
            Book loadedBook = dm.getBook(record.bookPk);
            if (loadedBook != null) makeBookView(loadedBook);
        } else {
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
        }

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
                            String bookPk = null;

                            // 책정보를 추가했다면
                            if (loadedBook != null) {
                                // 그게 새 책이라면
                                if (newBook != null) {
                                    dm.createBook(newBook); // 책데이터 셰어드에 생성
                                }
                                bookPk = loadedBook.getPK();
                            }

                            // 기록객체 생성
                            if (record != null) {   // 수정상황이라면
                                record.getPK();
                                record.bookPk = bookPk;
                                record.memo = tv_memo.getText().toString();
                                dm.putRecord(record);
                            } else {
                                String memo = tv_memo.getText().toString();
                                long elapsed = elapsedTime;
                                LocalDateTime start = startTime;
                                LocalDateTime end = endTime;

                                ReadRecord readRecord = new ReadRecord(
                                        bookPk,
                                        memo,
                                        elapsed,
                                        start,
                                        end
                                );

//                            String d = readRecord.getRecordDay();
//                            String t = readRecord.getRecordTime();
//                            String e = readRecord.getTotalElapsed();

                                // 기록객체 생성하고
                                dm.createRecord(readRecord);
                            }
                            //스톱워치로 복귀
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
            } else if (v == btn_delete) {
                Dialog mdialog = new AlertDialog.Builder(AddRecordActivity.this)
                        .setTitle("기록 삭제")
                        .setMessage("이 기록을 삭제하시겠습니까?")
                        .setNegativeButton("아니오", (dialog, which) -> {
                        })
                        .setPositiveButton("예", (dialog, which) -> {
                            dm.removeRecord(recordPK);
                            dialog.dismiss();
                            finish();
                        })
                        .setCancelable(true)
                        .create();

                mdialog.show();
            }
        }
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
        loadedBook = book;
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