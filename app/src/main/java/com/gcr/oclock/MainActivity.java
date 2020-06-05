package com.gcr.oclock;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = new OkHttpClient();

        Button connectToClock = (Button) findViewById(R.id.connect_clock);
        connectToClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkWifi("L-Clock")){

                }
                else{
                    Toast.makeText(getApplicationContext(), "L-Clock에 연결하여 주십시오.", Toast.LENGTH_SHORT).show();
                }
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

        Button brightness = (Button) findViewById(R.id.btn_brightness);
        brightness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText edittext = new EditText(MainActivity.this);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("AlertDialog Title");
                builder.setMessage("AlertDialog Content");
                builder.setView(edittext);
                builder.setPositiveButton("입력",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),edittext.getText().toString() ,Toast.LENGTH_LONG).show();
                                sendData("post", edittext.getText().toString());
                            }
                        });
                builder.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();
            }
        });

        sendData("first_request", "");
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
    private void sendData(String parameter, String data) {
        final String param = parameter;
        final String d = data;
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
                client.newCall(request).enqueue(callback);
            }
        }.start();
    }

    private final Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d(TAG, "콜백오류:"+e.getMessage());
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String body = response.body().string();
            Log.d(TAG, "서버에서 응답한 Body:"+body);
        }
    };

}
