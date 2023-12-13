package com.teamnova.dailybook.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.teamnova.dailybook.R;
import com.teamnova.dailybook.adapter.KakaoBookSearchResultAdapter;
import com.teamnova.dailybook.data.DataManager;
import com.teamnova.dailybook.dto.Book;
import com.teamnova.dailybook.dto.KakaoBookSearchResult;
import com.teamnova.dailybook.kakao.KakaoService;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * 책 제목 입력 후 해당 제목으로 등록할지 검색할지 결정하는 액티비티
 * 책 검색 및 검색결과 조회
 */
public class BookSerachActivity extends AppCompatActivity {

    SearchView searchView;
    RecyclerView recyclerView;
    KakaoBookSearchResultAdapter adapter;
    Call<KakaoBookSearchResult> call;   // 검색요청
    Dialog dialog;
    int position; // 선택한 아이템의 포지션
    boolean isLoading = false;
    KakaoService service;
    String mQuery; // 검색 입력값
    int curPage = 1;
    String from; // 호출자

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search);

        searchView = findViewById(R.id.searchview_search_keyword);
        recyclerView = findViewById(R.id.recyclerview_booksearch_list);

        from = getIntent().getStringExtra("from");

        DataManager dm = DataManager.getInstance();

        // retrofit 세팅
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://dapi.kakao.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(KakaoService.class);

        // 커스텀 다이얼로그 세팅
        dialog = new Dialog(BookSerachActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.dialog_search_add_book);
        ImageView iv = dialog.findViewById(R.id.imageView_select_dialog_img);
        TextView tv_title = dialog.findViewById(R.id.textview_select_dialog_title);
        TextView tv_author = dialog.findViewById(R.id.textview_select_dialog_authors);
        TextView tv_translator = dialog.findViewById(R.id.textview_select_dialog_translator);
        TextView tv_publisher = dialog.findViewById(R.id.textview_select_dialog_publisher);
        TextView tv_pubdate = dialog.findViewById(R.id.textview_select_dialog_pubdate);
        TextView tv_content = dialog.findViewById(R.id.textview_select_dialog_contens);
        Button btn_yes = dialog.findViewById(R.id.button_select_dialog_yes);
        Button btn_no = dialog.findViewById(R.id.button_select_dialog_no);

        btn_yes.setOnClickListener(v -> {
            if (from != null && from.equals("AddRecord")) {
                Book book = adapter.getmData().get(position);

                Intent result = new Intent();
                result.putExtra("from", "BookSearch");
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
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // 고른 책 셰어드에 저장
                Book book = adapter.getmData().get(position);
                dm.createBook(book);
                Log.d("TAG", "검색해서 찾은 책 저장: " + book);
                startActivity(intent);
                Toast.makeText(this, "도서정보가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btn_no.setOnClickListener(v -> dialog.dismiss());


        // 리사이클러 뷰 세팅
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new KakaoBookSearchResultAdapter(getApplicationContext());    // 데이터는 나중에 검색결과로 세팅한다.

        // 스크롤 감지(무한 스크롤용)
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                // findLastCompletelyVisibleItemPosition 마지막 요소 출력 감지
                if (!isLoading) {
                    if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == adapter.getItemCount() - 1) {
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });

        adapter.setOnItemClickListener((v, pos) -> {    // 책을 선택하면 다이얼로그 등장
            position = pos;
            Book book = adapter.getmData().get(pos);
            Glide.with(getApplicationContext()).load(book.thumbnail).into(iv);
            tv_title.setText(book.title);
            tv_author.setText(Arrays.toString(book.authors).replace("[", "").replace("]", ""));
            tv_translator.setText(Arrays.toString(book.translators).replace("[", "").replace("]", ""));
            tv_publisher.setText(book.publisher);
            String str = "";
            if (book.dateTime != null) {
                str = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(book.dateTime);
            }
            tv_pubdate.setText(str);
            tv_content.setText(book.contents);

            // 다이얼로크 크기 조절
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes(params);
            dialog.show();
        });
        recyclerView.setAdapter(adapter);

        // 검색 값 입력완료
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mQuery = query; // 검색값은 여기서면 바뀐다.
                curPage = 1;    // 새로운 검색시 page 수는 1로 초기화
                call = service.searchBook(query, curPage, 10);
                kakaoCallback(call);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public void loadMore() {
        adapter.mData.add(null); // 로딩 아이템 추가
        adapter.notifyItemInserted(adapter.getItemCount() - 1);

        curPage += 1;
        call = service.searchBook(mQuery, curPage, 10);    // 다음페이지 요청
        kakaoCallback(call);
    }

    public void kakaoCallback(Call<KakaoBookSearchResult> call) {
        // 검색결과 콜백
        call.enqueue(new Callback<KakaoBookSearchResult>() {
            @Override
            public void onResponse(Call<KakaoBookSearchResult> call, Response<KakaoBookSearchResult> response) {
                if (response.isSuccessful()) {
                    KakaoBookSearchResult result = response.body();

                    // 새 데이터가 도착하면 로딩창(마지막 요소) 제거
                    if (adapter.mData.contains(null)) {
                        adapter.mData.remove(null);
                        adapter.notifyDataSetChanged();
                    }

                    if (curPage == 1) { // 새로운 검색 결과
                        adapter.setmData(result);
                    } else {            // 무한 스크롤링 결과
                        adapter.addmDate(result);
                        isLoading = false;
                    }
                    Log.d("TAG", "카카오 책 검색결과: \n" + result);
                } else {
                    try {
                        Log.d("TAG", "카카오 책 검색결과: 검색 실패" + response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<KakaoBookSearchResult> call, Throwable t) {
                Log.d("TAG", "카카오 책 검색결과: 시스템 실패(통신오류 등)");
            }
        });
    }
}