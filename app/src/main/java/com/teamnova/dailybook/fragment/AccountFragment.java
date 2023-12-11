package com.teamnova.dailybook.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.teamnova.dailybook.R;
import com.teamnova.dailybook.activity.LoginActivity;
import com.teamnova.dailybook.data.DataManager;
import com.teamnova.dailybook.dto.User;

public class AccountFragment extends Fragment {

    private final int REQUEST_CODE = 10000;

    ImageView iv_profile;
    EditText et_nickname;
    TextView tv_nickname;
    EditText et_email;
    Button btn_save;
    TextView tv_logout;
    TextView tv_drop;
    DataManager dm;
    User user;

    Toast toast;

    Uri currentUri;

    public AccountFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    // 액티비티의 onCreate와 비슷한 상태
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dm = DataManager.getInstance();
        user = dm.getCurrentUser();

        toast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);

        iv_profile = view.findViewById(R.id.imageview_account_image);
        et_nickname = view.findViewById(R.id.et_account_nickname);
        tv_nickname = view.findViewById(R.id.tv_account_nickname);
        et_email = view.findViewById(R.id.et_account_eamil);
        btn_save = view.findViewById(R.id.button_account_save);
        tv_logout = view.findViewById(R.id.tv_account_logout);
        tv_drop = view.findViewById(R.id.tv_account_drop);

        Glide.with(this).load(user.imgUri).into(iv_profile);
        currentUri = user.imgUri;
        et_nickname.setText(user.nickName);
        et_email.setText(user.email);

        iv_profile.setOnClickListener(new OnClickListner());
        btn_save.setOnClickListener(new OnClickListner());
        tv_logout.setOnClickListener(new OnClickListner());
        tv_drop.setOnClickListener(new OnClickListner());

        et_nickname.addTextChangedListener(new mTextWatcher());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Glide.with(this).load(uri).into(iv_profile);
            currentUri = uri;
        }
    }

    class OnClickListner implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v == iv_profile) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE);
            } else if (v == btn_save) {
                // 닉네임이 문제 없는지 확인, 문제있으면 원래대로 복구
                if (tv_nickname.getCurrentTextColor() == getContext().getColor(R.color.warn)
                        || et_nickname.getText().toString().length() == 0) {
                    toast.setText("올바른 형식의 닉네임이 아닙니다.");
                    toast.show();
                    tv_nickname.setVisibility(View.INVISIBLE);
                    et_nickname.setText(user.nickName);
                    et_nickname.requestFocus();
                    return;
                }

                user.nickName = et_nickname.getText().toString();
                user.imgUri = currentUri;
                dm.updateUser(user);
                toast.setText("저장되었습니다.");
                toast.show();
            } else if (v == tv_logout) {
                new AlertDialog.Builder(getContext())
                        .setTitle("로그아웃")
                        .setMessage("로그아웃 하시겠습니까?")
                        .setNegativeButton("아니오", (dialog, which) -> {
                        })
                        .setPositiveButton("예", (dialog, which) -> {
                            dm.logOut(dm.getCurrentId(), getContext(), LoginActivity.class);
                            toast.setText("로그아웃 되었습니다.");
                            toast.show();
                        })
                        .setCancelable(false)
                        .create()
                        .show();



            } else if (v == tv_drop) {
                new AlertDialog.Builder(getContext())
                        .setTitle("회원탈퇴")
                        .setMessage("회원탈퇴를 진행하시겠습니까?")
                        .setNegativeButton("아니오", (dialog, which) -> {
                        })
                        .setPositiveButton("예", (dialog, which) -> {
                            // 탈퇴처리 코드
                            dm.removeUser(dm.getCurrentId());

                            Toast.makeText(getContext(), "탈퇴처리 되었습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }
        }
    }

    class mTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String input = et_nickname.getText().toString();
            TextView tv = tv_nickname;

            if (input.length() == 0) {
                fail(tv, "필수입력 항목입니다.");
                return;
            }

            boolean isDuplicate = DataManager.getInstance().containNickName(input);
            if (isDuplicate) {
                fail(tv, "이 닉네임은 사용할 수 없습니다.");
                return;
            }

            pass(tv, "사용할 수 있는 닉네임입니다.");
        }


        // 유효성 검사 실패
        private void fail(TextView tv, String msg) {
            tv.setText(msg);
            tv.setTextColor(getContext().getColor(R.color.warn));
            tv.setVisibility(View.VISIBLE);
        }

        // 유효성 검사 통과
        private void pass(TextView tv, String msg) {
            tv.setText(msg);
            tv.setTextColor(getContext().getColor(R.color.ok));
            tv.setVisibility(View.VISIBLE);
        }
    }

}