package com.example.smartcards;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Pomodoro extends AppCompatActivity {

    private int duration;
    private int count;
    private boolean timerRunning;
    private CountDownTimer timer;
    private TextView minute;
    private TextView second;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);
        Button btnShortBreak = findViewById(R.id.btnShortBeak);
        btnShortBreak.setEnabled(false);
        Button btnLongBreak = findViewById(R.id.btnLongBreak);
        btnLongBreak.setEnabled(false);

        Button btnStart = findViewById(R.id.btnStart);
        Button btnSkip = findViewById(R.id.btnSkip);

        minute = findViewById(R.id.minute);
        second = findViewById(R.id.second);

        duration = 1500;
        count = 1;
        timerRunning = false;

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!timerRunning) {
                    startTimer();
                    timerRunning = true;
                } else {
                    Toast.makeText(Pomodoro.this, "Timer is already running", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timerRunning) {
                    timer.cancel();
                    timerRunning = false;
                    startNextPhase();
                }
            }
        });
    }

    private void startTimer() {
        timer = new CountDownTimer(duration * 1000, 1000) {
            @Override
            public void onTick(long l) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        long minutes = TimeUnit.MILLISECONDS.toMinutes(l);
                        long seconds = TimeUnit.MILLISECONDS.toSeconds(l) % 60;
                        String time = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                        minute.setText(String.valueOf(minutes));
                        if(seconds == 0) {
                            second.setText("00");
                        } else {
                            second.setText(String.valueOf(seconds));
                        }

                    }
                });
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                startNextPhase();
            }
        }.start();
    }

    private void startNextPhase() {
        Button btnFocus = findViewById(R.id.btnFocus);
        Button btnShortBreak = findViewById(R.id.btnShortBeak);
        Button btnLongBreak = findViewById(R.id.btnLongBreak);

        if (btnFocus.isEnabled()) {
            btnFocus.setEnabled(false);
            if (count % 4 != 0) {
                btnShortBreak.setEnabled(true);
                minute.setText("5");
                second.setText("00");
                duration = 300;
            } else {
                btnLongBreak.setEnabled(true);
                minute.setText("15");
                second.setText("00");
                duration = 900;
            }
            count++;
        } else {
            btnFocus.setEnabled(true);
            minute.setText("25");
            second.setText("00");
            duration = 1500;
            btnLongBreak.setEnabled(false);
            btnShortBreak.setEnabled(false);
        }

        if (timerRunning) {
            startTimer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}