package com.example.smartcards;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class ViewFolders extends AppCompatActivity implements View.OnClickListener, RecyclerViewInterface {
    ArrayList<Folder> folders;
    DBHelper dbHelper;
    private ActivityResultLauncher<Intent> someActivityResultLauncher;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_folders);
        recyclerView = findViewById(R.id.recyclerviewfolders);
        FloatingActionButton fab = findViewById(R.id.fabCreateFolder);
        fab.setOnClickListener(this);
        Button btnImportFolder = findViewById(R.id.btnImportFolder);
        btnImportFolder.setOnClickListener(this);
        dbHelper = new DBHelper(this);
        folders = new ArrayList<>();
        loadFolders();

        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        readCSVFile(getFilePathFromUri(uri));
                    }
                });
    }

    private void loadFolders() {
        Cursor res = dbHelper.getFolders();

        FoldersRecyclerViewAdapter adapter = new FoldersRecyclerViewAdapter(this, folders, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (res.getCount() != 0) {
            while (res.moveToNext()) {
                Folder f = new Folder(res.getInt(0), res.getString(1));
                folders.add(f);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.fabCreateFolder:
                Intent intent1 = new Intent(this, AddFolder.class);
                startActivity(intent1);
                break;
            case R.id.btnImportFolder:
                readCSV(view);
                break;
        }
    }

    public void readCSV(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Set MIME type to "text/csv"
        someActivityResultLauncher.launch(Intent.createChooser(intent, "Select CSV File"));
    }

    private String getFilePathFromUri(Uri uri) {
        String filePath = null;
        if (uri != null) {
            if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
                filePath = uri.getPath();
            } else if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
                try {
                    ContentResolver contentResolver = getContentResolver();
                    String displayName = getDisplayNameFromUri(uri);
                    if (displayName != null) {
                        Cursor cursor = contentResolver.query(uri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            int columnIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                            if (columnIndex != -1) {
                                long size = cursor.getLong(columnIndex);
                                if (size >= 0) {
                                    InputStream inputStream = contentResolver.openInputStream(uri);
                                    if (inputStream != null) {
                                        File tempFile = createTempFile(displayName);
                                        filePath = tempFile.getAbsolutePath();
                                        FileOutputStream outputStream = new FileOutputStream(tempFile);
                                        byte[] buffer = new byte[4096];
                                        int bytesRead;
                                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                                            outputStream.write(buffer, 0, bytesRead);
                                        }
                                        outputStream.close();
                                        inputStream.close();
                                    }
                                }
                            }
                        }
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return filePath;
    }

    private String getDisplayNameFromUri(Uri uri) {
        String displayName = null;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (columnIndex != -1) {
                displayName = cursor.getString(columnIndex);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return displayName;
    }

    private File createTempFile(String fileName) throws IOException {
        File cacheDir = getCacheDir();
        String tempFileName = "temp_" + System.currentTimeMillis() + "_" + fileName;
        return new File(cacheDir, tempFileName);
    }

    public void readCSVFile(String path) {
        String fileData = null;
        String folder_name = "";
        int folder_id = 0;

        ArrayList<Card> cards = new ArrayList<>();
        File file = new File(path);
        try {
            int count = 0;
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] split = line.split(",");

                if (count == 0) {
                    folder_name = split[0];
                    folder_id = dbHelper.insertFolderReturnID(folder_name);
                } else {
                        dbHelper.insertCards(split[0], split[1], folder_id);
                }

                count++;
            }

            Toast.makeText(this, "Imported Folder: " + folder_name, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Added " + (count-1) + " cards", Toast.LENGTH_SHORT).show();
            updateRecyclerView();
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    private void updateRecyclerView() {
        folders.clear(); // Clear the existing data
        loadFolders(); // Load the updated data
        recyclerView.getAdapter().notifyDataSetChanged(); // Notify the adapter about the changes
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(ViewFolders.this, ViewCards.class);
        intent.putExtra("folder_name", folders.get(position).getName());
        intent.putExtra("folder_id", folders.get(position).getId());
        startActivity(intent);
    }
}