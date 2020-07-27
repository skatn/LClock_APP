package com.namsu.lclockapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;

import com.namsu.lclockapp.R;

public class FirstFragment extends Fragment {
    private TimePicker timePicker;
    private int hour, minutes;
    private boolean isHour24 = true;
    private Switch switch1;
    private boolean isFirst = true;
    private View view;

    // newInstance constructor for creating fragment with arguments
    public FirstFragment(){

    }

    public static FirstFragment newInstance() {
        FirstFragment fragment = new FirstFragment();
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

        if(isFirst){
            isFirst=false;
            view = inflater.inflate(R.layout.fragment_first, container, false);

            switch1 = (Switch) view.findViewById(R.id.switch1);
            switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) isHour24 = true;
                    else isHour24 = false;
                    setTime(hour, minutes);
                    if(((MainActivity)getActivity()).checkSync()){
                        ((MainActivity)getActivity()).sendData("set_hour_mode", isHour24? "0":"1");
                    }
                }
            });

            timePicker = (TimePicker) view.findViewById(R.id.timePicker);
            timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    hour = hourOfDay;
                    minutes = minute;
                    setTime(hour, minutes);
                }
            });

            setHour(0);
            setMinute(0);
            setCheck(isHour24);
        }

        return view;
    }

    public void setTime(int hour, int minute){
        ((MainActivity)getActivity()).setTime(hour, minute, isHour24);
    }
    public void setHour(int h){
        hour = h;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            timePicker.setHour(hour);
        } else {
            timePicker.setCurrentHour(hour);
        }
    }

    public void setMinute(int minute){
        minutes = minute;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            timePicker.setMinute(minutes);
        } else {
            timePicker.setCurrentMinute(minutes);
        }
    }

    public void setCheck(final boolean check){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch1.setChecked(check);
            }
        });
    }

    public void setHourMode(boolean state){
        isHour24 = state;
        setCheck(isHour24);
        setTime(hour, minutes);
    }

    public int getHour(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            return timePicker.getHour();
        } else {
            return timePicker.getCurrentHour();
        }
    }

    public int getMinute(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            return timePicker.getMinute();
        } else {
            return timePicker.getCurrentMinute();
        }
    }

    public boolean getHourMode(){
        return isHour24;
    }
}
