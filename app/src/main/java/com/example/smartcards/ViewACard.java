package com.example.smartcards;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ViewACard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_acard);
        TextView txtCardID = findViewById(R.id.txtCardID);
        TextView txtFront = findViewById(R.id.tfFrontViewACard);
        TextView txtBack = findViewById(R.id.tfBackViewACard);
        Button btnUpdate = findViewById(R.id.btnUpdate);
        Button btnDelete = findViewById(R.id.btnDelete);


        int card_id = getIntent().getIntExtra("folder_id", 0);
        String front = getIntent().getStringExtra("front_text");
        String back = getIntent().getStringExtra("back_text");

        txtFront.setText(front);
        txtBack.setText(back);

        btnUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                DBHelper dbHelper = new DBHelper(ViewACard.this);
                String front_text = txtFront.getText().toString();
                String back_text = txtBack.getText().toString();
                if(dbHelper.updateCardsText(card_id, front_text, back_text)) {
                    Toast.makeText(ViewACard.this, "Updated the Card", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                DBHelper dbHelper = new DBHelper(ViewACard.this);

                if(dbHelper.deleteCards(card_id)) {
                    Toast.makeText(ViewACard.this, "Deleted the Card", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ViewACard.this, ViewCards.class);
                    startActivity(intent);
                }

            }
        });
    }
}