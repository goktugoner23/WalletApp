package goktugoner.walletapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView budgetTextView = findViewById(R.id.budget_text_view);
        //load database
        WalletDB db = new WalletDB(this);
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        db.onCreate(sqLiteDatabase);
        // load budget and expenses data
        float income = loadIncome();
        float expenses = loadExpenses();
        budgetTextView.setText("Budget: $" + (income - expenses));
        // setup pie chart
        setupPieChart(income, expenses);
        Button addIncomeButton = findViewById(R.id.add_income_button);
        addIncomeButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddIncomeActivity.class);
            startActivity(intent);
        });

        Button addExpenseButton = findViewById(R.id.add_expense_button);
        addExpenseButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            startActivity(intent);
        });
        //budget history activity
        Button budgetHistoryButton = findViewById(R.id.budget_history_button);
        budgetHistoryButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, BudgetHistoryActivity.class);
            startActivity(intent);
        });
        //reset database if needed
        Button resetButton = findViewById(R.id.reset_database_button);
        resetButton.setOnClickListener(view -> {
            resetDatabase(view);
            setupPieChart(loadIncome(), loadExpenses());
        });
    }



    protected void onResume() {
        super.onResume();
        updatePieChart(loadIncome(), loadExpenses());
    }
    private float loadIncome() {
        WalletDB db = new WalletDB(this);
        Cursor cursor = db.getAllTransactions();
        float income = 0;
        if (cursor.moveToFirst()) {
            int amountColumnIndex = cursor.getColumnIndex(WalletDB.COLUMN_AMOUNT);
            int typeColumnIndex = cursor.getColumnIndex(WalletDB.COLUMN_TYPE);

            do {
                if (typeColumnIndex != -1 && cursor.getString(typeColumnIndex).equals("income")) {
                    income += cursor.getInt(amountColumnIndex);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return income;
    }

    private float loadExpenses() {
        WalletDB db = new WalletDB(this);
        Cursor cursor = db.getAllTransactions();

        float expenses = 0;
        if (cursor.moveToFirst()) {
            int amountColumnIndex = cursor.getColumnIndex(WalletDB.COLUMN_AMOUNT);
            int typeColumnIndex = cursor.getColumnIndex(WalletDB.COLUMN_TYPE);

            do {
                if (typeColumnIndex != -1 && cursor.getString(typeColumnIndex).equals("expense")) {
                    expenses += cursor.getInt(amountColumnIndex);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return expenses;
    }

    private void setupPieChart(float income, float expenses) {
        PieChart pieChart = findViewById(R.id.pie_chart);

        ArrayList<PieEntry> entries = new ArrayList<>();

        if (income == 0.0f && expenses == 0.0f) {
            entries.add(new PieEntry((float) 100.0, "Enter your budget info"));
        } else {
            entries.add(new PieEntry(income - expenses, "Income"));
            entries.add(new PieEntry(expenses, "Expenses"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(20f);
        dataSet.setSliceSpace(3f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.animateXY(1000, 1000);
        pieChart.invalidate();
        pieChart.notifyDataSetChanged();
    }

    private void updatePieChart(float income, float expenses) {
        PieChart mPieChart = findViewById(R.id.pie_chart);
        List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(income, "Income"));
        pieEntries.add(new PieEntry(expenses, "Expenses"));

        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        dataSet.setValueTextSize(20f);
        dataSet.setValueTextColor(Color.BLACK);
        PieData data = new PieData(dataSet);
        mPieChart.setData(data);
        mPieChart.invalidate();
    }

    public void resetDatabase(View view) {
        WalletDB db = new WalletDB(this);
        db.dropDatabase();
        Toast.makeText(MainActivity.this, "Database dropped!", Toast.LENGTH_SHORT).show();
    }

}