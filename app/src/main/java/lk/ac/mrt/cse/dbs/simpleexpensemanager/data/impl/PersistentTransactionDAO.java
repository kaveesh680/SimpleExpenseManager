package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.myDatabase.DatabaseHandler;

public class PersistentTransactionDAO implements TransactionDAO {
    private DatabaseHandler databseHandler;
    private DateFormat dateFormat;

    public PersistentTransactionDAO(DatabaseHandler dbHandler) {
        this.databseHandler = dbHandler;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    }
    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db= databseHandler.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(databseHandler.getTransactionDate(), this.dateFormat.format(date));
        contentValues.put(databseHandler.getTransactionAccountNo(), accountNo);
        contentValues.put(databseHandler.getTransactionType(), expenseType.toString());
        contentValues.put(databseHandler.getTransactionAmount(), amount);

        //insert new row to transaction table
        db.insert(databseHandler.getTransactionTable(), null, contentValues);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase database = databseHandler.getReadableDatabase();

        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + databseHandler.getTransactionTable() + " ORDER BY " + databseHandler.getTransactionDate() + " DESC ",
                null
        );

        ArrayList<Transaction> transactions = new ArrayList<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            try {

                Transaction transaction = new Transaction(
                        this.dateFormat.parse(cursor.getString(1)),
                        cursor.getString(2),
                        ExpenseType.valueOf(cursor.getString(3).toUpperCase()),
                        cursor.getDouble(4)
                );

                transactions.add(transaction);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        SQLiteDatabase db = databseHandler.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + databseHandler.getTransactionTable() + " ORDER BY " + databseHandler.getTransactionDate() + " DESC " +
                        " LIMIT ?;"
                , new String[]{Integer.toString(limit)}
        );


        ArrayList<Transaction> transactions = new ArrayList<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            try {

                Transaction transaction = new Transaction(
                        this.dateFormat.parse(cursor.getString(1)),
                        cursor.getString(2),
                        ExpenseType.valueOf(cursor.getString(3).toUpperCase()),
                        cursor.getDouble(4)
                );

                transactions.add(transaction);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return transactions;
    }
}