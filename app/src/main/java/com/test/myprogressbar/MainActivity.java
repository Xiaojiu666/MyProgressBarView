package com.test.myprogressbar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private MyProgressBar mpb;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.e("Message", msg.arg1 + "");
            mpb.setFileCur(msg.arg1);
            mpb.invalidate();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mpb = (MyProgressBar) findViewById(R.id.mpb);
        mpb.setFileMax(100);


        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 100; i++) {
                    try {
                        Thread.sleep(300);
                        // mpb.setFileCur(i);
                        Log.e("Time", i + "");
                        Message msg = new Message();
                        msg.arg1 = i;
                        handler.sendMessage(msg);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


      /*  for (int i = 0; i <= 100; i++) {
            try {
                Thread.sleep(3000);
                mpb.setFileCur(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
    }
}
