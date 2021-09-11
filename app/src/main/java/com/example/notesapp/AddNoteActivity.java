package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class AddNoteActivity extends AppCompatActivity {

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    EditText header_input, text_input;
    ListView listView;
    Button add_button;
    Bitmap selectedImage;
    ImageView add_image_view;
    MyDataBaseHelper myDB;
    ArrayList<String> tag_id, tag_value, chosen_id;
    String check_value = "false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        header_input = findViewById(R.id.header_input);
        text_input = findViewById(R.id.text_input);
        add_image_view = findViewById(R.id.add_image_view);
        myDB = new MyDataBaseHelper(AddNoteActivity.this);
        tag_id = new ArrayList<>();
        tag_value = new ArrayList<>();

        storeTagsInArrays();

        listView = (ListView) findViewById(R.id.list_of_tags);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice,tag_value);
        listView.setAdapter(adapter);
        chosen_id = new ArrayList<>();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(chosen_id.contains(tag_id.get(position))){
                    chosen_id.remove(tag_id.get(position));
                } else{
                    chosen_id.add(tag_id.get(position));
                }
            }
        });

        add_button = findViewById(R.id.add_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDataBaseHelper myDB = new MyDataBaseHelper(AddNoteActivity.this);
                if(checkForImage(check_value)){
                    try {
                        byte[] byteArray = getBytes(selectedImage);
                        myDB.addNote(header_input.getText().toString().trim(), text_input.getText().toString().trim(), byteArray);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else{
                    myDB.addNote(header_input.getText().toString().trim(), text_input.getText().toString().trim());
                }
                addTagsForNote();
                finish();
            }
        });
    }

    void storeTagsInArrays(){
        Cursor cursor = myDB.readAllTags();
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        } else{
            while (cursor.moveToNext()){
                tag_id.add(cursor.getString(0));
                tag_value.add(cursor.getString(1));
            }
        }
    }

    void addTagsForNote(){
        Cursor cursor = myDB.getNote(header_input.getText().toString().trim(), text_input.getText().toString().trim());
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        } else{
            cursor.moveToFirst();
            String note_id = cursor.getString(0);
            if(chosen_id.size() > 0){
                for(String tag_id : chosen_id)
                    myDB.addTagToNote(note_id, tag_id);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_image){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    //без доступа. запрос
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions, PERMISSION_CODE);
                } else{
                    //уже есть доступ
                    pickImageFromGallery();
                }
            } else{
                //версия старше
                pickImageFromGallery();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void pickImageFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //доступ выдан
                    pickImageFromGallery();
                } else {
                    //отказал
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE && data != null) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                add_image_view.setImageBitmap(selectedImage);
                check_value = "true";
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        } else{
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_SHORT).show();

        }
    }

    private Boolean checkForImage(String check_value){
        if(check_value.equals("true")){
            return true;
        } else{
            return false;
        }
    }

    private byte[] getBytes(Bitmap bitmap) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        stream.close();
        return stream.toByteArray();
    }
}
