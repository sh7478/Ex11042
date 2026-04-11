package com.example.ex11042;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class SortingActivity extends AppCompatActivity {

    String [] columns = null;
    String selection = null;
    String [] selectionArgs = null;
    String groupBy = null;
    String having = null;
    String orderBy = null;
    String limit = null;
    SQLiteDatabase db;
    HelperDB hlp;
    Cursor crsr;
    ListView lvSort;
    Switch dateOrPrice, descOrAsce;
    ArrayAdapter adp;
    ArrayList<String> tbl = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorting);
        lvSort = findViewById(R.id.lvSort);
        dateOrPrice = findViewById(R.id.dateOrPrice);
        descOrAsce = findViewById(R.id.descOrAsce);
        hlp = new HelperDB(this);
        db = hlp.getReadableDatabase();
        readSwitches();
        db.close();
        putDataInLv();
    }

    private void putDataInLv() {
        db = hlp.getReadableDatabase();
        crsr = db.query(Expenses.TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
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
            String tmp = "" + desc + ", " + amount + ", " + category + ", " + time;
            tbl.add(tmp);
            crsr.moveToNext();
        }
        crsr.close();
        db.close();
        adp = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item, tbl);
        lvSort.setAdapter(adp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();
        if(id == R.id.menuInpu)
        {
            Intent it = new Intent(this, MainActivity.class);
            startActivity(it);
        }else if(id == R.id.menuExpens) {
            Intent it = new Intent(this, DisplayActivity.class);
            startActivity(it);
        }else if(id == R.id.menuFilter) {
            Intent it = new Intent(this, FilteringActivity.class);
            startActivity(it);
        }else if(id == R.id.menuCred) {
            Intent it = new Intent(this, CreditsActivity.class);
            startActivity(it);
        }
        return super.onOptionsItemSelected(item);
    }

    public void readSwitches()
    {
        if(dateOrPrice.isChecked())
        {
            orderBy = Expenses.EXPENSE_TIME;
        }
        else
        {
            orderBy = Expenses.AMOUNT;
        }
        if(descOrAsce.isChecked())
        {
            orderBy += " ASC";
        }
        else
        {
            orderBy += " DESC";
        }
    }

    public void ChangeDescAsc(View view) {
        readSwitches();
        tbl.clear();
        putDataInLv();
    }

    public void changeDatePrice(View view) {
        readSwitches();
        tbl.clear();
        putDataInLv();
    }
}