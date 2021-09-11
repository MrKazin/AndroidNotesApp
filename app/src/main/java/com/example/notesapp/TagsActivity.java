package com.example.notesapp;

import android.app.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TagsActivity extends AppCompatActivity {

    ListView listView;
    FloatingActionButton floatingActionButton;
    MyDataBaseHelper myDB;
    ArrayList<String> tag_id, tag_value, tag_color;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        activity = TagsActivity.this;
        listView = findViewById(R.id.tagsListView);
        floatingActionButton = findViewById(R.id.addTagButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TagsActivity.this, AddTagActivity.class);
                activity.startActivityForResult(intent,1);
            }
        });

        myDB = new MyDataBaseHelper(TagsActivity.this);
        tag_id = new ArrayList<>();
        tag_value = new ArrayList<>();
        tag_color = new ArrayList<>();

        this.storeTagsInArrays();

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,tag_value);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TagsActivity.this,UpdateTagActivity.class);
                intent.putExtra("id",String.valueOf(tag_id.get(position)));
                intent.putExtra("value",String.valueOf(tag_value.get(position)));
                intent.putExtra("color",Integer.valueOf(tag_color.get(position)));
                activity.startActivityForResult(intent,1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            recreate();
        }
    }

    private void storeTagsInArrays(){
        Cursor cursor = myDB.readAllTags();
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        } else{
            while (cursor.moveToNext()){
                tag_id.add(cursor.getString(0));
                tag_value.add(cursor.getString(1));
                tag_color.add(cursor.getString(2));
            }
        }
    }


}
