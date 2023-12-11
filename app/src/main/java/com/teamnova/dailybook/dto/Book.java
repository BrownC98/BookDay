package com.teamnova.dailybook.dto;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 카카오 도서검색 api를 통해 받아온 정보를 저장하는 클래스
 * 기존 {@link Book} 클래스를 이 클래스로 대체할지 고려 중
 */
public class Book {

    static class Meta {
        public int total_count;     // 검색된 문서 수
        public int pageable_count;  // 중복된 문서를 제외하고, 처음부터 요청 페이지까지의 노출 가능 문서 수
        public boolean is_end;      // 현재 페이지가 마지막 페이지인지 여부, 값이 false면 page를 증가시켜 다음 페이지를 요청할 수 있음

        @Override
        public String toString() {
            return "meta{" +
                    "total_count=" + total_count +
                    ", pageable_count=" + pageable_count +
                    ", is_end=" + is_end +
                    '}';
        }
    }

    static class Document {
        @SerializedName("title")    // json(gson)으로 변환시 매칭할 변수명 - 미지정시, 변수명을 맞춰줘야함
        public String title;            // 도서 제목
        public String contents;         // 도서소개
        public String url;              // 해당 도서의 다음 포털 검색결과 링크
        public String isbn;             // ISBN 코드 10자리 또는 13자리, 두 값이 모두 제공되면 공백 (' ')으로 구분
        public LocalDateTime dateTime;  // 도서 출판날짜, [YYYY]-[MM]-[DD]T[hh]:[mm]:[ss].000+[tz]
        public String[] authors;        // 저자리스트
        public String publisher;        // 출판사
        public String[] translators;    // 번역자 리스트
        public int price;               // 정가
        public int sale_price;          // 판매가
        public String thumbnail;        // 미리보기 URL
        public String status;           // 판매 상태(정상, 품절, 절판 등) read only로 활용할 것

        @Override
        public String toString() {
            return "documents{" +
                    "title='" + title + '\'' +
                    ", contents='" + contents + '\'' +
                    ", url='" + url + '\'' +
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

    public Meta meta;
    public Document[] documents;

    @Override
    public String toString() {
        return "KaKaoBookDto{" +
                "meta=" + meta +
                ", documents=" + Arrays.toString(documents) +
                '}';
    }
}


