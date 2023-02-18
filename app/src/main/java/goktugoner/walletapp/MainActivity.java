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
    //I'm declaring this as public because we gonna use it on budgethistoryactivity.java too
    public static float totalBudget = 0f;
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
        totalBudget = income - expenses;
        budgetTextView.setText("Budget: $" + totalBudget);
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
            //pass the total budget at start
            intent.putExtra("totalBudget", totalBudget);
            startActivity(intent);
        });
        //reset database if needed
        Button resetButton = findViewById(R.id.reset_database_button);
        resetButton.setOnClickListener(view -> {
            resetDatabase(view);
            setupPieChart(loadIncome(), loadExpenses());
        });
    }


    @SuppressLint("SetTextI18n")
    protected void onResume() {
        super.onResume();
        //update pie chart if the user enters a new income or expense data
        updatePieChart(loadIncome(), loadExpenses());
        //update budget textview to show the new budget
        TextView budgetTextView = findViewById(R.id.budget_text_view);
        //update the totalbudget again
        totalBudget = loadIncome() - loadExpenses();
        budgetTextView.setText("Budget: $" + (totalBudget));
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
        //get rid of the description label because it annoys me too much
        pieChart.getDescription().setEnabled(false);
        ArrayList<PieEntry> entries = new ArrayList<>();

        if (income == 0.0f && expenses == 0.0f) {
            entries.add(new PieEntry((float) 100.0, "Enter your budget info"));
        } else {
            entries.add(new PieEntry(totalBudget, "Income"));
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

    @SuppressLint("SetTextI18n")
    private void updatePieChart(float income, float expenses) {
        PieChart mPieChart = findViewById(R.id.pie_chart);
        List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(income, "Income"));
        pieEntries.add(new PieEntry(expenses, "Expenses"));

        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(ColorTemplate.rgb("#25D366"), ColorTemplate.rgb("#FF0000"));
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