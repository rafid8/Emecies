package com.rapplis.android.emecies;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.rapplis.android.emecies.Data.DataContract;
import com.rapplis.android.emecies.Data.DatabaseHelper;

import java.lang.invoke.CallSite;
import java.util.ArrayList;

public class CallCenterList extends AppCompatActivity {
    private static String title;
    private static byte[] icon;
    private static byte[] cover;
    private static String phone;

    GridAdapter listAdapter;
    GridView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                DataContract.DataEntry.COLUMN_NAME,
                DataContract.DataEntry.COLUMN_PHONE,
                DataContract.DataEntry.COLUMN_PROFILE_IMAGE,
                DataContract.DataEntry.COLUMN_COVER_IMAGE};

        Cursor cursor = db.query(
                DataContract.DataEntry.CALL_CENTER_TABLE_NAME,   // The table to query
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order

        final ArrayList<List> lists = new ArrayList<>();

        try {
            lists.clear();

            // Figure out the index of each column
            int nameColumnIndex = cursor.getColumnIndex(DataContract.DataEntry.COLUMN_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(DataContract.DataEntry.COLUMN_PHONE);
            int profileImageColumnIndex = cursor.getColumnIndex(DataContract.DataEntry.COLUMN_PROFILE_IMAGE);
            int coverImageColumnIndex = cursor.getColumnIndex(DataContract.DataEntry.COLUMN_COVER_IMAGE);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {

                lists.add(new List(cursor.getString(nameColumnIndex),cursor.getBlob(profileImageColumnIndex),
                        cursor.getBlob(coverImageColumnIndex), cursor.getString(phoneColumnIndex),
                        null, null));
            }
        } finally {
            cursor.close();
        }

        LinearLayout header = findViewById(R.id.header);
        header.setVisibility(View.GONE);

        GridAdapter adapter = new GridAdapter(this, lists);

        GridView gridView = findViewById(R.id.list);

        this.listView = gridView;
        this.listAdapter = adapter;

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final List list = lists.get(position);
                title = list.getText();
                icon = list.getIcon();
                cover = list.getCover();
                phone = list.getPhone();

                Intent option = new Intent(view.getContext(), CallCenterOption.class);
                startActivity(option);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView)myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (TextUtils.isEmpty(s)){
                    listAdapter.filter("");
                    listView.clearTextFilter();
                }
                else {
                    listAdapter.filter(s);
                }
                return true;
            }
        });
        return true;
    }

    public String title(){
        return title;
    }
    public byte[] icon(){
        return icon;
    }
    public byte[] setCover(){
        return cover;
    }
    public String setPhone(){
        return phone;
    }
}
