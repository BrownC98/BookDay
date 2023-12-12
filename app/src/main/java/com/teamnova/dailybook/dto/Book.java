package com.teamnova.dailybook.dto;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 책 한권의 정보를 담는 클래스
 */
public class Book {

    public String title;            // 도서 제목
    public String contents;         // 도서소개
    public String isbn;             // ISBN 코드 10자리 또는 13자리, 두 값이 모두 제공되면 공백 (' ')으로 구분
    public LocalDateTime dateTime;  // 도서 출판날짜, [YYYY]-[MM]-[DD]T[hh]:[mm]:[ss].000+[tz]
    public String[] authors;        // 저자리스트
    public String publisher;        // 출판사
    public String[] translators;    // 번역자 리스트
    public int price;               // 정가
    public int sale_price;          // 판매가
    public String thumbnail;        // 미리보기 URL
    public String status;           // 판매 상태(정상, 품절, 절판 등) read only로 활용할 것

    public String PK; // 식별자
    public String owner;    // 책 정보 소유자
    public String dataSource;   // 정보출처

    public Book() {
    }

    public Book(String owner, String dataSource) {
        this.owner = owner;
        this.dataSource = dataSource;
    }

    /**
     * 처음 입력한 값기반으로 PK 생성
     * @return
     */
    public String getPK() {
        if (PK == null) PK = owner + title + dateTime + Arrays.toString(authors) + publisher + Arrays.toString(translators) + dataSource;
        return PK;
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", isbn='" + isbn + '\'' +
                ", dateTime=" + dateTime +
                ", authors=" + Arrays.toString(authors) +
                ", publisher='" + publisher + '\'' +
                ", translators=" + Arrays.toString(translators) +
                ", price=" + price +
                ", sale_price=" + sale_price +
                ", thumbnail='" + thumbnail + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
