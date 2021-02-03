package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.myDatabase.MyDatabaseHandler;

public class PersistentExpenseManager extends ExpenseManager{

    private MyDatabaseHandler dbHandler;

    public PersistentExpenseManager(Context context) throws ExpenseManagerException {
        this.dbHandler = new MyDatabaseHandler(context);
        setup();
    }
    @Override
    public void setup() throws ExpenseManagerException {

        TransactionDAO persistentTransactionDAO = new PersistentTransactionDAO(dbHandler);
        setTransactionsDAO(persistentTransactionDAO);

        AccountDAO persistentAccountDAO = new PersistentAccountDAO(dbHandler);
        setAccountsDAO(persistentAccountDAO);
    }
}