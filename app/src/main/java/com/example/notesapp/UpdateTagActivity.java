package com.example.notesapp;

import android.app.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import yuku.ambilwarna.AmbilWarnaDialog;

public class UpdateTagActivity extends AppCompatActivity {

    EditText tag_value_input;
    Button update_tag_button, delete_tag_button, change_color_button, current_color;
    String tag_id, tag_value;
    Integer tag_color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_tag);

        tag_value_input = findViewById(R.id.value_input2);
        change_color_button = findViewById(R.id.change_color_button);
        change_color_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });
        current_color = findViewById(R.id.current_color2);

        this.getAndSetIntentData();

        update_tag_button = findViewById(R.id.update_tag_button);
        update_tag_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tag_value = tag_value_input.getText().toString();
                MyDataBaseHelper myDb = new MyDataBaseHelper(UpdateTagActivity.this);
                myDb.updateTag(tag_id, tag_value, tag_color);
                finish();
            }
        });

        delete_tag_button = findViewById(R.id.delete_tag_button);
        delete_tag_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog();
            }
        });
    }

    private void openColorPicker(){
        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, tag_color, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                Toast.makeText(UpdateTagActivity.this, "Unavailable", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                tag_color = color;
                current_color.setBackgroundColor(tag_color);
            }
        });

        ambilWarnaDialog.show();
    }

    private void getAndSetIntentData(){
        if(getIntent().hasExtra("id") && getIntent().hasExtra("value")){
            tag_id = getIntent().getStringExtra("id");
            tag_value = getIntent().getStringExtra("value");
            tag_color = getIntent().getIntExtra("color", Color.GRAY);
            tag_value_input.setText(tag_value);
            current_color.setBackgroundColor(tag_color);
        } else{
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        }
    }

    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete " + tag_value + " tag?");
        builder.setMessage("Are you sure you want to delete " + tag_value + " tag from database?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyDataBaseHelper myDB = new MyDataBaseHelper(UpdateTagActivity.this);
                myDB.deleteTagFromAllNotes(tag_id);
                myDB.deleteTag(tag_id);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });
        builder.create().show();
    }
}
