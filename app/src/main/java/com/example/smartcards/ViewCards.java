package com.example.smartcards;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class ViewCards extends AppCompatActivity implements View.OnClickListener, RecyclerViewInterface {
    ArrayList<Card> cards;
    DBHelper dbHelper;
    int folder_id;
    String folder_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        FloatingActionButton fab = findViewById(R.id.fabCreateCard);
        fab.setOnClickListener(this);
        Button btnReviewCard = findViewById(R.id.btnReviewCards);
        btnReviewCard.setOnClickListener(this);
        Button btnRandom = findViewById(R.id.btnShuffleReview);
        btnRandom.setOnClickListener(this);
        Button btnExportFolder = findViewById(R.id.btnExportFolder);
        btnExportFolder.setOnClickListener(this);

        dbHelper = new DBHelper(this);
        cards = new ArrayList<>();

        folder_id =  getIntent().getIntExtra("folder_id", 0);
        folder_name = getIntent().getStringExtra("folder_name");
        TextView textViewFolderName = (TextView) findViewById(R.id.txtFolderName);

        textViewFolderName.setText(folder_name.toUpperCase());
        textViewFolderName.setGravity(Gravity.CENTER_HORIZONTAL);
        loadCards(folder_id);
    }

    private void loadCards(int folder_id) {
        Cursor res = dbHelper.getCards(folder_id);

        CardsRecyclerViewAdapter adapter = new CardsRecyclerViewAdapter(this, this, cards);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewCards);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (res.getCount() != 0) {
            while (res.moveToNext()) {
                Card c = new Card(res.getInt(0), res.getString(1), res.getString(2), res.getInt(3));
                cards.add(c);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.fabCreateCard:
                Intent intent1 = new Intent(this, AddCard.class);
                intent1.putExtra("folder_id", folder_id);
                intent1.putExtra("folder_name", folder_name);
                startActivity(intent1);
                break;
            case R.id.btnReviewCards:
                Intent intent2 = new Intent(this, ReviewCards.class);
                intent2.putExtra("folder_id", folder_id);
                intent2.putExtra("folder_name", folder_name);
                startActivity(intent2);
                break;
            case R.id.btnExportFolder:
                if(cards.size() != 0 ) {
                    export(view);
                } else {
                    Toast.makeText(this, "No Cards! Can't Export to a File", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnShuffleReview:
                Intent intent3 = new Intent(this, ReviewCardsShuffled.class);
                intent3.putExtra("folder_id", folder_id);
                intent3.putExtra("folder_name", folder_name);
                startActivity(intent3);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    public void export(View view) {
        //generate data
        StringBuilder data = new StringBuilder();
        data.append(folder_name  + ", folder_name");

        for(Card c: cards) {
            data.append("\n" + c.getFront() + ", " + c.getBack());
        }

        try {
            Context context = getApplicationContext();
            File appSpecificDir = getExternalFilesDir(null);

            // Create the file with the desired filename
            File file = new File(appSpecificDir, "data.csv");

            FileOutputStream out = new FileOutputStream(file);
            out.write(data.toString().getBytes());
            out.close();

            Uri path = FileProvider.getUriForFile(context, "com.example.smartcards.fileprovider", file);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);

            startActivity(fileIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(ViewCards.this, ViewACard.class);
        intent.putExtra("card_id", cards.get(position).getId());
        intent.putExtra("front_text", cards.get(position).getFront());
        intent.putExtra("back_text", cards.get(position).getBack());
        intent.putExtra("rating", cards.get(position).getRating());
        startActivity(intent);
    }
}