package com.example.notesapp;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import yuku.ambilwarna.AmbilWarnaDialog;

public class AddTagActivity extends AppCompatActivity {

    EditText tag_value_input;
    Button add_tag_button, choose_color_button, current_color;
    private int tag_color = Color.GRAY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tag);

        tag_value_input = findViewById(R.id.value_input);
        choose_color_button = findViewById(R.id.choose_color_button);
        choose_color_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });
        current_color = findViewById(R.id.current_color);
        current_color.setBackgroundColor(tag_color);

        add_tag_button = findViewById(R.id.add_tag_button);
        add_tag_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDataBaseHelper myDB = new MyDataBaseHelper(AddTagActivity.this);
                myDB.addTag(tag_value_input.getText().toString().trim(), tag_color);
            }
        });
    }

    private void openColorPicker(){
        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, tag_color, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                Toast.makeText(AddTagActivity.this, "Unavailable", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                tag_color = color;
                current_color.setBackgroundColor(tag_color);
            }
        });

        ambilWarnaDialog.show();
    }
}
