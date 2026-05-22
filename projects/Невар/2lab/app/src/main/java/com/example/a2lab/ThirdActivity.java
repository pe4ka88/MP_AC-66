package com.example.a2lab;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;

public class ThirdActivity extends AppCompatActivity {

    private static final String TAG = "ThirdActivity";
    private static final int REQUEST_FROM_MAP = 1;
    private static final int REQUEST_TO_MAP = 2;

    private EditText editTextFrom;
    private EditText editTextTo;
    private Button buttonSelectFromMap;
    private Button buttonSelectToMap;
    private Button buttonSelectDate;
    private Button buttonSelectTime;
    private RadioGroup radioGroupTimeType;
    private NumberPicker numberPickerPassengers;
    private EditText editTextComment;
    private Button buttonOk;
    private Button buttonCancel;

    private String selectedDate = "";
    private String selectedTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        Log.d(TAG, "onCreate вызван");

        initViews();
        setupNumberPicker();
        setupListeners();
    }

    private void initViews() {
        editTextFrom = findViewById(R.id.editTextFrom);
        editTextTo = findViewById(R.id.editTextTo);
        buttonSelectFromMap = findViewById(R.id.buttonSelectFromMap);
        buttonSelectToMap = findViewById(R.id.buttonSelectToMap);
        buttonSelectDate = findViewById(R.id.buttonSelectDate);
        buttonSelectTime = findViewById(R.id.buttonSelectTime);
        radioGroupTimeType = findViewById(R.id.radioGroupTimeType);
        numberPickerPassengers = findViewById(R.id.numberPickerPassengers);
        editTextComment = findViewById(R.id.editTextComment);
        buttonOk = findViewById(R.id.buttonOk);
        buttonCancel = findViewById(R.id.buttonCancel);
    }

    private void setupNumberPicker() {
        numberPickerPassengers.setMinValue(1);
        numberPickerPassengers.setMaxValue(3);
        numberPickerPassengers.setValue(1);
        numberPickerPassengers.setWrapSelectorWheel(false);
    }

    private void setupListeners() {
        buttonSelectFromMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapForLocation(true);
            }
        });

        buttonSelectToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapForLocation(false);
            }
        });

        buttonSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        radioGroupTimeType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioAsap) {
                    buttonSelectTime.setEnabled(false);
                    selectedTime = getString(R.string.as_soon_as_possible);
                } else {
                    buttonSelectTime.setEnabled(true);
                }
            }
        });

        buttonSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAndReturn();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void openMapForLocation(boolean isFromPoint) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("isForFromPoint", isFromPoint);
        startActivityForResult(intent, isFromPoint ? REQUEST_FROM_MAP : REQUEST_TO_MAP);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth);
                        buttonSelectDate.setText(getString(R.string.date) + ": " + selectedDate);

                        Calendar selectedCal = Calendar.getInstance();
                        selectedCal.set(year, month, dayOfMonth);
                        Calendar today = Calendar.getInstance();
                        today.set(Calendar.HOUR_OF_DAY, 0);
                        today.set(Calendar.MINUTE, 0);
                        today.set(Calendar.SECOND, 0);

                        if (selectedCal.before(today)) {
                            Toast.makeText(ThirdActivity.this,
                                    R.string.error_date_past, Toast.LENGTH_SHORT).show();
                            selectedDate = "";
                            buttonSelectDate.setText(R.string.select_date);
                        }
                    }
                }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                        buttonSelectTime.setText(getString(R.string.time) + ": " + selectedTime);
                    }
                }, hour, minute, true);

        timePickerDialog.show();
    }

    private void saveAndReturn() {
        String from = editTextFrom.getText().toString().trim();
        String to = editTextTo.getText().toString().trim();

        if (from.isEmpty()) {
            editTextFrom.setError(getString(R.string.enter_from));
            return;
        }

        if (to.isEmpty()) {
            editTextTo.setError(getString(R.string.enter_to));
            return;
        }

        if (selectedDate.isEmpty()) {
            Toast.makeText(this, R.string.select_date, Toast.LENGTH_SHORT).show();
            return;
        }

        int checkedId = radioGroupTimeType.getCheckedRadioButtonId();
        String timeInfo;

        if (checkedId == R.id.radioAsap) {
            timeInfo = getString(R.string.as_soon_as_possible);
        } else {
            if (selectedTime.isEmpty()) {
                Toast.makeText(this, R.string.select_time, Toast.LENGTH_SHORT).show();
                return;
            }
            timeInfo = selectedTime;
        }

        RouteData routeData = new RouteData(
                from,
                to,
                selectedDate,
                timeInfo,
                String.valueOf(numberPickerPassengers.getValue()),
                editTextComment.getText().toString()
        );

        Intent resultIntent = new Intent();
        resultIntent.putExtra("routeData", routeData);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            String address = data.getStringExtra("address");
            if (requestCode == REQUEST_FROM_MAP) {
                editTextFrom.setText(address);
            } else if (requestCode == REQUEST_TO_MAP) {
                editTextTo.setText(address);
            }
        }
    }
}