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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.RowIdLifetime;
import java.util.ArrayList;

public class DisplayActivity extends AppCompatActivity implements View.OnCreateContextMenuListener , AdapterView.OnItemSelectedListener{

    SQLiteDatabase db;
    HelperDB hlp;
    Cursor crsr;
    ListView lv;
    ArrayList<String> tbl = new ArrayList<>();
    ArrayAdapter adp;
    Spinner spinMonths;
    EditText etDescSearch;
    TextView tvSumByMonth, tvSearchResult;
    String [] months;
    String [] columns = null;
    String selection = null;
    String [] selectionArgs = null;
    String groupBy = null;
    String having = null;
    String orderBy = null;
    String limit = null;
    ArrayList<Integer> idList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        lv = findViewById(R.id.lvDisplay);
        spinMonths = findViewById(R.id.spinMonths);
        etDescSearch = findViewById(R.id.etDescSearch);
        tvSumByMonth = findViewById(R.id.tvSumByMonth);
        tvSearchResult = findViewById(R.id.tvSearchResult);
        months = getResources().getStringArray(R.array.months);
        spinMonths.setOnItemSelectedListener(this);
        ArrayAdapter<String> adpSpin = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, months);
        spinMonths.setAdapter(adpSpin);
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
            String tmp = "" + desc + ", " + amount + ", " + category + ", " + time;
            tbl.add(tmp);
            idList.add(key);
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
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        int key = idList.get(position);
        db = hlp.getWritableDatabase();

        if(func.equals("delete expense"))
        {
            db.delete(Expenses.TABLE_NAME, Expenses.KEY_ID + "=?" , new String[]{Integer.toString(key)});
            db.close();
            tbl.remove(position);
            idList.remove(position);
            adp.notifyDataSetChanged();
        }else if(func.equals("update expense"))
        {
            Intent it = new Intent(this, UpdateExpenseActivity.class);
            it.putExtra("id", key);
            startActivity(it);
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tbl.clear();
        idList.clear();
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
            String tmp = "" + desc + ", " + amount + ", " + category + ", " + time;
            tbl.add(tmp);
            idList.add(key);
            crsr.moveToNext();
        }
        crsr.close();
        db.close();
        adp.notifyDataSetChanged();
    }

    public void searchByDescription(View view) {
        String description = etDescSearch.getText().toString();
        db = hlp.getWritableDatabase();
        columns = null;
        selection = Expenses.DESCRIPTION + "=?";
        selectionArgs = new String[] {description};
        groupBy = null;
        having = null;
        orderBy = null;
        limit = null;
        crsr = db.query(Expenses.TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        int col1 = crsr.getColumnIndex(Expenses.KEY_ID);
        int col2 = crsr.getColumnIndex(Expenses.DESCRIPTION);
        int col3 = crsr.getColumnIndex(Expenses.AMOUNT);
        int col4 = crsr.getColumnIndex(Expenses.CATEGORY);
        int col5 = crsr.getColumnIndex(Expenses.EXPENSE_TIME);
        crsr.moveToFirst();
        if(!crsr.isAfterLast())
        {
            int key = crsr.getInt(col1);
            String desc = crsr.getString(col2);
            double amount = crsr.getDouble(col3);
            String category = crsr.getString(col4);
            String time = crsr.getString(col5);
            String tmp = "" + desc + ", " + amount + ", " + category + ", " + time;
            tvSearchResult.setText(tmp);
        }
        else
        {
            tvSearchResult.setText("You didn't searched for an expense");
        }
        crsr.close();
        db.close();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        double sum = 0;
        if(position != 0) {
            db = hlp.getReadableDatabase();
            String selection = Expenses.EXPENSE_TIME + " LIKE ?";
            String[] selectionArgs = {"%/" + position + "/%"};
            crsr = db.query(Expenses.TABLE_NAME, null, selection, selectionArgs, null, null, null);
            int col = crsr.getColumnIndex(Expenses.AMOUNT);
            crsr.moveToFirst();
            sum = 0;
            while(!crsr.isAfterLast())
            {
                sum += crsr.getDouble(col);
                crsr.moveToNext();
            }
            crsr.close();
            db.close();
            tvSumByMonth.setText("the sum of the expenses in the selected month is: " + sum);
        }
        else
        {
            tvSumByMonth.setText("You need to select a month in order to see the month's total expenses");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        tvSumByMonth.setText("You need to select a month in order to see the month's total expenses");
    }
}
