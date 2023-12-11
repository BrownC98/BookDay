package com.teamnova.dailybook.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.teamnova.dailybook.dto.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 셰어드 관리 클래스
 * 싱클턴으로 관리
 * init(context)로 초기화 하지 않으면 error
 */
public class DataManager {

    // 상수들을 묶어서 정리
    private static class CONST {
        private static final String TAG = "TAG";
        private static final String KEY_LAST_PK = "LAST_PK";   // 마지막 pk를 나타내는 키값
        private static final String CURRENT_USER_ID = "CURRENT_USER";
        private static final String REMEMBER_ME = "REMEMBER_ME";
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

//    private static int getNextUserId() {
//        int ret = userList.getInt(KEY_LAST_ID, -1) + 1;
//        userList.edit().putInt(KEY_LAST_ID, ret).apply();
//        return ret;
//    }

    /**
//     * @param sp bookList or memoList
     * @return
     */
//    private static String getNextId(SharedPreferences sp) {
//        String lastId = sp.getString(KEY_LAST_ID, null); // b_1, b_2 ...
//
//        String nextId = makeNextId(sp, lastId);
//
//        sp.edit().putString(KEY_LAST_ID, nextId).apply();
//        return nextId;
//    }
//
//    // 아이디 증가 문자열 처리 메소드
//    private static String makeNextId(SharedPreferences sp, String lastId) {
//        if (lastId == null) {
//            if (sp == bookSP) lastId = "b_0";
//            else if (sp == essaySP) lastId = "m_0";
//        }
//
//        String arr[] = lastId.split("_");
//        return arr[0] + "_" + (Integer.parseInt(arr[1]) + 1);
//    }


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
     *
     * @param email
     * @return
     */
    public User getUser(String email) {
        return gson.fromJson(userSP.getString(email, null), User.class);
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

    public void removeUser(String email) {
//        // 논리적 삭제처리 - 보류
//        User user = getUser(email);
//        user.deleted = true;
//        updateUser(user);
        // 보유중인 책 제거
        for (String bId : getUser(email).bookList) {
            // TODO removeBook(email, bId);
        }

        // 현재 로그인 계정정보 초기화
        userSP.edit()
                .remove(email)
                .putString(CONST.CURRENT_USER_ID, null)
                .putBoolean(CONST.REMEMBER_ME, false)
                .apply();
    }

    /**
     *
     * @param email - 로그아웃할 계정 이메일
     * @param context - 현재 컨텍스트
     * @param to - 로그아웃 하고 이동할 액티비티
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
        return getUser(getCurrentId());
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
        return userSP.getString(CONST.CURRENT_USER_ID, null);
    }

    public boolean isRememberMe() {
        return userSP.getBoolean(CONST.REMEMBER_ME, false);
    }

//    public static void createBook(User user, Book created) {
//        if (bookSP.contains(created.id)) return;
//        created.id = getNextId(bookSP);
//        updateBook(created);
//
//        user.bookList.add(created.id); // 유저의 책 리스트에는 책 id만 저장한다.
//        updateUser(user);
//    }
//
//    public static void updateBook(Book book) {
//
//        if (book.imgpath == null) book.imgpath = Util.CONSTANT.EMPTY_STRING;
//
//        String bookJson = gson.toJson(book);
//
//        Log.d("TAG", "updateBook: \n" + bookJson);
//
//        bookSP.edit()
//                .putString(book.id, bookJson)
//                .apply();
//    }
//
//    public static Book getBook(String id) {
//        return gson.fromJson(bookSP.getString(id, null), Book.class);
//    }
//
//    public static ArrayList<Book> getBookList(User user) {
//        ArrayList<Book> ret = new ArrayList<>();
//        for (String bId : user.bookList) {
//            ret.add(getBook(bId));
//        }
//        return ret;
//    }
//
//    public static void removeBook(String userId, String bookId) {
//        // user객체의 책 id도 제거해야함
//        User user = getUser(userId);
//        user.bookList.removeIf(bId -> bId.equals(bookId));
//        updateUser(user); // 보유한 책 목록에서 제거
//
//        // 책이 가지고 있던 메모를 셰어드에서 제거
//        for (String mId : getBook(bookId).memoList) {
//            removeMemo(bookId, mId);
//        }
//
//        bookSP.edit().remove(bookId).apply();
//    }
//
//    public static void createMemo(Book book, Memo created) {
//        if (essaySP.contains(created.id)) return;
//        created.id = getNextId(essaySP);
//        updateMemo(created);
//        book.memoList.add(created.id);
//        updateBook(book);
//    }
//
//    public static void updateMemo(Memo memo) {
//        //if (!memoList.contains(memo.id)) return;
//
//        String memoJson = gson.toJson(memo);
//
//        Log.d("TAG", "updateMemo: \n" + memoJson);
//
//        essaySP.edit()
//                .putString(memo.id, memoJson)
//                .apply();
//    }
//
//    public static Memo getMemo(String id) {
//        return gson.fromJson(essaySP.getString(id, null), Memo.class);
//    }
//
//    public static void removeMemo(String bookId, String memoId) {
//        // 책의 메모리스트 정보도 갱신해야함
//        Book book = getBook(bookId);
//        book.memoList.removeIf(mId -> mId.equals(memoId));
//        updateBook(book);
//
//        essaySP.edit().remove(memoId).apply();
//    }
//
//    public static ArrayList<Memo> getMemoList(Book book) {
//        ArrayList<Memo> ret = new ArrayList<>();
//        for (String mId : book.memoList) {
//            ret.add(getMemo(mId));
//        }
//        return ret;
//    }

}