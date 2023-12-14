package com.teamnova.dailybook.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 독서기록 정보를 담는 객체
 */
public class ReadRecord {

    private String PK;
    public String bookPk; // null일 수도 있음
    public String memo; // 간단한 메모
    public long elapsedTimeMills; // 총 독서시간
    public LocalDateTime startTime; // 시작시간
    public LocalDateTime endTime; // 종료시간

    public ReadRecord() {
    }

    public ReadRecord(String bookPk, String memo, long elapsedTimeMills, LocalDateTime startTime, LocalDateTime endTime) {
        this.bookPk = bookPk;
        this.memo = memo;
        this.elapsedTimeMills = elapsedTimeMills;
        this.startTime = startTime;
        this.endTime = endTime;
    }


    public String getPK() {
        if (PK == null) {
            PK = bookPk + memo + elapsedTimeMills + startTime + endTime + hashCode();
        }
        return PK;
    }

    // 독서날짜
    public String getRecordDay() {
        LocalDate date = startTime.toLocalDate();
        return date.getYear() + "/" + date.getMonthValue() + "/" + date.getDayOfMonth();
        //return String.format("%02d/%02d/%02d", date.getYear(), date.getMonthValue() ,  date.getDayOfMonth());
    }

    // 독서 시작시간 ~ 독서 종료시간
    public String getRecordTime() {
        LocalTime sTime = startTime.toLocalTime();
        LocalTime eTime = endTime.toLocalTime();
        return sTime.getHour() + ":" + sTime.getMinute() + " - " + eTime.getHour() + ":" + eTime.getMinute();
    }

    public String getTotalElapsed() {
        int seconds = (int) (elapsedTimeMills / 1000) % 60;
        int minutes = (int) ((elapsedTimeMills / (1000 * 60)) % 60);
        int hours = (int) ((elapsedTimeMills / (1000 * 60 * 60)) % 24);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Override
    public String toString() {
        return "ReadRecord{" +
                "bookPk='" + bookPk + '\'' +
                ", memo='" + memo + '\'' +
                ", elapsedTimeMills=" + elapsedTimeMills +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
