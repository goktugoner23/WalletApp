package goktugoner.walletapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WalletDB extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "wallet.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "transactions";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TYPE = "Type";
    public static final String COLUMN_CATEGORY = "category";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_AMOUNT + " REAL, " +
            COLUMN_DATE + " TEXT, " +
            COLUMN_TYPE + " TEXT, " +
            COLUMN_CATEGORY + " TEXT" +
            ")";

    public WalletDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) { //check table if exists then create a new one if not
        String query = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_NAME + "';";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() <= 0) {
            db.execSQL(CREATE_TABLE);
        }
        cursor.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addTransaction(double amount, String date, String type, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_AMOUNT, amount);
        contentValues.put(COLUMN_DATE, date);
        contentValues.put(COLUMN_TYPE, type);
        contentValues.put(COLUMN_CATEGORY, category);

        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return result != -1;
    }
    public Cursor getAllTransactions() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }

    public void dropDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}

