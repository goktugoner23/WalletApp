package goktugoner.walletapp;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

public class BudgetHistoryActivity extends AppCompatActivity {

    ListView transactionList;
    WalletDB walletDB;
    SimpleCursorAdapter adapter;
    String[] fromColumns = {WalletDB.COLUMN_DATE, WalletDB.COLUMN_CATEGORY, WalletDB.COLUMN_AMOUNT, WalletDB.COLUMN_TYPE};
    int[] toViews = {R.id.textViewDate, R.id.textViewCategory, R.id.textViewAmount};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.budget_history);

        transactionList = findViewById(R.id.transactions_list_view);
        walletDB = new WalletDB(this);

        Cursor cursor = walletDB.getAllTransactions();
        adapter = new SimpleCursorAdapter(this, R.layout.transaction_row, cursor, fromColumns, toViews, 0);
        adapter.setViewBinder((view, cursor1, columnIndex) -> {
            if (columnIndex == cursor1.getColumnIndex(WalletDB.COLUMN_AMOUNT)) {
                int amount = cursor1.getInt(columnIndex);
                int typeIndex = cursor1.getColumnIndex(WalletDB.COLUMN_TYPE);
                String type = cursor1.getString(typeIndex);
                String amountStr;
                if (type.equals("income")) {
                    amountStr = "+" + amount;
                } else {
                    amountStr = "-" + amount;
                }
                ((android.widget.TextView) view).setText(amountStr);
                return true;
            }
            return false;
        });
        transactionList.setAdapter(adapter);
    }
}
