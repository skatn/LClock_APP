package com.namsu.lclockapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.fragment.app.Fragment;

public class ThirdFragment extends Fragment {
    private boolean showAMPM = false;
    Switch colonSwitch;

    // newInstance constructor for creating fragment with arguments
    public static ThirdFragment newInstance() {
        ThirdFragment fragment = new ThirdFragment();
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
        View view = inflater.inflate(R.layout.fragment_third, container, false);

        colonSwitch = (Switch) view.findViewById(R.id.colon_mode);
        colonSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(((MainActivity)getActivity()).checkSync()){
                    if(isChecked) ((MainActivity)getActivity()).sendData("set_colon_mode", "1");
                    else ((MainActivity)getActivity()).sendData("set_colon_mode", "0");
                }
            }
        });

        return view;
    }

    public void setColonMode(boolean state){
        showAMPM = state;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                colonSwitch.setChecked(showAMPM);
            }
        });
    }
}
