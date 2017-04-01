package com.mohdeva.sendit;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Video_Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        try {

        ListView lv = (ListView) findViewById(R.id.videolist);
        String[] projection = {
                MediaStore.Files.FileColumns.TITLE,
                MediaStore.Files.FileColumns.DATA
        };

        // Return only video metadata.
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        Uri queryUri = MediaStore.Files.getContentUri("external");

        CursorLoader cursorLoader = new CursorLoader(
                this,
                queryUri,
                projection,
                selection,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DISPLAY_NAME // Sort order.
        );

        Cursor cursor = cursorLoader.loadInBackground();
            final List<String> file_list = new ArrayList<>();
            final List<String> file_data=new ArrayList<>();
            if (cursor.moveToFirst()) {

               do {
                      file_list.add(cursor.getString(cursor.getColumnIndex(projection[0])));
                      file_data.add(cursor.getString(cursor.getColumnIndex(projection[1])));
               }while (cursor.moveToNext());


            }
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                    (this, android.R.layout.simple_list_item_1, file_list);
            lv.setAdapter(arrayAdapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    String strName = file_data.get(arg2);
                    //Toast.makeText(getApplicationContext(), " " + strName, Toast.LENGTH_LONG).show();
                    Intent i = new Intent(Video_Activity.this, sendActivity.class);
                    i.putExtra("Data", strName);
                    startActivity(i);
                }
            });


        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        }
    }

}
