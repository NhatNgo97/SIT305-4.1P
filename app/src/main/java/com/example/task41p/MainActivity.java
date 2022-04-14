package com.example.task41p;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    ImageButton playButton, pauseButton, stopButton;
    EditText taskEditText;
    TextView timerTextView, latestTaskTimerTextView;
    Timer timer;
    TimerTask timerTask;
    public String sharedPreferenceName = "sharedPreference";
    public String taskKeyPreference = "task";
    public String timeKeyPreference = "time";
    public String task="";
    public String prevTask;
    public double prevTime;


    double time = 0.0;

    boolean isRunning;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isRunning = false;
        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);
        stopButton = findViewById(R.id.stopButton);

        taskEditText = findViewById(R.id.taskEditText);
        timer = new Timer();
        timerTextView = findViewById(R.id.timerText);
        latestTaskTimerTextView = findViewById(R.id.latestTaskTimerTextView);
        getTask();

        String prevTimeAsText = parseTime(prevTime);
        latestTaskTimerTextView.setText("You spent "+prevTimeAsText+" on "+prevTask+" last time.");

        //handle rotation
        if (savedInstanceState != null) {
            isRunning = savedInstanceState.getBoolean("isRunning");
            double getTime = savedInstanceState.getDouble("time");
            time = getTime;
            timerTextView.setText(parseTime(time));
            if (isRunning == true) {
                handlerStartTimer();
            }
        }

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View screen) {
                if(!isRunning)
                {
                    handlerStartTimer();
                }

                isRunning=true;
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View screen) {
                handlePauseTimer();
                isRunning=false;
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View screen) {
                handleStopTimer();
                isRunning=false;
            }
        });
    }

    public void handlerStartTimer(){
        timerTask = new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        time++;
                        timerTextView.setText(parseTime(time));
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 1000 ,1000);
    }
    public void handlePauseTimer(){
        if(isRunning)
        {
            timerTask.cancel();
        }
    }

    public void handleStopTimer(){
        if(isRunning)
        {
            timerTask.cancel();
        }

        String prevTimeAsText = parseTime(time);
        task = taskEditText.getText().toString();
        latestTaskTimerTextView.setText("You spent "+prevTimeAsText+" on "+task+" last time.");

        setTask();
    }

    public void setTask(){
        SharedPreferences sharedPreferences = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(taskKeyPreference, task);
        editor.putLong(timeKeyPreference, (Long)Double.doubleToRawLongBits(time));
        editor.apply();
        time = 0.0;
        timerTextView.setText("00:00.00");

    }

    public void getTask(){
        SharedPreferences sharedPreferences = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);
        prevTask = sharedPreferences.getString(taskKeyPreference,"this task").toString();
        prevTime = Double.longBitsToDouble(sharedPreferences.getLong(timeKeyPreference, 0));
    }

    public String parseTime(double timeIn){
        String output;

        int hrs = (int)(timeIn/60)/60;
        int min = (int)(timeIn/60);
        int sec = (int)timeIn;
        String col = ":";
        String dot = ".";
        String zer = "0";
        if(sec<10){
            dot = ":0";
        }
        if(min<10)
        {
            col = ":0";
        }
        if(hrs>0)
        {
            zer="";
        }
        output = (zer+hrs+col+min%60+dot+sec%60);
        return output;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble("time", time);
        outState.putBoolean("isRunning", isRunning);
    }
}