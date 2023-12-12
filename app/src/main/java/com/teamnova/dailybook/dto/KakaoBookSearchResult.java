package com.teamnova.dailybook.dto;

import androidx.annotation.NonNull;

import com.teamnova.dailybook.data.DataManager;

import java.util.Arrays;

/**
 * 카카오 도서검색 api를 통해 받아온 정보를 저장하는 클래스
 */
public class KakaoBookSearchResult {

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

    public static class Document extends Book {

        public Document(){
            super(DataManager.getInstance().getCurrentId(), "kakao");
        }

        public String url;              // 해당 도서의 다음 포털 검색결과 링크

        @Override
        public String toString() {
            return "Document{" +
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

    @NonNull
    @Override
    public String toString() {
        return "KakaoBookSearchResult{" +
                "meta=" + meta +
                ", documents=" + Arrays.toString(documents) +
                '}';
    }
}


