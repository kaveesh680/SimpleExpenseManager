package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.myDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHandler extends SQLiteOpenHelper {
    private final static String dbName = "180369L";
    private final static int dbVersion = 1;
    private final static String accountTable = "account_table";
    private final static String transactionTable = "transaction_table";
    private final static String accountAccountNo = "account_no";
    private final static String accountBankName = "bank_name";
    private final static String accountHolderName = "holder_name";
    private final static String accountAccountBalance = "balance";
    private final static String transactionId = "id";
    private final static String transactionAccountNo = "account_no";
    private final static String transactionType = "type";
    private final static String transactionDate = "date";
    private final static String transactionAmount = "amount";
    private final static String typeExpense = "EXPENSE";
    private final static String typeIncome = "INCOME";

    public MyDatabaseHandler(Context context) {
        super(context, dbName, null, dbVersion);
    }
    public static String getDbName() { return dbName; }

    public static int getDbVersion() { return dbVersion; }

    public static String getAccountTable() {
        return accountTable;
    }

    public static String getTransactionTable() {
        return transactionTable;
    }

    public static String getAccountAccountNo() {
        return accountAccountNo;
    }

    public static String getAccountBankName() {
        return accountBankName;
    }

    public static String getAccountHolderName() {
        return accountHolderName;
    }

    public static String getAccountAccountBalance() {
        return accountAccountBalance;
    }

    public static String getTransactionId() {
        return transactionId;
    }

    public static String getTransactionAccountNo() {
        return transactionAccountNo;
    }

    public static String getTransactionType() {
        return transactionType;
    }

    public static String getTransactionDate() {
        return transactionDate;
    }

    public static String getTransactionAmount() {
        return transactionAmount;
    }

    public static String getTypeExpense() {
        return typeExpense;
    }

    public static String getTypeIncome() {
        return typeIncome;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS " + accountTable + "(" +
                        accountAccountNo + " TEXT PRIMARY KEY," +
                        accountBankName + " TEXT NOT NULL," +
                        accountHolderName + " TEXT NOT NULL," +
                        accountAccountBalance + " REAL" +
                        ");"
        );

        sqLiteDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS " + transactionTable + "(" +
                        transactionId + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        transactionDate + " TEXT NOT NULL," +
                        transactionAccountNo + " TEXT NOT NULL," +
                        transactionType + " TEXT NOT NULL," +
                        transactionAmount + " REAL NOT NULL," +
                        "FOREIGN KEY (" + transactionAccountNo + ") REFERENCES "
                        + accountTable + "(" + accountAccountNo + ")," +
                        "CHECK ("+transactionType+"==\""+typeExpense+"\" OR "+transactionType+"==\""+typeIncome+"\")"+
                        ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + transactionTable);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + accountTable);
        onCreate(sqLiteDatabase);
    }
}