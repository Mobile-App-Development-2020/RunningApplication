/* #############################
 *
 * Author: Johnathon Mc Grory
 * Date : 22 November 2020
 * Description : Running App SummaryActivity page Java Code
 *
 * ############################# */

package com.example.runningapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SummaryActivity extends AppCompatActivity {

    double calories, meters;
    int steps, seconds;
    TextView Seconds, Calories, Meters, Steps, tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        steps = getIntent().getIntExtra("Steps", -1);
        seconds = getIntent().getIntExtra("Seconds", -1);

        Seconds = findViewById(R.id.tvSeconds);
        Calories = findViewById(R.id.tvCalories);
        Meters = findViewById(R.id.tvMeters);
        Steps = findViewById(R.id.tvSummarySteps);
        tvDate = findViewById(R.id.tvDate);

        calories = steps * 0.04;
        meters = steps * 0.8;
        meters = Math.round(meters * 100.0) / 100.0;
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        Seconds.setText(String.valueOf(seconds) + "  seconds");
        Calories.setText(String.valueOf(calories) + "  cal");
        Meters.setText(String.valueOf(meters) + "  meters");
        Steps.setText(String.valueOf(steps) + "  steps");
        tvDate.setText(formattedDate);


    }

    public void doExit(View view) {
        finish();
    }
}