package com.example.ex11042;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    SQLiteDatabase db;
    HelperDB hlp;
    Spinner catSpin;
    String [] categories;
    EditText eTDesc, eTPrice;
    AlertDialog.Builder adb;
    String category;
    Button datePick;
    String date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hlp = new HelperDB(this);
        db = hlp.getWritableDatabase();
        db.close();
        catSpin = findViewById(R.id.catSpin);
        eTDesc = findViewById(R.id.eTDesc);
        eTPrice = findViewById(R.id.eTPrice);
        datePick = findViewById(R.id.datePick);
        categories = getResources().getStringArray(R.array.categories);
        catSpin.setOnItemSelectedListener(this);
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, categories);
        catSpin.setAdapter(adp);
        datePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker view, int year, int month, int day) {
                        date = day + "/" + (month + 1) + "/" + year;
                    }
                }, year, month, day);
                dpd.show();
            }
        });
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
            eTDesc.setText("");
            eTPrice.setText("");
            catSpin.setSelection(0);
            date = "";
            category = "";
        }else if(id == R.id.menuExpens) {
            Intent it = new Intent(this, DisplayActivity.class);
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(i != 0) {
            category = categories[i];
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.i("Spinner", "Nothing selected");
    }

    public void addToDb(View view) {
        if(eTDesc.getText().toString().isEmpty() || eTPrice.getText().toString().isEmpty()) {
            adb = new AlertDialog.Builder(this);
            adb.setTitle("Error");
            adb.setMessage("Please fill all the fields before confirming");
            adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog ad = adb.create();
            ad.show();
        }
        else
        {
            ContentValues cv = new ContentValues();
            cv.put(Expenses.DESCRIPTION, eTDesc.getText().toString());
            cv.put(Expenses.AMOUNT, eTPrice.getText().toString());
            cv.put(Expenses.CATEGORY, category);
            cv.put(Expenses.EXPENSE_TIME, date);
            db = hlp.getWritableDatabase();
            db.insert(Expenses.TABLE_NAME, null, cv);
            db.close();
            eTDesc.setText("");
            eTPrice.setText("");
            catSpin.setSelection(0);
            date = "";
            category = "";
        }
    }
}