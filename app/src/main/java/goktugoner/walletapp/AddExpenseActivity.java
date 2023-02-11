package goktugoner.walletapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Calendar;

public class AddExpenseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_expense);
        Spinner expenseCategorySpinner = findViewById(R.id.expense_category_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.expense_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expenseCategorySpinner.setAdapter(adapter);
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        final TextView expenseDateTextView = findViewById(R.id.expense_date_text_view);
        expenseDateTextView.setOnClickListener(view -> {
            @SuppressLint("DefaultLocale") DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddExpenseActivity.this,
                    (view1, year1, monthOfYear, dayOfMonth) -> expenseDateTextView.setText(String.format("%d/%d/%d", dayOfMonth, monthOfYear + 1, year1)), year, month, day);
            datePickerDialog.show();
        });
        Button addExpenseButton = findViewById(R.id.add_expense_button);
        addExpenseButton.setOnClickListener(view -> addExpense());
    }

    private void addExpense() {
        EditText expenseAmountEditText = findViewById(R.id.expense_amount_edit_text);
        TextView expenseDateTextView = findViewById(R.id.expense_date_text_view);
        Spinner expenseCategorySpinner = findViewById(R.id.expense_category_spinner);

        String amountString = expenseAmountEditText.getText().toString();
        String dateString = expenseDateTextView.getText().toString();
        String categoryString = expenseCategorySpinner.getSelectedItem().toString();

        if (amountString.isEmpty()) { //this is broken
            Toast.makeText(AddExpenseActivity.this, "Please fill in the amount!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dateString.isEmpty() || dateString.equals(getString(R.string.expense_date))) {
            Toast.makeText(AddExpenseActivity.this, "Please select a date!", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            double amount = Double.parseDouble(amountString);
            WalletDB db = new WalletDB(this);
            db.addTransaction(amount, dateString, "expense", categoryString);
            Toast.makeText(AddExpenseActivity.this, "Expense added!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (NumberFormatException e) {
            Toast.makeText(AddExpenseActivity.this, "Please enter a valid amount!", Toast.LENGTH_SHORT).show();
        }
    }

}
