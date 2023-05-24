package com.example.smartcards;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddFolder extends AppCompatActivity {

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_folder);
        dbHelper = new DBHelper(this);
        EditText tfName = findViewById(R.id.tfName);
        Button btnAdd = findViewById(R.id.btnAdd2);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = tfName.getText().toString();
                if(dbHelper.insertFolder(name)) {
                    Toast.makeText(AddFolder.this, "Added Folder to the Database", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(AddFolder.this, ViewFolders.class);
                    startActivity(intent1);
                } else {
                    Toast.makeText(AddFolder.this, "Failed on Adding Folder to the Database!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}