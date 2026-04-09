package com.example.ex11042;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class FilteringActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    String [] columns = null;
    String selection = null;
    String [] selectionArgs = null;
    String groupBy = null;
    String having = null;
    String orderBy = null;
    String limit = null;
    String category = "";
    String [] categories;
    LinearLayout priceInput;
    SQLiteDatabase db;
    HelperDB hlp;
    Switch priceOrCategory;
    Cursor crsr;
    ArrayList<String> tbl = new ArrayList<>();
    ArrayAdapter adp;
    ListView lvSort;
    Spinner spinCatFilter;
    EditText etPriceMin, etPriceMax;
    AlertDialog.Builder adb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtering);
        lvSort = findViewById(R.id.lvFilter);
        spinCatFilter = findViewById(R.id.spinCatFilter);
        etPriceMin = findViewById(R.id.etPriceMin);
        etPriceMax = findViewById(R.id.etPriceMax);
        priceOrCategory = findViewById(R.id.priceOrCategory);
        priceInput = findViewById(R.id.priceInput);
        categories = getResources().getStringArray(R.array.categories);
        hlp = new HelperDB(this);
        putDataInLv();
        spinCatFilter.setOnItemSelectedListener(this);
        ArrayAdapter<String> adpSpin = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, categories);
        spinCatFilter.setAdapter(adpSpin);
        spinCatFilter.setVisibility(View.INVISIBLE);
    }

    private void readData() {
        if(!priceOrCategory.isChecked())
        {
            if(!etPriceMin.getText().toString().equals("") || !etPriceMax.getText().toString().equals(""))
            {
                selection = Expenses.AMOUNT + " BETWEEN ? AND ?";
                selectionArgs = new String[]{etPriceMin.getText().toString(), etPriceMax.getText().toString()};
            }
            else
            {
                adb = new AlertDialog.Builder(this);
                adb.setTitle("Error");
                adb.setMessage("Please fill both min and max fields before Filtering");
                adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog ad = adb.create();
                ad.show();
            }
        }
        else
        {
            if(!category.equals("")) {
                if(selection == null) {
                    selection = Expenses.CATEGORY + " =?";
                }
                else
                {
                    selection += " OR " + Expenses.CATEGORY + " =?";
                }
                selectionArgs = new String[]{category};
            }
            else
            {
                adb = new AlertDialog.Builder(this);
                adb.setTitle("Error");
                adb.setMessage("Please select a category before Filtering");
                adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog ad = adb.create();
                ad.show();
            }
        }
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
        }else if(id == R.id.menuSort) {
            Intent it = new Intent(this, SortingActivity.class);
            startActivity(it);
        }else if(id == R.id.menuCred) {
            Intent it = new Intent(this, CreditsActivity.class);
            startActivity(it);
        }
        return super.onOptionsItemSelected(item);
    }

    public void Filter(View view) {
        readData();
        tbl.clear();
        putDataInLv();
        adp.notifyDataSetChanged();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        category = categories[i];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.i("Spinner", "Nothing selected");
    }

    public void putDataInLv()
    {
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
                this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, tbl);
        lvSort.setAdapter(adp);
    }

    public void clearAll(View view) {
        priceOrCategory.setChecked(false);
        etPriceMin.setText("");
        etPriceMax.setText("");
        category = "";
        spinCatFilter.setSelection(0);
        selection = null;
        selectionArgs = null;
        tbl.clear();
        putDataInLv();
    }

    public void changeFilter(View view) {
        selection = null;
        selectionArgs = null;
        category = "";
        spinCatFilter.setSelection(0);
        if(priceOrCategory.isChecked())
        {
            priceInput.setVisibility(View.INVISIBLE);
            spinCatFilter.setVisibility(View.VISIBLE);
        }
        else
        {
            priceInput.setVisibility(View.VISIBLE);
            spinCatFilter.setVisibility(View.INVISIBLE);
        }

    }
}