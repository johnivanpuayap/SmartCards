package com.example.smartcards;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ReviewCards extends AppCompatActivity implements View.OnClickListener {
    ArrayList<Card> cards;
    DBHelper dbHelper;
    int folder_id;
    String folder_name;
    int count;
    int total;
    boolean onFront;
    TextView textViewFrontBack;
    TextView textViewNumbers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_cards);
        textViewFrontBack = (TextView) findViewById(R.id.txtFrontBack);
        textViewNumbers = (TextView) findViewById(R.id.txtNumber);
        TextView folder = (TextView) findViewById(R.id.txtFolderNameReview);
        Button btnCheck = findViewById(R.id.btnCheck);
        Button btnWrong = findViewById(R.id.btnWrong);
        dbHelper = new DBHelper(this);
        cards = new ArrayList<>();
        count = 0;
        onFront = true;

        folder_id =  getIntent().getIntExtra("folder_id", 0);
        folder_name = getIntent().getStringExtra("folder_name");
        folder.setText(folder_name.toUpperCase());
        loadCards(folder_id);
        total = cards.size();

        if(total != 0 && count < total) {
            String newText = "Front\n\n\n";
            newText += cards.get(count).getFront();
            textViewFrontBack.setText(newText);
            textViewNumbers.setText((count+1) + " / " + total);
        } else{
            textViewFrontBack.setText("\nNo Cards");
            textViewFrontBack.setTextSize(50);
            textViewFrontBack.setGravity(Gravity.CENTER_HORIZONTAL);
        }

        textViewFrontBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count < total) {
                    if(onFront) {
                        String newText = "Back\n\n\n";
                        newText += cards.get(count).getBack();
                        textViewFrontBack.setText(newText);
                        onFront = false;
                    } else {
                        String newText = "Front\n\n\n";
                        newText += cards.get(count).getFront();
                        textViewFrontBack.setText(newText);
                        onFront = true;
                    }
                }

            }
        });

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count+1 > total) {
                    count++;
                    textViewFrontBack.setText("\nNo More Cards");
                    textViewFrontBack.setTextSize(50);
                    textViewFrontBack.setGravity(Gravity.CENTER_HORIZONTAL);
                } else if(count+1 == total) {
                    int old_rating = cards.get(count).getRating();
                    int new_rating = old_rating + 1;

                    if(dbHelper.updateRating(cards.get(count).getId(), new_rating)) {
                        Toast.makeText(ReviewCards.this, "Rating changed from " + old_rating + " to " + new_rating, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ReviewCards.this, "Rating was not updated", Toast.LENGTH_SHORT).show();
                    }
                    count++;
                    textViewFrontBack.setText("\nNo More Cards");
                    textViewFrontBack.setTextSize(50);
                    textViewFrontBack.setGravity(Gravity.CENTER_HORIZONTAL);
                } else {
                    int old_rating = cards.get(count).getRating();
                    int new_rating = old_rating + 1;

                    if(dbHelper.updateRating(cards.get(count).getId(), new_rating)) {
                        Toast.makeText(ReviewCards.this, "Rating changed from " + old_rating + " to " + new_rating, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ReviewCards.this, "Rating was not updated", Toast.LENGTH_SHORT).show();
                    }

                    count++;

                    String newText = "Front\n\n\n";
                    newText += cards.get(count).getFront();
                    textViewFrontBack.setText(newText);
                    textViewNumbers.setText((count+1) + " / " + total);
                }

            }
        });

        btnWrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(count+1 > total) {
                    textViewFrontBack.setText("\nNo More Cards");
                    textViewFrontBack.setTextSize(50);
                    textViewFrontBack.setGravity(Gravity.CENTER_HORIZONTAL);
                } else if(count+1 == total) {
                    int old_rating = cards.get(count).getRating();
                    int new_rating = old_rating + 1;

                    if(dbHelper.updateRating(cards.get(count).getId(), new_rating)) {
                        Toast.makeText(ReviewCards.this, "Rating changed from " + old_rating + " to " + new_rating, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ReviewCards.this, "Rating was not updated", Toast.LENGTH_SHORT).show();
                    }
                    count++;
                    textViewFrontBack.setText("\nNo More Cards");
                    textViewFrontBack.setTextSize(50);
                    textViewFrontBack.setGravity(Gravity.CENTER_HORIZONTAL);
                } else{
                    int old_rating = cards.get(count).getRating();
                    int new_rating = old_rating - 1;

                    if(dbHelper.updateRating(cards.get(count).getId(), new_rating)) {
                        Toast.makeText(ReviewCards.this, "Rating changed from " + old_rating + " to " + new_rating, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ReviewCards.this, "Rating was not updated", Toast.LENGTH_SHORT).show();
                    }

                    count++;


                    String newText = "Front\n\n\n";
                    newText += cards.get(count).getFront();
                    textViewFrontBack.setText(newText);
                    textViewNumbers.setText((count+1) + " / " + total);
                }
            }
        });
    }

    private void loadCards(int folder_id) {
        Cursor res = dbHelper.reviewCards(folder_id);

        if (res.getCount() != 0) {
            while (res.moveToNext()) {
                Card c = new Card(res.getInt(0), res.getString(1), res.getString(2), res.getInt(3));
                cards.add(c);
            }
        }

    }

    @Override
    public void onClick(View view) {

    }
}