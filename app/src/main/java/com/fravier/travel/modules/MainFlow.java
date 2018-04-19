package com.fravier.travel.modules;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fravier.travel.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFlow extends Fragment {


    public MainFlow() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_flow, container, false);
    }

}
