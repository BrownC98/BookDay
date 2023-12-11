package com.teamnova.dailybook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.teamnova.dailybook.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyBooksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyBooksFragment extends Fragment {


    public MyBooksFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_books, container, false);
    }
}