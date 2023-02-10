package goktugoner.walletapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Calendar;

public class AddIncomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_income);
        Spinner incomeCategorySpinner = findViewById(R.id.income_category_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.income_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        incomeCategorySpinner.setAdapter(adapter);
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        final TextView incomeDateTextView = findViewById(R.id.income_date_text_view);
        incomeDateTextView.setOnClickListener(view -> {
            @SuppressLint("DefaultLocale") DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddIncomeActivity.this,
                    (view1, year1, monthOfYear, dayOfMonth) -> incomeDateTextView.setText(String.format("%d/%d/%d", dayOfMonth, monthOfYear + 1, year1)), year, month, day);
            datePickerDialog.show();
        });
        Button addIncomeButton = findViewById(R.id.add_income_button);
        addIncomeButton.setOnClickListener(view -> addIncome());
    }

    private void addIncome() {
        EditText incomeAmountEditText = findViewById(R.id.income_amount_edit_text);
        TextView incomeDateTextView = findViewById(R.id.income_date_text_view);
        Spinner incomeCategorySpinner = findViewById(R.id.income_category_spinner);

        String amountString = incomeAmountEditText.getText().toString();
        String dateString = incomeDateTextView.getText().toString();
        String categoryString = incomeCategorySpinner.getSelectedItem().toString();

        if (amountString.isEmpty() || dateString.isEmpty()) {
            Toast.makeText(AddIncomeActivity.this, "Please fill in the amount and date!", Toast.LENGTH_SHORT).show();
            return;
        }
        double amount = Double.parseDouble(amountString);
        WalletDB db = new WalletDB(AddIncomeActivity.this);
        db.addTransaction(amount, dateString, "income", categoryString);

        Toast.makeText(AddIncomeActivity.this, "Income added!", Toast.LENGTH_SHORT).show();
        finish();
    }
}