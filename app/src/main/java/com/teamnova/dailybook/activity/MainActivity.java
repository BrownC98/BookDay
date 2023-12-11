package com.teamnova.dailybook.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.teamnova.dailybook.R;
import com.teamnova.dailybook.fragment.MyBooksFragment;
import com.teamnova.dailybook.fragment.ProfileFragment;
import com.teamnova.dailybook.fragment.ReadFragment;
import com.teamnova.dailybook.fragment.RecordFragment;

/**
 * 각종 기능으로 접근하는 통로가 되는 액티비티
 * 바텀 네비게이션으로 각 기능에 접근한다.
 */
public class MainActivity extends AppCompatActivity {

    BottomNavigationView bnv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bnv = findViewById(R.id.bottom_navigation_view_main);
        bnv.setOnItemSelectedListener(new ItemSelectedListner());

    }

    class ItemSelectedListner implements NavigationBarView.OnItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment;

            if (id == R.id.item_main_nav_mybooks) {
                fragment = new MyBooksFragment();
            } else if (id == R.id.item_main_nav_read) {
                fragment = new ReadFragment();
            } else if (id == R.id.item_main_nav_record) {
                fragment = new RecordFragment();
            } else if (id == R.id.item_main_nav_profile) {
                fragment = new ProfileFragment();
            } else {
                return false;
            }

            transaction.replace(R.id.fragment_container_view_main, fragment).commit();

            return true;
        }
    }

}