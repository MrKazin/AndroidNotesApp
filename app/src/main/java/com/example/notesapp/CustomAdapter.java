package com.example.notesapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder>{

    private Context context;
    private Activity activity;
    private ArrayList note_id, note_header, note_text, note_date;
    private List<String> tags_id, tags_value;
    private List<Integer> tags_color;
    private List<byte[]> note_image;

    public CustomAdapter(Activity activity, Context context, ArrayList note_id,
                         ArrayList note_header, ArrayList note_text,
                         ArrayList note_date, ArrayList note_image) {
        this.activity = activity;
        this.context = context;
        this.note_id = note_id;
        this.note_header = note_header;
        this.note_text = note_text;
        this.note_date = note_date;
        this.note_image = note_image;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.note_header_txt.setText(String.valueOf(note_header.get(position)));
        holder.note_text_txt.setText(String.valueOf(note_text.get(position)));
        holder.note_date_txt.setText(String.valueOf(note_date.get(position)));
        giveTagsForNote(String.valueOf(note_header.get(position)), String.valueOf(note_text.get(position)));
        for (int i = 0; i < tags_id.size(); i++) {
            TextView textView = new TextView(context);
            textView.setText(tags_value.get(i));
            textView.setTextColor(tags_color.get(i));
            holder.tagsLayout.addView(textView);
        }
        if(note_image.get(position) != null){
            Bitmap imageBitMap = getImage(note_image.get(position));
            ImageView imageView = new ImageView(context);
            imageView.setImageBitmap(imageBitMap);
            holder.imageLayout.addView(imageView);
            imageView.getLayoutParams().height = 450;
            imageView.getLayoutParams().width = 900;
        }
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateNoteActivity.class);
                intent.putExtra("id", String.valueOf(note_id.get(position)));
                intent.putExtra("header", String.valueOf(note_header.get(position)));
                intent.putExtra("text", String.valueOf(note_text.get(position)));
                activity.startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return note_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView note_header_txt, note_text_txt, note_date_txt;
        LinearLayout mainLayout, imageLayout;
        GridLayout tagsLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            note_header_txt = itemView.findViewById(R.id.note_header_txt);
            note_text_txt = itemView.findViewById(R.id.note_text_txt);
            note_date_txt = itemView.findViewById(R.id.note_date_txt);
            tagsLayout = itemView.findViewById(R.id.tagsLayout);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            imageLayout = itemView.findViewById(R.id.imageLayout);

        }
    }

    void giveTagsForNote(String note_header, String note_text) {
        tags_id = new ArrayList<>();
        tags_value = new ArrayList<>();
        tags_color = new ArrayList<>();
        MyDataBaseHelper myDB = new MyDataBaseHelper(context);
        Cursor cursor = myDB.getNote(note_header, note_text);
        if (cursor.getCount() == 0) {
            Toast.makeText(context, "Can't Find Note", Toast.LENGTH_SHORT).show();
        } else {
            cursor.moveToFirst();
            String row_id = cursor.getString(0);
            cursor = myDB.getTagsIdForNote(row_id);
            if(cursor.getCount() == 0){
                Toast.makeText(context, "Can't Find Tags For Note", Toast.LENGTH_SHORT).show();
            } else{
                while (cursor.moveToNext()){
                    tags_id.add(cursor.getString(2));
                }
                for(String item : tags_id){
                    cursor = myDB.getTag(item);
                    cursor.moveToFirst();
                    tags_value.add(cursor.getString(1));
                    tags_color.add(cursor.getInt(2));
                }
            }
        }
    }

    private Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
