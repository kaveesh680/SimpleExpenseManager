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
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.myDatabase.MyDatabaseHandler;

public class PersistentTransactionDAO implements TransactionDAO {
    private MyDatabaseHandler dbHandler;
    private DateFormat dateFormat;

    public PersistentTransactionDAO(MyDatabaseHandler dbHandler) {
        this.dbHandler = dbHandler;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    }
    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db= dbHandler.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHandler.getTransactionDate(), this.dateFormat.format(date));
        contentValues.put(dbHandler.getTransactionAccountNo(), accountNo);
        contentValues.put(dbHandler.getTransactionType(), expenseType.toString());
        contentValues.put(dbHandler.getTransactionAmount(), amount);

        //insert new row to transaction table
        db.insert(dbHandler.getTransactionTable(), null, contentValues);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase db = dbHandler.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + dbHandler.getTransactionTable() + " ORDER BY " + dbHandler.getTransactionDate() + " DESC ",
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
        SQLiteDatabase db = dbHandler.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + dbHandler.getTransactionTable() + " ORDER BY " + dbHandler.getTransactionDate() + " DESC " +
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