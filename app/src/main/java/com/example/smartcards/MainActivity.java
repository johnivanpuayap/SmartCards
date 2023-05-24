package com.example.smartcards;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
public class MainActivity extends AppCompatActivity implements View.OnClickListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnPomodoro = findViewById(R.id.btnPomodoro);
        Button btnViewFolders = findViewById(R.id.btnView);
        Button btnReview = findViewById(R.id.btnReview);

        btnViewFolders.setOnClickListener(this);
        btnReview.setOnClickListener(this);
        btnPomodoro.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnPomodoro:
                Intent intent = new Intent(this, Pomodoro.class);
                startActivity(intent);
                break;
            case R.id.btnView:
                Intent intent1 = new Intent(this, ViewFolders.class);
                startActivity(intent1);
                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}