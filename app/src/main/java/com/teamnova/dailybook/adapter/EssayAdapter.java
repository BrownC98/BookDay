package com.teamnova.dailybook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teamnova.dailybook.R;
import com.teamnova.dailybook.dto.Essay;

import java.util.ArrayList;

/**
 * 개별 독후감 아이템을 표시하기 위한 아답터
 */
public class EssayAdapter extends RecyclerView.Adapter<EssayAdapter.ViewHolder> {

    public ArrayList<Essay> mData = new ArrayList<>();
    public Context context;

    public EssayAdapter(Context context, ArrayList<Essay> list) {
        this.context = context;
        this.mData = list;
    }

    public void setmData(ArrayList<Essay> mData) {
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
        TextView title;
        TextView content;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                // 클릭시 사전에 설정한 리스너 콜백 실행
                if (pos != RecyclerView.NO_POSITION && mListener != null) {
                    mListener.onItemClick(v, pos);
                }
            });

            title = itemView.findViewById(R.id.textview_essay_item_title);
            content = itemView.findViewById(R.id.textview_essay_item_content);
        }
    }

    // 뷰을 담은 뷰홀더 반환
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_essay_item, parent, false);
        return new ViewHolder(view);
    }

    // ViewHolder 값 세팅
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Essay essay = mData.get(position);
        if(essay == null) return;
        holder.title.setText(essay.title);
        holder.content.setText(essay.content);
    }

    // 데이터 개수
    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }


}
