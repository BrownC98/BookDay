package com.teamnova.dailybook.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.teamnova.dailybook.dto.Book;
import com.teamnova.dailybook.dto.Essay;
import com.teamnova.dailybook.dto.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

/**
 * 셰어드 관리 클래스
 * 싱클턴으로 관리
 * init(context)로 초기화 하지 않으면 error
 */
public class DataManager {


    // 상수들을 묶어서 정리
    public static class CONST {
        public static final String TAG = "TAG";
        public static final String KEY_LAST_PK = "LAST_PK";   // 마지막 pk를 나타내는 키값
        public static final String CURRENT_USER_ID = "CURRENT_USER";
        public static final String REMEMBER_ME = "REMEMBER_ME";
    }

    private static DataManager instance;
    public Gson gson;
    public SharedPreferences userSP; // userLsit에는 현재 로그인한 계정의 정보(ID, 자동로그인 여부), 마지막으로 쓰인 ID값, user데이터 로 구성되어 있다.
    public SharedPreferences bookSP; // 마지막으로 쓰인 PK(b_1, b_2 ...)값, 책 데이터 들
    public SharedPreferences essaySP; // 마지막으로 쓰인 PK(m_1, m_2...)값, 책 데이터 들


    private DataManager() {
    }

    ;

    public static DataManager getInstance() {
        return instance;
    }

    /**
     * getInstance 사용 전 반드시 init을 먼저 실행할 것
     * 셰어드 및 LocalDateTime 직렬화 어댑터 등록
     *
     * @param context
     */
    @SuppressLint("NewApi")
    public static void init(Context context) {
        instance = new DataManager();
        instance.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .registerTypeAdapter(Uri.class, new UriSerializer())
                .create();
        instance.userSP = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        instance.bookSP = context.getSharedPreferences("BOOK", Context.MODE_PRIVATE);
        instance.essaySP = context.getSharedPreferences("ESSAY", Context.MODE_PRIVATE);
    }


    // 유저 데이터 순회해서 주어진 닉네임이 이미 존재하는지 검사
    public boolean containNickName(String nickName) {
        Map<String, ?> userMap = userSP.getAll();
        for (Map.Entry<String, ?> entry : userMap.entrySet()) {
            // key가 current_id or rememberMe 인 데이터는 패스
            if (((String) entry.getKey()).equals(CONST.CURRENT_USER_ID)
                    || ((String) entry.getKey()).equals(CONST.REMEMBER_ME)) continue;
            String v = (String) entry.getValue();

            if (nickName.equals(gson.fromJson(v, User.class).nickName)) return true;
        }
        return false;
    }

    public boolean containsUser(String key) {
        return userSP.contains(key);
    }

    public boolean containsUser(User user) {
        return containsUser(user.email);
    }

    /**
     * 중복계정 방지를 위해 생성 메소드, 갱신메소드 분리
     *
     * @param created
     * @return -1 : 중복발견, 0 : 정상처리
     */
    public int createUser(User created) {
        if (containsUser(created.email)) return -1;
        putUser(created);
        return 0;
    }

    /**
     * id에 해당하는 {@link User} 객체를 반환함
     * 찾는 객체가 없으면 null 반환
     * <p>
     *
     * @param email
     * @return
     */
    public User getUser(String email) {
        User user = gson.fromJson(userSP.getString(email, null), User.class);
        return user;
    }

    /**
     * 주어진 user와 동일한 email을 가진 데이터가 셰어드에 존재하면 데이터 update의 기능을 수행함
     *
     * @param user
     */
    public void updateUser(User user) {
        if (!containsUser(user.email)) return; // 데이터 생성 방지, 생성은 createUser에서 해야함

        putUser(user);
    }

    /**
     * 셰어드의 put 연산 수행
     *
     * @param user
     */
    private void putUser(User user) {
        if (user.imgUri == null) user.imgUri = Uri.EMPTY;

        String userJson = gson.toJson(user);

        Log.d("TAG", "updateUser: \n" + userJson);

        userSP.edit()
                .putString(user.email, userJson)
                .apply();
    }

    /**
     * 유저삭제
     * 유저하위의 책들과 에세이도 같이 삭제됨
     *
     * @param email
     */
    public void removeUser(String email) {
        // 하위 책 삭제, 책이 삭제되면 그 밑의 독후감도 같이 삭제된다.
        User user = getUser(email);
        for (int i = 0; i < user.bookList.size(); i++) {
            String bookPK = user.bookList.get(i);
            removeBook(bookPK); // 이게 실행될 때 하위 독후감도 삭제됨
        }

        // 계정 삭제 및 현재 로그인 계정정보 초기화
        userSP.edit()
                .remove(email)
                .putString(CONST.CURRENT_USER_ID, null)
                .putBoolean(CONST.REMEMBER_ME, false)
                .apply();
    }

