package com.teamnova.dailybook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.teamnova.dailybook.R;
import com.teamnova.dailybook.activity.AddRecordActivity;
import com.teamnova.dailybook.data.DataManager;
import com.teamnova.dailybook.dto.Book;
import com.teamnova.dailybook.dto.ReadRecord;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * 기록조회 화면
 */
public class RecordFragment extends Fragment {

    CalendarView calendarView;
    LinearLayout ll_record_list;

    DataManager dm;

    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dm = DataManager.getInstance();

        calendarView = view.findViewById(R.id.calendarView_record_calendar);
        ll_record_list = view.findViewById(R.id.ll_record_record_list);

        displayRecord(calendarView.getDate());

        // 날짜 선택
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                long millis = calendar.getTimeInMillis();

                Log.d("TAG", "onSelectedDayChange: ");
                displayRecord(millis);
            }
        });
    }

    // 주어진 날짜에(밀리초 단위) 맞는 기록 목록을 하단에 출력
    private void displayRecord(long dateMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateMillis);

        int year = calendar.get(java.util.Calendar.YEAR);
        int month = calendar.get(java.util.Calendar.MONTH) + 1; // 월은 0부터 시작하므로 1을 더해줌
        int dayOfMonth = calendar.get(java.util.Calendar.DAY_OF_MONTH);

        String selectedDate = year + "/" + month + "/" + dayOfMonth;
        Log.d("TAG", "displayRecord: " + selectedDate);

        ll_record_list.removeAllViews(); // 초기화

        // 선택된 날짜와 일치하는 기록(시작날짜 기준) 가져오기
        // record를 일괄적으로 로딩을 해야함
        ArrayList<ReadRecord> records = dm.getAllRecord();
        for (int i = 0; i < records.size(); i++) {
            ReadRecord record = records.get(i);

            if (record.getRecordDay().equals(selectedDate)) { // 날짜가 일치하면

                // 뷰 생성하고 리니어 레이아웃에 add
                View view = LayoutInflater.from(RecordFragment.this.getContext())
                        .inflate(R.layout.linearlayout_record_item, ll_record_list, true);

                ImageView iv = view.findViewById(R.id.imageview_record_item_bookimage);
                TextView tv_title = view.findViewById(R.id.textview_record_item_title);
                TextView tv_elapsed = view.findViewById(R.id.textview_record_item_elapsed);
                TextView tv_StoE = view.findViewById(R.id.textview_record_item_start_end);


                // 데이터 채워넣고
                if (record.bookPk != null && dm.getBook(record.bookPk) != null) {
                    Book book = dm.getBook(record.bookPk);
                    Glide.with(view).load(book.thumbnail).into(iv);
                    tv_title.setText(book.title);
                } else { // 책정보 없으면 기본값
                    Glide.with(view).load(R.drawable.splashimage).into(iv);
                    tv_title.setText(record.getRecordDay() + " 의 기록");
                }
                String s = tv_title.getText().toString();

                tv_StoE.setText(record.getRecordTime());
                tv_elapsed.setText("독서시간 : " + record.getTotalElapsed());

                // 리스너 장착
                view.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), AddRecordActivity.class);
                    intent.putExtra("from", "RecordFragment");
                    intent.putExtra("recordPK", record.getPK());
                    startActivity(intent);
                });

            }
        }
    }
}