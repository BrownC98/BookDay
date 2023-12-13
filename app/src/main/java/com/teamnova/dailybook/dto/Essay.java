package com.teamnova.dailybook.dto;

public class Essay {
    public String title;
    public String content;
    public String PK; // 식별자
    public String owner;    // 소유자
    public String bookPk; // 에세이 대상

    public Essay(String owner, String bookPk) {
        this.owner = owner;
        this.bookPk = bookPk;
    }

    /**
     * 처음 입력한 값기반으로 PK 생성
     *
     * @return
     */
    public String getPK() {
        if (PK == null)
            PK = owner +  title + hashCode();
        return PK;
    }

    @Override
    public String toString() {
        return "Essay{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", PK='" + PK + '\'' +
                ", owner='" + owner + '\'' +
                ", bookPk='" + bookPk + '\'' +
                '}';
    }
}
