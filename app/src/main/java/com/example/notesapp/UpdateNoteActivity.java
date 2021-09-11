package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import java.util.List;

public class UpdateNoteActivity extends AppCompatActivity{

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    EditText header_input, text_input;
    ListView listView;
    ImageView update_image_view;
    Button update_button, delete_button;
    String id, header, text;
    Bitmap imageBitMap;
    byte[] image;
    List<String> tag_id, tag_value, received_tags, total_tags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_note);

        header_input = findViewById(R.id.header_input2);
        text_input = findViewById(R.id.text_input2);
        update_image_view = findViewById(R.id.update_image_view);

        getAndSetIntentData();

        tag_id = new ArrayList<>();
        tag_value = new ArrayList<>();
        received_tags = new ArrayList<>();

        storeTagsInArrays();

        listView = findViewById(R.id.list_of_tags2);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        this.setTagsAndImageForNote(header,text);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice,tag_value);
        listView.setAdapter(adapter);
        for(int i = 0; i < received_tags.size(); i++){
            listView.setItemChecked(tag_id.indexOf(received_tags.get(i)),true);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(total_tags.contains(tag_id.get(position))){
                    total_tags.remove(tag_id.get(position));
                } else{
                    total_tags.add(tag_id.get(position));
                }
            }
        });

        update_button = findViewById(R.id.update_button);
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateTagsInNote();
                header = header_input.getText().toString();
                text = text_input.getText().toString();
                MyDataBaseHelper myDb = new MyDataBaseHelper(UpdateNoteActivity.this);
                if(image != null){
                    myDb.updateNote(id,header,text, image);
                } else{
                    myDb.updateNote(id,header,text);
                }
                finish();
            }
        });

        delete_button = findViewById(R.id.delete_button);
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog();
            }
        });
    }

    void getAndSetIntentData(){
        if(getIntent().hasExtra("id") && getIntent().hasExtra("header") && getIntent().hasExtra("text")){
            id = getIntent().getStringExtra("id");
            header = getIntent().getStringExtra("header");
            text = getIntent().getStringExtra("text");
            header_input.setText(header);
            text_input.setText(text);
        } else{
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        }
    }

    void storeTagsInArrays(){
        MyDataBaseHelper myDB = new MyDataBaseHelper(UpdateNoteActivity.this);
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

    void setTagsAndImageForNote(String note_header, String note_text) {
        MyDataBaseHelper myDB = new MyDataBaseHelper(this);
        Cursor cursor = myDB.getNote(note_header, note_text);
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "Can't Find Note", Toast.LENGTH_SHORT).show();
        } else{
            cursor.moveToFirst();
            String row_id = cursor.getString(0);
            if(cursor.getBlob(4) != null){
                image = cursor.getBlob(4);
                imageBitMap = getImage(image);
                update_image_view.setImageBitmap(Bitmap.createScaledBitmap(imageBitMap, 600,400, false));
            }
            cursor = myDB.getTagsIdForNote(row_id);
            if(cursor.getCount() == 0){
                Toast.makeText(this, "Can't Find Tags For Note", Toast.LENGTH_SHORT).show();
            } else{
                while (cursor.moveToNext()){
                    received_tags.add(cursor.getString(2));
                }
                total_tags = new ArrayList<>(received_tags);
            }
        }
    }

    void UpdateTagsInNote(){
        ArrayList<String> total_delete = new ArrayList<>();
        for(String item : received_tags){
            if(total_tags.contains(item)){
                total_tags.remove(item);
            } else{
                total_delete.add(item);
            }
        }
        MyDataBaseHelper myDB = new MyDataBaseHelper(this);
        if(total_delete.size() > 0){
            for(String delete_item : total_delete){
                myDB.deleteTagInNote(id,delete_item);
            }
        }
        if(total_tags.size() > 0){
            for(String add_item : total_tags){
                myDB.addTagToNote(id,add_item);
            }
        }
    }

    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete " + header + " ?");
        builder.setMessage("Are you sure you want to delete " + header + " from database?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyDataBaseHelper myDB = new MyDataBaseHelper(UpdateNoteActivity.this);
                myDB.deleteAllTagsWithNote(id);
                myDB.deleteNote(id);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.update_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.alarm_menu:
                Intent intent = new Intent(UpdateNoteActivity.this, SetAlarmActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("header",header);
                intent.putExtra("text",text);
                startActivity(intent);
                break;
            case R.id.add_image_menu:
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
                break;
            case R.id.delete_image_menu:
                update_image_view.setImageResource(0);
                image = null;
                break;
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE && data != null) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                imageBitMap = BitmapFactory.decodeStream(imageStream);
                image = getBytes(imageBitMap);
                update_image_view.setImageBitmap(Bitmap.createScaledBitmap(imageBitMap, 600,400, false));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Can't Transform Bitmap To Array Of Bytes", Toast.LENGTH_SHORT).show();
            }
        } else{
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_SHORT).show();

        }
    }

    private Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    private byte[] getBytes(Bitmap bitmap) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        stream.close();
        return stream.toByteArray();
    }
}
