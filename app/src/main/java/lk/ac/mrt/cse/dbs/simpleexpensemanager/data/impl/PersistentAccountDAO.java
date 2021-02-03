package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.myDatabase.MyDatabaseHandler;

public class PersistentAccountDAO implements AccountDAO {

    private MyDatabaseHandler dbHandler;

    public PersistentAccountDAO(MyDatabaseHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase db = dbHandler.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " + dbHandler.getAccountAccountNo() + " FROM " + dbHandler.getAccountTable(),
                null
        );

        ArrayList<String> accNumbers = new ArrayList<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            accNumbers.add(cursor.getString(0));
        }

        cursor.close();
        return accNumbers;
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase db = dbHandler.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + dbHandler.getAccountTable(),
                null
        );

        ArrayList<Account> accounts = new ArrayList<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Account account = new Account(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3)
            );
            accounts.add(account);
        }

        cursor.close();
        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = dbHandler.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + dbHandler.getAccountTable() + " WHERE " + dbHandler.getAccountAccountNo() + "=?;"
                , new String[]{accountNo});

        Account account;
        if (cursor != null && cursor.moveToFirst()) {
            account = new Account(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3)
            );
        } else {
            throw new InvalidAccountException("The Account "+accountNo+" is Invalid");
        }
        cursor.close();
        return account;
    }

    @Override
    public void addAccount(Account account) {

        Account existingAccount = null;
        try {
            existingAccount = getAccount(account.getAccountNo());
        } catch (InvalidAccountException e) {
            e.printStackTrace();
        }
        if (existingAccount!=null){
            System.out.println("Account already exists.");
            return;
        }

        SQLiteDatabase db = dbHandler.getWritableDatabase();

        ContentValues contentvalues = new ContentValues();
        contentvalues.put(dbHandler.getAccountAccountNo(), account.getAccountNo());
        contentvalues.put(dbHandler.getAccountBankName(), account.getBankName());
        contentvalues.put(dbHandler.getAccountHolderName(), account.getAccountHolderName());
        contentvalues.put(dbHandler.getAccountAccountBalance(), account.getBalance());

        db.insert(dbHandler.getAccountTable(), null, contentvalues);
        db.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = dbHandler.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + dbHandler.getAccountTable() + " WHERE " + dbHandler.getAccountAccountNo() + "=?;"
                , new String[]{accountNo}
        );

        if (cursor.moveToFirst()) {
            db.delete(
                    dbHandler.getAccountTable(),
                    dbHandler.getAccountAccountNo()+ " = ?",
                    new String[]{accountNo}
            );
        } else {
            throw new InvalidAccountException("The Account "+accountNo+" is Invalid");
        }
        cursor.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db = dbHandler.getWritableDatabase();

        if (accountNo==null) {
            throw new InvalidAccountException("Account is not specified.");
        }

        Account account = this.getAccount(accountNo);

        if (account != null) {
            double updatedBalance;

            if (expenseType == ExpenseType.INCOME) {
                updatedBalance = account.getBalance() + amount;
            } else if (expenseType == ExpenseType.EXPENSE) {
                updatedBalance = account.getBalance() - amount;
            } else {
                throw new InvalidAccountException("Invalid Expense Type");
            }

            if (updatedBalance < 0){
                throw  new InvalidAccountException("Balance of " + account.getBalance() + " is insufficient for the transaction.");
            }

            db.execSQL(
                    "UPDATE " + dbHandler.getAccountTable() +
                            " SET " + dbHandler.getAccountAccountBalance() + " = ?" +
                            " WHERE " + dbHandler.getAccountAccountNo() + " = ?",
                    new String[]{Double.toString(updatedBalance), accountNo});

        } else {
            throw new InvalidAccountException("The Account "+accountNo+" is Invalid");
        }
    }
}