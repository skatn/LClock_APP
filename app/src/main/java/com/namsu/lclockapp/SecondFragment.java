package com.namsu.lclockapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.fragment.app.Fragment;

import com.namsu.lclockapp.R;

public class SecondFragment extends Fragment {
    private boolean isAuto = false;

    // newInstance constructor for creating fragment with arguments
    public static SecondFragment newInstance() {
        SecondFragment fragment = new SecondFragment();
        return fragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        final LinearLayout contain = (LinearLayout) view.findViewById(R.id.container);
        Switch brightSwitch = (Switch) view.findViewById(R.id.bright_mode);
        final Switch autoSwitch = (Switch) view.findViewById(R.id.auto_mode);

        if(isAuto) contain.setAlpha(1);
        else contain.setAlpha(0.2f);
        brightSwitch.setChecked(isAuto);
        autoSwitch.setClickable(isAuto);


        brightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isAuto = isChecked;

                if(isChecked){
                    contain.setAlpha(1);
                    autoSwitch.setClickable(true);
                }
                else{
                    contain.setAlpha(0.2f);
                    autoSwitch.setClickable(false);
                }
            }
        });

        autoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                }
                else{

                }
            }
        });

        return view;
    }
}
