package com.example.notesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    MyDataBaseHelper myDB;
    ArrayList<String> note_id, note_header, note_text, note_date;
    ArrayList<byte[]> note_image;
    CustomAdapter customAdapter;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = MainActivity.this;

        recyclerView = findViewById(R.id.notesRecyclerView);

        floatingActionButton = findViewById(R.id.addNoteButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                activity.startActivityForResult(intent,1);
            }
        });

        myDB = new MyDataBaseHelper(MainActivity.this);
        note_id = new ArrayList<>();
        note_header = new ArrayList<>();
        note_text = new ArrayList<>();
        note_date = new ArrayList<>();
        note_image = new ArrayList<byte[]>();

        storeNotesInArrays();

        customAdapter = new CustomAdapter(MainActivity.this,this, note_id, note_header, note_text, note_date, note_image);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            recreate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.tag_menu){
            Intent intent = new Intent(MainActivity.this, TagsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    void storeNotesInArrays(){
        Cursor cursor = myDB.readAllNotes();
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        } else{
            while (cursor.moveToNext()){
                note_id.add(cursor.getString(0));
                note_header.add(cursor.getString(1));
                note_text.add(cursor.getString(2));
                note_date.add(cursor.getString(3));
                note_image.add(cursor.getBlob(4));
            }
        }
    }
}