package com.mohdeva.sendit;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class pdf_activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_apk);
            ListView lv=(ListView)findViewById(R.id.apklist);

        try {
            String[] projection = {
                    MediaStore.Files.FileColumns.TITLE,
                    MediaStore.Files.FileColumns.DATA
            };
            String selection = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
            String[] selectionArgsAPK = new String[]{ mimeType };

            Uri queryUri = MediaStore.Files.getContentUri("external");

            CursorLoader cursorLoader = new CursorLoader(
                    this,
                    queryUri,
                    projection,
                    selection,
                    selectionArgsAPK, // Selection args pdf
                    MediaStore.Files.FileColumns.DISPLAY_NAME // Sort order.
            );
            Cursor cursor = cursorLoader.loadInBackground();

            final List<String> file_list = new ArrayList<>();
            final List<String> file_data = new ArrayList<>();
            if (cursor.moveToFirst()) {

                do {
                    file_list.add(cursor.getString(cursor.getColumnIndex(projection[0])));
                    file_data.add(cursor.getString(cursor.getColumnIndex(projection[1])));
                } while (cursor.moveToNext());


            }
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                    (this, android.R.layout.simple_list_item_1, file_list);
            lv.setAdapter(arrayAdapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    String strName = file_data.get(arg2);
                    //Toast.makeText(getApplicationContext(), " " + strName, Toast.LENGTH_LONG).show();
                    Intent i = new Intent(pdf_activity.this, sendActivity.class);
                    i.putExtra("Data", strName);
                    startActivity(i);
                }
            });


        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}



