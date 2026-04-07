package com.example.ex11042;

import static com.example.ex11042.Expenses.AMOUNT;
import static com.example.ex11042.Expenses.CATEGORY;
import static com.example.ex11042.Expenses.DESCRIPTION;
import static com.example.ex11042.Expenses.EXPENSE_TIME;
import static com.example.ex11042.Expenses.KEY_ID;
import static com.example.ex11042.Expenses.TABLE_NAME;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class HelperDB extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Expenses.db";
    public static final int DATABASE_VERSION = 1;
    public String strCreate, strDelete;
    public HelperDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        strCreate = "CREATE TABLE " +TABLE_NAME;
        strCreate += " ("+KEY_ID+" INTEGER PRIMARY KEY,";
        strCreate += " "+DESCRIPTION+" TEXT,";
        strCreate += " "+AMOUNT+" REAL,";
        strCreate += " "+CATEGORY+" TEXT,";
        strCreate += " "+EXPENSE_TIME+" DATE";
        strCreate += ")";
        db.execSQL(strCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        strDelete="DROP TABLE IF EXISTS "+TABLE_NAME;
        db.execSQL(strDelete);
        onCreate(db);
    }
}
