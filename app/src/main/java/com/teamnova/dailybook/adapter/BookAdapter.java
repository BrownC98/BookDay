package com.teamnova.dailybook.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.teamnova.dailybook.R;
import com.teamnova.dailybook.dto.Book;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 개별 책 아이템을 표시하기 위한 아답터
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    public ArrayList<Book> mData = null;
    public Context context;

    public BookAdapter(Context context, ArrayList<Book> list) {
        this.context = context;
        this.mData = list;
    }

    public void setmData(ArrayList<Book> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }

    // 아이템 클릭시 상세 페이지로 이동해야하기 때문에 커스텀 리스너를 사용한다.
    // 커스텀 리스너 코드 *
    public onItemClickListener mListener;

    public interface onItemClickListener {
        void onItemClick(View v, int pos);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.mListener = listener;
    }

    // * 커스텀 리스너 코드

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textview_title;
        TextView textview_author;
        TextView textview_publisher;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                // 클릭시 사전에 설정한 리스너 콜백 실행
                if (pos != RecyclerView.NO_POSITION && mListener != null) {
                    mListener.onItemClick(v, pos);
                }
            });

            imageView = itemView.findViewById(R.id.imageview_bookitem_bookimage);
            textview_title = itemView.findViewById(R.id.textview_bookitem_title);
            textview_author = itemView.findViewById(R.id.textview_bookitem_author);
            textview_publisher = itemView.findViewById(R.id.textview_bookitem_publisher);
        }
    }

    // 뷰을 담은 뷰홀더 반환
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_book_item, parent, false);
        return new ViewHolder(view);
    }

    // ViewHolder 값 세팅
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = mData.get(position);
        Glide.with(context).load(Uri.parse(book.thumbnail)).into(holder.imageView);
        holder.textview_title.setText(book.title);
        holder.textview_author.setText(Arrays.toString(book.authors).replace("[", "").replace("]", ""));
        holder.textview_publisher.setText(book.publisher);
    }

    // 데이터 개수
    @Override
    public int getItemCount() {
        return mData.size();
    }


}
