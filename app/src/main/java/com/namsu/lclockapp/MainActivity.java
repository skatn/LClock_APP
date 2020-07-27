package com.namsu.lclockapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.namsu.lclockapp.R;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


import me.relex.circleindicator.CircleIndicator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "qqqq";
    private OkHttpClient client;
    private String lastParam;
    private FirstFragment firstFragment;
    private SecondFragment secondFragment;
    private ThirdFragment thirdFragment;
    private Timer timer;
    private TimerTask timerTask;
    private boolean isSynced = false;
    SeekBar bright_bar;
    private int brightness = 50;
    int timeOffset = 0;
    int currTime = 0;

    FragmentPagerAdapter adapterViewPager;
    static ImageView firstSegment, secondSegment, colonSegment, thirdSegment, forthSegment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = new OkHttpClient();
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);

        timer = new Timer();

        firstFragment = FirstFragment.newInstance();
        secondFragment = SecondFragment.newInstance();
        thirdFragment = ThirdFragment.newInstance();

        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);

        firstSegment = (ImageView) findViewById(R.id.firstSegment);
        secondSegment = (ImageView) findViewById(R.id.secondSegment);
        colonSegment = (ImageView) findViewById(R.id.colonSegment);
        thirdSegment = (ImageView) findViewById(R.id.thirdSegment);
        forthSegment = (ImageView) findViewById(R.id.forthSegment);


        Button connectToClock = (Button) findViewById(R.id.connect_clock);
        connectToClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData("sync_request", "");
                startTimerTask();

                /*if(checkWifi("L-Clock")){

                }
                else{
                    Toast.makeText(getApplicationContext(), "L-Clock에 연결하여 주십시오.", Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        Button autoConnect = (Button) findViewById(R.id.auto_connect);
        autoConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WebActivity.class);
                startActivity(intent);
                if(checkWifi("L-Clock-Auto-connect")){
                }
                else{
                    Toast.makeText(getApplicationContext(), "L-Clock-Auto-connect에 연결하여 주십시오.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        bright_bar = (SeekBar) findViewById(R.id.bright_bar);
        bright_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                firstSegment.setAlpha(1-(100-progress)/100.0f);
                secondSegment.setAlpha(1-(100-progress)/100.0f);
                colonSegment.setAlpha(1-(100-progress)/100.0f);
                thirdSegment.setAlpha(1-(100-progress)/100.0f);
                forthSegment.setAlpha(1-(100-progress)/100.0f);
                brightness = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sendData("set_brightness", String.valueOf(brightness));
            }
        });
        setBrightness(55);
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS = 3;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if(firstFragment==null) firstFragment = FirstFragment.newInstance();
                    return firstFragment;
                case 1:
                    if(secondFragment==null) secondFragment = SecondFragment.newInstance();
                    return secondFragment;
                case 2:
                    if(thirdFragment==null) thirdFragment = ThirdFragment.newInstance();
                    return thirdFragment;
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

    private void setBrightness(int brightness){
        this.brightness = brightness;

        bright_bar.setProgress(brightness);
        firstSegment.setAlpha(1-(100-brightness)/100.0f);
        secondSegment.setAlpha(1-(100-brightness)/100.0f);
        colonSegment.setAlpha(1-(100-brightness)/100.0f);
        thirdSegment.setAlpha(1-(100-brightness)/100.0f);
        forthSegment.setAlpha(1-(100-brightness)/100.0f);
    }

    public void setTime(int hour, int minute, boolean isHour24){
        int prevHour = currTime/100;
        int prevMinute = currTime%100;

        timeOffset += (hour-prevHour)*60*60;
        timeOffset += (minute-prevMinute)*60;
        currTime = hour*100+minute;

        if(!isHour24){
            if(hour>12)hour -= 12;
            else if(hour==0)hour = 12;
        }

        final int modifyHour = hour;
        final int modifyMinute = minute;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int[] numbers = {R.drawable.seg_0, R.drawable.seg_1, R.drawable.seg_2, R.drawable.seg_3, R.drawable.seg_4, R.drawable.seg_5 ,R.drawable.seg_6,R.drawable.seg_7, R.drawable.seg_8, R.drawable.seg_9};

                if(modifyHour/10 > 0) firstSegment.setImageResource(numbers[modifyHour/10]);
                else firstSegment.setImageResource(0);
                secondSegment.setImageResource(numbers[modifyHour%10]);
                thirdSegment.setImageResource(numbers[modifyMinute/10]);
                forthSegment.setImageResource(numbers[modifyMinute%10]);
            }
        });
        if(isSynced){
            sendData("set_time", String.valueOf(timeOffset));
        }
    }

    String getWiFiSSID(Context context){
        WifiManager manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = manager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();
        return ssid;
    }

    private boolean checkWifi(String ssid){
        String current_ssid=getWiFiSSID(getApplication());
        Log.d(TAG, current_ssid);
        if(!current_ssid.contains(ssid)){    //if not connected to Device
            return false;
        }else{    //if connected to Device
            return true;
        }
    }

    void connect(String ssid, String password){
        WifiConfiguration wificonfig=new WifiConfiguration();
        wificonfig.SSID = String.format("\"%s\"", ssid);
        wificonfig.preSharedKey = String.format("\"%s\"", password);

        WifiManager wifiManager=(WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        int netId = wifiManager.addNetwork(wificonfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }

    /** 웹 서버로 데이터 전송 */
    public void sendData(final String parameter, String data) {
        final String param = parameter;
        final String d = data;
        lastParam = parameter;
        // 네트워크 통신하는 작업은 무조건 작업스레드를 생성해서 호출 해줄 것!!
        new Thread() {
            public void run() {
                /** 웹 서버로 요청을 한다. */
                RequestBody body = new FormBody.Builder()
                        .add(param, d)
                        .build();
                Request request = new Request.Builder()
                        .url("http://192.168.4.1/post")
                        .post(body)
                        .build();
                if(parameter.equals("sync_request") || parameter.equals("time_request")){
                    client.newCall(request).enqueue(callbackRequest);
                }
                else{
                    client.newCall(request).enqueue(callback);
                }
            }
        }.start();
    }

    private final Callback callbackRequest = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d(TAG, "콜백오류:"+e.getMessage());
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final String body = response.body().string();
            Log.d(TAG, "서버에서 응답한 Body:"+body);

            Handler mHandler = new Handler(Looper.getMainLooper());
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), body, Toast.LENGTH_SHORT).show();
                }
            }, 0);

            if(lastParam.equals("sync_request")){
                parseSyncRequest(body);
            }
            else if(lastParam.equals("time_request")){
                timeRequest(body);
            }
        }
    };

    private final Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d(TAG, "콜백오류:"+e.getMessage());
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final String body = response.body().string();
            Log.d(TAG, "서버에서 응답한 Body:"+body);

            Handler mHandler = new Handler(Looper.getMainLooper());
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), body, Toast.LENGTH_SHORT).show();
                }
            }, 0);
        }
    };

    private void parseSyncRequest(String body){
        String[] datas = body.split(",");       //rawTime, timeOffset, hour12, useColon, brightMode, brightness
        int rawTime = Integer.parseInt(datas[0]);
        timeOffset = Integer.parseInt(datas[1]);
        int hour12 = Integer.parseInt(datas[2]);
        int showAMPM = Integer.parseInt(datas[3]);
        int brightMode = Integer.parseInt(datas[4]);
        int brightness = Integer.parseInt(datas[5]);

        currTime = rawTime;

        firstFragment.setHourMode(hour12!=1);
        firstFragment.setHour(rawTime/100);
        firstFragment.setMinute(rawTime%100);
/*
        setTime(rawTime/100, rawTime%100, firstFragment.getHourMode());
        setBrightness(brightness);

        secondFragment.setMode(brightMode);
        thirdFragment.setColonMode(showAMPM==1);
*/
        //isSynced = true;

        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isSynced = true;
            }
        }, 2000);

    }

    private void timeRequest(String body){
        int rawTime = Integer.parseInt(body);
        setTime(rawTime/100, rawTime%100, firstFragment.getHourMode());

        firstFragment.setHour(rawTime/100);
        firstFragment.setMinute(rawTime%100);
    }

    private void startTimerTask(){
        /*if(timerTask==null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    sendData("time_request", "");
                }
            };
            timer.schedule(timerTask, 2000, 2000);
        }*/
    }

    public boolean checkSync(){
        return isSynced;
    }
}
