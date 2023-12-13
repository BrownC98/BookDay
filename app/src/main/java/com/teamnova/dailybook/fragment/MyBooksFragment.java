package com.teamnova.dailybook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.teamnova.dailybook.OnDataPassedListener;
import com.teamnova.dailybook.R;
import com.teamnova.dailybook.activity.AddBookActivity;
import com.teamnova.dailybook.activity.BookDetailActivity;
import com.teamnova.dailybook.adapter.BookAdapter;
import com.teamnova.dailybook.data.DataManager;
import com.teamnova.dailybook.dto.Book;
import com.teamnova.dailybook.dto.User;

import java.util.ArrayList;

public class MyBooksFragment extends Fragment {

    DataManager dm;
    Toast toast;
    Button btn_add;
    RecyclerView recyclerview_bookList;
    BookAdapter bookAdapter;
    String from;

    public MyBooksFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_books, container, false);
    }

    /**
     * activity의 onCreate에서 하는 일을 여기서 하면 됨
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);

        dm = DataManager.getInstance();
        User user = dm.getCurrentUser();
        ArrayList<Book> books = dm.getBookList(user);

        btn_add = view.findViewById(R.id.button_mybooks_addbook);
        recyclerview_bookList = view.findViewById(R.id.recyclerview_mybooks_list);

        btn_add.setOnClickListener(new OnClickListener());
        btn_add.setVisibility(View.VISIBLE);

        // 특정 액티비티에서 온걸 여기서 감지한다.
        Bundle bundle = getArguments();
        if (bundle != null) {
            from = bundle.getString("from");

            // 내 서재에서 찾기
            if (from.equals("AddRecord")) {
                btn_add.setVisibility(View.GONE);
            }
        }


        // 리사이클러 뷰 세팅
        recyclerview_bookList.setLayoutManager(new LinearLayoutManager(getContext()));
        bookAdapter = new BookAdapter(getContext(), books);
        bookAdapter.setOnItemClickListener(((v, pos) -> {
            String bookPK =  bookAdapter.mData.get(pos).getPK();

            // 기록용으로 여기 왔으면 선택한 책의 pk를 반환하고 종료
            if (from != null && from.equals("AddRecord")) {
                ((OnDataPassedListener) getActivity()).onDataPassed(bookPK);
                return;
            }

            Intent intent = new Intent(getContext(), BookDetailActivity.class);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // 책 PK 담아서 전송
            intent.putExtra("BOOK_PK", bookPK);
            Log.d("TAG", "onViewCreated: 데이터 전송 key : BOOK_PK -> " + bookPK);
            startActivity(intent);
        }));
        recyclerview_bookList.setAdapter(bookAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        bookAdapter.setmData(dm.getBookList(dm.getCurrentUser()));
    }

    class OnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v == btn_add) {
                Intent intent = new Intent(getActivity(), AddBookActivity.class);
                startActivity(intent);
            }
        }
    }

}