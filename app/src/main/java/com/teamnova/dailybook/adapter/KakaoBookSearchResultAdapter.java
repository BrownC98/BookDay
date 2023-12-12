package com.teamnova.dailybook.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.teamnova.dailybook.R;
import com.teamnova.dailybook.dto.Book;
import com.teamnova.dailybook.dto.KakaoBookSearchResult;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 카카오 책 검색결과를 표시하기 위한 아답터
 *
 */
public class KakaoBookSearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public ArrayList<Book> mData = new ArrayList<>();
    public Context context;

    public ArrayList<Book> getmData() {
        return mData;
    }

    //  public KakaoBookSearchResultAdapter(){}

    public KakaoBookSearchResultAdapter(Context context) {
        this.context = context;
    }

    public void setmData(KakaoBookSearchResult mData) {
        this.mData = new ArrayList<>(Arrays.asList(mData.documents));
        notifyDataSetChanged();
    }

    public void addmDate(KakaoBookSearchResult mData) {
        int startPos = this.mData.size();
        this.mData.addAll(Arrays.asList(mData.documents));
        notifyItemRangeInserted(startPos, Arrays.asList(mData.documents).size());
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

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textview_title;
        TextView textview_author;
        TextView textview_publisher;

        public ItemViewHolder(@NonNull View itemView) {
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

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    // 뷰을 담은 뷰홀더 반환
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_book_item, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_book_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ItemViewHolder){
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            Book book = mData.get(position);
            Glide.with(context).load(Uri.parse(book.thumbnail)).into(itemHolder.imageView);
            itemHolder.textview_title.setText(book.title);
            String authors = Arrays.toString(book.authors).replace("[", "").replace("]", "");
            itemHolder.textview_author.setText(authors);
            itemHolder.textview_publisher.setText(book.publisher);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    // 데이터 개수
    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }


}
