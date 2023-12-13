package com.teamnova.dailybook.dto;

import android.net.Uri;

import com.teamnova.dailybook.R;

import java.util.ArrayList;

/**
 * 회원정보를 담는 객체
 */
public class User {
    public String email; // email이 pk 역할을 함
    public String password;
    public String nickName;
    public Uri imgUri; // 프사 이미지 경로
    public ArrayList<String> bookList = new ArrayList<>(); // book의 pk를 저장한 리스트 (나의 서재에 저장된)

//    public ArrayList<String> getBookList() {
//        DataManager.getInstance().removeDeleteBookPK(email);
//        return bookList;
//    }

    /**
     * 생성용
     *
     * @param email
     * @param password
     * @param nickName
     */
    public User(String email, String password, String nickName) {
        this.email = email;
        this.password = password;
        this.nickName = nickName;
        this.imgUri = Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + R.drawable.main_nav_profile);
    }

    /**
     * 카카오 회원원가입용
     *
     * @param email
     * @param password
     * @param nickName
     * @param imgUri
     */
    public User(String email, String password, String nickName, Uri imgUri) {
        this.email = email;
        this.password = password;
        this.nickName = nickName;
        this.imgUri = imgUri;
        this.bookList = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", nickName='" + nickName + '\'' +
                ", imgUri=" + imgUri +
                ", bookList=" + bookList +
                '}';
    }
}