    /**
     * @param email   - 로그아웃할 계정 이메일
     * @param context - 현재 컨텍스트
     * @param to      - 로그아웃 하고 이동할 액티비티
     */
    public void logOut(String email, Context context, Class<?> to) {
        userSP.edit()
                .remove(CONST.CURRENT_USER_ID)
                .remove(CONST.REMEMBER_ME)
                .apply();

        Intent intent = new Intent(context, to);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 실행할 액티비티 제외한 나머지 액티비티 제거
        context.startActivity(intent);
    }

    /**
     * {@link DataManager#setCurrentUser(User, boolean)} 를 통해 현재 유저가 지정되지
     * 않은채로 실행하면 null 반환
     *
     * @return {@link User}
     */
    public User getCurrentUser() {
        User ret = getUser(getCurrentId());
        Log.d("TAG", "getCurrentUser: " + ret);
        return ret;
    }

    /**
     * 현재 로그인한 유저 id를 별도의 key값으로 저장한다.
     *
     * @param user
     * @param rememberMe
     */
    public void setCurrentUser(User user, boolean rememberMe) {
        userSP.edit()
                .putString(CONST.CURRENT_USER_ID, user.email)
                .putBoolean(CONST.REMEMBER_ME, rememberMe)
                .apply();
    }

    public String getCurrentId() {
        String ret = userSP.getString(CONST.CURRENT_USER_ID, null);
        return ret;
    }

    public boolean isRememberMe() {
        return userSP.getBoolean(CONST.REMEMBER_ME, false);
    }

    public void createBook(Book created) {
        // 중복생성 방지
        if (bookSP.contains(created.getPK())) return;
        putBook(created);

        // owner의 보유 책에 추가
        User user = getUser(created.ownerPK);
        user.bookList.add(created.getPK());
        updateUser(user);
    }

    /**
     * 책 정보에 대해 put 연산 수행
     *
     * @param book
     */
    public void putBook(Book book) {

        if (book.thumbnail == null) book.thumbnail = "";

        String bookJson = gson.toJson(book);

        Log.d("TAG", "updateBook: \n" + bookJson);

        bookSP.edit()
                .putString(book.getPK(), bookJson)
                .apply();
    }

    public Book getBook(String pk) {
        return gson.fromJson(bookSP.getString(pk, null), Book.class);
    }

    /**
     * 입력받은 사용자의 책 목록 반환
     *
     * @param user
     * @return
     */
    public ArrayList<Book> getBookList(User user) {
        ArrayList<Book> ret = new ArrayList<>();

        for (String bId : user.bookList) {
            ret.add(getBook(bId));
        }
        return ret;
    }


    public void removeBook(String bookPK) {
        // 하위 에세이를 삭제한다.
        Book book = getBook(bookPK);
        for (int i = 0; i < book.essayList.size(); i++) {
            String essayPK = book.essayList.get(i);
            removeEssay(essayPK);   // book 셰어드의 보유 pk가 삭제되지만, 어차피 책 자체도 삭제되기 때문에 문제없음
        }

        // user에도 삭제 상태를 반영한다.
        User user = getUser(getBook(bookPK).ownerPK);
        user.bookList.remove(bookPK);
        updateUser(user);

        bookSP.edit().remove(bookPK).apply();
    }

    public boolean containsBook(String bookPK) {
        return bookSP.contains(bookPK);
    }


    /**
     * 독후감을 추가할 때, 해당 사실을 책 데이터에도 추가한다.
     *
     * @param created
     */
    public void createEssay(Essay created) {
        // 중복생성 방지
        if (essaySP.contains(created.getPK())) return;
        putEssay(created);

        // 추가된 사실을 책 데이터에도 기록
        Book book = getBook(created.bookPk);
        book.essayList.add(created.getPK());
        putBook(book);
    }

    /**
     * put 연산 실행
     * 인자로 주어진 객체가 셰어드에 없으면 생성하고, 있으면 수정한다.
     * 여기선 책 객체에 별도 처리를 하지 않는다.(이미 책에 추가되어 있기 때문이고, PK가 수정이 되진 않기 때문)
     *
     * @param essay
     */
    public void putEssay(Essay essay) {

        String essayJson = gson.toJson(essay);

        Log.d("TAG", "putEssay: \n" + essayJson);

        essaySP.edit()
                .putString(essay.getPK(), essayJson)
                .apply();
    }

    public Essay getEssay(String pk) {
        return gson.fromJson(essaySP.getString(pk, null), Essay.class);
    }

    /**
     * @param pk
     */
    public void removeEssay(String pk) {
        // 삭제 사실 책에도 반영
        Book book = getBook(getEssay(pk).bookPk);
        book.essayList.remove(pk); // remove는 equals()를 사용한다.
        putBook(book);

        essaySP.edit().remove(pk).apply();
    }

    public ArrayList<Essay> getEssayList(Book book) {
        ArrayList<Essay> ret = new ArrayList<>();

        for (String pk : book.essayList) {
            ret.add(getEssay(pk));
        }
        return ret;
    }


    public boolean containsEssay(String essayPK) {
        return essaySP.contains(essayPK);
    }
}