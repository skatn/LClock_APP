package com.namsu.lclockapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;

public class TimeFragment extends Fragment {
    private TimePicker timePicker;
    private static int hour, minutes;
    private static boolean isHour24 = true;
    private Switch switch1;
    private boolean isFirst = true;
    private View view;
    private int timeOffset = 0;

    // newInstance constructor for creating fragment with arguments
    public TimeFragment(){

    }

    public static TimeFragment newInstance() {
        TimeFragment fragment = new TimeFragment();
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
                    sendHourMode();
                }
            });

            timePicker = (TimePicker) view.findViewById(R.id.timePicker);
            timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    setTime(hourOfDay, minute);
                    sendTimeOffset();
                }
            });

            setHour(0);
            setMinute(0);
            updateHour();
            updateMinute();
        }

        return view;
    }

    private void updateHour(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            timePicker.setHour(hour);
        } else {
            timePicker.setCurrentHour(hour);
        }
    }

    private void updateMinute(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            timePicker.setMinute(minutes);
        } else {
            timePicker.setCurrentMinute(minutes);
        }
    }

    public void update(){
        try {
            updateHour();
            updateMinute();

            int h = hour;
            if (!isHour24) {
                if (h > 12) h -= 12;
                else if (h == 0) h = 12;
            }
            ((MainActivity) getActivity()).setTime(h, minutes);

            //update hour mode 12 or 24
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch1.setChecked(isHour24);
                }
            });
        } catch (Exception e){
            Log.d("qqqq", e.getMessage());
        }
    }

    private void sendHourMode(){
        if(((MainActivity)getActivity()).checkSync()){
            ((MainActivity)getActivity()).sendData("set_hour_mode", isHour24? "0":"1");
        }
    }

    private void sendTimeOffset(){
        if(((MainActivity)getActivity()).checkSync()){
            ((MainActivity)getActivity()).sendData("set_time", String.valueOf(timeOffset));
        }
    }

    private void setTime(int h, int m){
        timeOffset += (h-hour)*60*60;
        timeOffset += (m-minutes)*60;

        if(!isHour24){
            if(h>12)h -= 12;
            else if(h==0)h = 12;
        }

        ((MainActivity)getActivity()).setTime(h, m);

        hour = h;
        minutes = m;
    }

    public void setTimeOffset(int offset){
        timeOffset = offset;
    }

    public static void setHour(int h){
        hour = h;
    }

    public static void setMinute(int minute){
        minutes = minute;
    }

    public static void setHourMode(boolean state){
        isHour24 = state;
    }
}
