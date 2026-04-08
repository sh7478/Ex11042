package com.example.ex11042;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class DisplayActivity extends AppCompatActivity {

    SQLiteDatabase db;
    HelperDB hlp;
    Cursor crsr;
    ListView lv;
    ArrayList<String> tbl = new ArrayList<>();
    ArrayAdapter adp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        lv = findViewById(R.id.lvDisplay);
        hlp = new HelperDB(this);
        db = hlp.getWritableDatabase();
        crsr = db.query(Expenses.TABLE_NAME, null, null, null, null, null, null);
        int col1 = crsr.getColumnIndex(Expenses.KEY_ID);
        int col2 = crsr.getColumnIndex(Expenses.DESCRIPTION);
        int col3 = crsr.getColumnIndex(Expenses.AMOUNT);
        int col4 = crsr.getColumnIndex(Expenses.CATEGORY);
        int col5 = crsr.getColumnIndex(Expenses.EXPENSE_TIME);
        crsr.moveToFirst();
        while (!crsr.isAfterLast()){
            int key = crsr.getInt(col1);
            String desc = crsr.getString(col2);
            double amount = crsr.getDouble(col3);
            String category = crsr.getString(col4);
            String time = crsr.getString(col5);
            String tmp = "" + key + ", " + desc + ", " + amount + ", " + category + ", " + time;
            tbl.add(tmp);
            crsr.moveToNext();
        }
        crsr.close();
        db.close();
        adp = new ArrayAdapter<String>(
                this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, tbl);
        lv.setAdapter(adp);
    }
}