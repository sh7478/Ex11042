package com.example.ex11042;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class UpdateExpenseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    SQLiteDatabase db;
    HelperDB hlp;
    int id;
    EditText eTDescUpd, eTPriceUpd;
    Spinner spinCatUpd;
    Button datePickUpd;
    String date;
    String category;
    String [] categories;
    String [] columns = null;
    String selection = Expenses.KEY_ID + "=?";
    String groupBy = null;
    String having = null;
    String orderBy = null;
    AlertDialog.Builder adb;
    String limit = null;
    Cursor crsr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_expense);
        categories = getResources().getStringArray(R.array.categories);
        hlp = new HelperDB(this);
        db = hlp.getWritableDatabase();
        db.close();
        Intent it = getIntent();
        id = it.getIntExtra("id", 0);
        String [] selectionArgs = {id +""};
        spinCatUpd = findViewById(R.id.spinCatUpd);
        eTDescUpd = findViewById(R.id.eTDescUpd);
        eTPriceUpd = findViewById(R.id.etPriceUpd);
        datePickUpd = findViewById(R.id.datePickUpd);
        categories = getResources().getStringArray(R.array.categories);
        spinCatUpd.setOnItemSelectedListener(this);
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, categories);
        spinCatUpd.setAdapter(adp);
        datePickUpd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(UpdateExpenseActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker view, int year, int month, int day) {
                        date = day + "/" + (month + 1) + "/" + year;
                    }
                }, year, month, day);
                dpd.show();
            }
        });
        db = hlp.getWritableDatabase();
        crsr = db.query(Expenses.TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        int col1 = crsr.getColumnIndex(Expenses.KEY_ID);
        int col2 = crsr.getColumnIndex(Expenses.DESCRIPTION);
        int col3 = crsr.getColumnIndex(Expenses.AMOUNT);
        int col4 = crsr.getColumnIndex(Expenses.CATEGORY);
        int col5 = crsr.getColumnIndex(Expenses.EXPENSE_TIME);
        crsr.moveToFirst();
        eTDescUpd.setText(crsr.getString(col2));
        eTPriceUpd.setText(crsr.getString(col3));
        spinCatUpd.setSelection(findCatIndex(crsr.getString(col4)));
        crsr.close();
        db.close();
    }

    private int findCatIndex(String cat) {
        int index = 0;
        for(int i = 0; i < categories.length; i++)
        {
            if(categories[i].equals(cat))
            {
                index  = i;
            }
        }
        return index;
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

    public void update(View view) {
        if(eTDescUpd.getText().toString().isEmpty() || eTPriceUpd.getText().toString().isEmpty() || date.isEmpty())
        {
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
        else {
            ContentValues cv = new ContentValues();
            cv.put(Expenses.DESCRIPTION, eTDescUpd.getText().toString());
            cv.put(Expenses.AMOUNT, eTPriceUpd.getText().toString());
            cv.put(Expenses.CATEGORY, category);
            cv.put(Expenses.EXPENSE_TIME, date);
            db = hlp.getWritableDatabase();
            db.update(Expenses.TABLE_NAME, cv, Expenses.KEY_ID + "=?", new String[]{id + ""});
        }
        finish();
    }
}
