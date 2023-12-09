package com.teamnova.bookday.kakao;

import com.teamnova.bookday.BuildConfig;
import com.teamnova.bookday.dto.Book;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * RetroFit 요청 인터페이스
 * kakao api 대상
 */
public interface KakaoService {
    // 요청 예시
    // https://dapi.kakao.com/v3/search/book?target=title"&query=미움받을 용기
    @Headers("Authorization: KakaoAK " +  BuildConfig.KAKAO_REST_API_KEY)
    @GET("v3/search/book?target=title")
    Call<Book> searchBook(@Query("query") String title);
}
