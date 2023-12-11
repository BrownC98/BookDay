package com.teamnova.dailybook.dto;

import android.net.Uri;

import java.util.ArrayList;

/**
 * 회원정보를 담는 객체
 */
public class User {
    public String email; // email이 pk 역할을 함
    public String password;
    public String nickName;
    public Uri imgUri; // 프사 이미지 경로
    public ArrayList<String> bookList; // book의 pk를 저장한 리스트 (나의 서재에 저장된)

    /**
     * 생성용
     * @param email
     * @param password
     * @param nickName
     */
    public User(String email, String password, String nickName) {
        this.email = email;
        this.password = password;
        this.nickName = nickName;
        this.imgUri = Uri.EMPTY;
        this.bookList = new ArrayList<>();
    }

    /**
     * 카카오 계정으로 회원가입용
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
