package com.safe.jessica.canceleventdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private String TAG = "Mine_Activity";
    private MyGroup slideView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.my_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "click me!!!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, ListActivity.class));
            }
        });
        findViewById(R.id.view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "delete success!!!", Toast.LENGTH_SHORT).show();
            }
        });

        slideView = findViewById(R.id.slideView);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d(TAG, "dispatchTouchEvent start: " + ev.getAction());
        boolean b = super.dispatchTouchEvent(ev);
        Log.d(TAG, "dispatchTouchEvent end: " + ev.getAction() + "," + b);
        return b;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
//        if (action == MotionEvent.ACTION_DOWN && slideView.isOpen) {
//            slideView.smoothScrollToFinal();
//        }
        boolean b = super.onTouchEvent(event);
        Log.d(TAG, "onTouchEvent: " + event.getAction() + "," + b);
        return b;
    }

}
