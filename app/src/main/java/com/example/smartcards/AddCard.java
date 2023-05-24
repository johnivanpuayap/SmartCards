package com.example.smartcards;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddCard extends AppCompatActivity {
    DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DBHelper(this);
        setContentView(R.layout.activity_add_cards);
        EditText tfFront = findViewById(R.id.tfFront);
        EditText tfBack =  findViewById(R.id.tfBack);
        Button btnAdd = findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String front = tfFront.getText().toString();
                String back = tfBack.getText().toString();
                int folder_id = getIntent().getIntExtra("folder_id", 0);

                if(dbHelper.insertCards(front, back, folder_id)) {
                    Toast.makeText(AddCard.this, "Added Cards to the Database", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(AddCard.this, ViewCards.class);
                    intent1.putExtra("folder_name", getIntent().getStringExtra("folder_name"));
                    intent1.putExtra("folder_id", folder_id);
                    startActivity(intent1);
                } else {
                    Toast.makeText(AddCard.this, "Failed on Adding Cards to the Database!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}