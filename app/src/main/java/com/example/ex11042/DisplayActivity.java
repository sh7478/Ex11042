package com.example.ex11042;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class DisplayActivity extends AppCompatActivity implements View.OnCreateContextMenuListener {

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
        registerForContextMenu(lv);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
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
        }else if(id == R.id.menuSort) {
            Intent it = new Intent(this, SortingActivity.class);
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

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add("delete expense");
        menu.add("update expense");
    }

    public boolean onContextItemSelected(MenuItem item){
        String func = item.getTitle().toString();
        if(func.equals("delete expense"))
        {
            db = hlp.getWritableDatabase();
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int position = info.position;
            db.delete(Expenses.TABLE_NAME, Expenses.KEY_ID + "=?" , new String[]{Integer.toString(position + 1)});
            db.close();
            tbl.remove(position);
            adp.notifyDataSetChanged();
        }else if(func.equals("update expense"))
        {
            Intent it = new Intent(this, UpdateExpenseActivity.class);
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int position = info.position;
            it.putExtra("id", position + 1);
            startActivity(it);
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adp.notifyDataSetChanged();
    }
}