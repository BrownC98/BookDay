package com.teamnova.dailybook.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.teamnova.dailybook.R;
import com.teamnova.dailybook.activity.AddRecordActivity;

import java.time.LocalDateTime;

/**
 * 독서기록 생성 화면
 */
public class ReadFragment extends Fragment {

    // 현재 버튼의 역할
    enum BUTTON_STATE {
        START, PAUSE, STOP
    }

    BUTTON_STATE currentState;

    Button btn_control;
    Button btn_stop;
    Chronometer chronometer;
    long pauseTime = 0;
    long startTime = 0; // 시작시점
    long stopTime = 0; // 멈춘시점
    long elapsedTime = 0; // 측정된 시간간격의 총합(독서시간)
    LocalDateTime startDT; // 시작시간
    LocalDateTime endDT; // 종료시간

    public ReadFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_read, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_control = view.findViewById(R.id.button_read_control);
        btn_stop = view.findViewById(R.id.button_read_stop);

        chronometer = view.findViewById(R.id.chronometer_read_stopwatch);

        btn_control.setOnClickListener(new OnClickListener());
        btn_stop.setOnClickListener(new OnClickListener());
    }

    class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == btn_control) {
                if (btn_control.getText().equals("독서 시작")) {  // 독서시작버튼을 누르면
                    if (pauseTime == 0) {
                        elapsedTime = 0;
                        startDT = LocalDateTime.now();
                    }

                    btn_stop.setVisibility(View.GONE);

                    // 마지막으로 멈춘시간을 베이스로
                    chronometer.setBase(SystemClock.elapsedRealtime() + pauseTime);
                    chronometer.start();

                    startTime = SystemClock.elapsedRealtime(); // 측정시작

                    // 독서중단버튼으로 바뀜
                    btn_control.setText("독서 중단");
                    btn_control.setBackgroundResource(R.color.warn);
                } else if (btn_control.getText().equals("독서 중단")) { // 독서중단 버튼 클릭하면
                    // 카운터 멈춤, 흐른 시간간격
                    pauseTime = chronometer.getBase() - SystemClock.elapsedRealtime();
                    chronometer.stop();

                    stopTime = SystemClock.elapsedRealtime();
                    elapsedTime += stopTime - startTime;

                    // 독서시작 버튼으로 바뀜
                    btn_control.setText("독서 시작");
                    btn_control.setBackgroundResource(R.color.ok);

                    // 독서 종료 버튼이 나타남
                    btn_stop.setVisibility(View.VISIBLE);
                }

            } else if (v == btn_stop) {// 독서 종료버튼 클릭시
                new AlertDialog.Builder(getContext())
                        .setTitle("독서 종료")
                        .setMessage("독서를 종료하고 독서결과 화면으로 이동합니다.")
                        .setNegativeButton("아니오", (dialog, which) -> {
                        })
                        .setPositiveButton("예", (dialog, which) -> {
                            endDT = LocalDateTime.now(); // 종료시간 기록
                            // 추가 정보 입력창으로
                            Intent intent = new Intent(getContext(), AddRecordActivity.class);
                            intent.putExtra("elapsedTime", elapsedTime); // 총 독서시간
                            intent.putExtra("startDT", startDT.toString()); // 독서 시작시간
                            intent.putExtra("endDT", endDT.toString()); // 독서 종료시간
                            startActivity(intent);
                        })
                        .setCancelable(true)
                        .create()
                        .show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        chronometer.stop();
    }
}