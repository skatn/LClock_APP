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
    private int brightMode = 0;
    LinearLayout llContainer;
    Switch brightSwitch, autoSwitch;


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

        llContainer = (LinearLayout) view.findViewById(R.id.container);
        brightSwitch = (Switch) view.findViewById(R.id.bright_mode);
        autoSwitch = (Switch) view.findViewById(R.id.auto_mode);

        setSetting();

        brightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    setMode(autoSwitch.isChecked()? 2 : 1);
                    //llContainer.setAlpha(1);
                    //autoSwitch.setClickable(true);
                }
                else{
                    setMode(0);
                    //brightMode = 0;
                    //llContainer.setAlpha(0.2f);
                    //autoSwitch.setClickable(false);
                }
            }
        });

        autoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    setMode(2);
                }
                else{
                    setMode(1);
                }
            }
        });

        return view;
    }

    public void setMode(int mode){
        brightMode = mode;
        setSetting();
        if(((MainActivity)getActivity()).checkSync()){
            ((MainActivity)getActivity()).sendData("set_bright_mode", String.valueOf(mode));
        }
    }

    private void setSetting(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(brightMode!=0) llContainer.setAlpha(1);
                else llContainer.setAlpha(0.2f);
                brightSwitch.setChecked(brightMode!=0);
                autoSwitch.setClickable(brightMode!=0);
            }
        });
    }
}
