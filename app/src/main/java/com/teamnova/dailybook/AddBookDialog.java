package com.teamnova.dailybook;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.teamnova.dailybook.data.DataManager;
import com.teamnova.dailybook.dto.Book;


// 도서추가 커스텀 다이얼로그
public class AddBookDialog extends Dialog {

    public ImageView iv = findViewById(R.id.imageView_select_dialog_img);
    public TextView tv_title = findViewById(R.id.textview_select_dialog_title);
    public  TextView tv_author = findViewById(R.id.textview_select_dialog_authors);
    public TextView tv_translator = findViewById(R.id.textview_select_dialog_translator);
    public   TextView tv_publisher = findViewById(R.id.textview_select_dialog_publisher);
    public TextView tv_pubdate = findViewById(R.id.textview_select_dialog_pubdate);
    public   TextView tv_content = findViewById(R.id.textview_select_dialog_contens);
    public   Button btn_yes = findViewById(R.id.button_select_dialog_yes);
    public  Button btn_no = findViewById(R.id.button_select_dialog_no);
    DataManager dm;

    public AddBookDialog(@NonNull Context context,
                         Book data,
                         Class<?> moveTo) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        setContentView(R.layout.dialog_search_add_book);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);

        dm = DataManager.getInstance();

        btn_yes.setOnClickListener(v -> {
            Intent intent = new Intent(context, moveTo);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // 책 정보를 인텐트에 담아서 전송 (book을  json으로 바꿔서)
            String bookJson = dm.gson.toJson(data, Book.class);
            Log.d("TAG", "bookJson: " + bookJson);
            intent.putExtra("BOOK", bookJson);
            context.startActivity(intent);
            Toast.makeText(context, "도서정보가 추가되었습니다.", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        btn_no.setOnClickListener(v -> dismiss());
    }
}
