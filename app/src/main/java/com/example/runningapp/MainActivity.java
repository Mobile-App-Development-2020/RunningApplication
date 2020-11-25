/* #############################
 *
 * Author: Johnathon Mc Grory
 * Date : 22 November 2020
 * Description : Running App MainActivity page Java Code
 *
 * ############################# */

package com.example.runningapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.media.MediaPlayer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


//region $OnCreate
    public class MainActivity extends AppCompatActivity implements SensorEventListener {

    //region $GlobalVariables
        // experimental values for hi and lo magnitude limits
        private final double HI_STEP = 11.0;     // upper mag limit
        private final double LO_STEP = 8.0;      // lower mag limit
        boolean highLimit = false;      // detect high limit

        TextView tvSteps;
        private SensorManager mSensorManager;
        private Sensor mSensor;

        CountUpTimer timer;
        int seconds, minutes, hours, counter = 0;
        TextView tvSecs, tvMinutes, tvHrs;
        MediaPlayer player;
        boolean runInProgress, runPaused, runReset;
    //endregion $GlobalVariables

        //region $OnCreate
        @Override
        protected void onCreate(Bundle savedInstanceState) {

            //standard pre-packaged code
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            //storing our textViews locally for use
            tvSecs = findViewById(R.id.tvSeconds);
            tvMinutes = findViewById(R.id.tvMins);
            tvHrs = findViewById(R.id.tvHours);
            tvSteps = findViewById(R.id.tvSummarySteps);

            //setting booleans to their starting value
            runInProgress = false;
            runReset = true;

            // we are going to use the sensor service. Get the entire sensor manager stored as a variable and then choose the sensor type of accelerometer
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            //region $Counter timer created
            //ADDED EXTRA SIMPLE LOGIC TO MAKE IT AN HOURS MINUTES AND SECONDS TIMER INSTEAD OF JUST SECONDS
            timer = new CountUpTimer(300000) {  // should be high for the run (ms)
                public void onTick(int second) {
                    if (second >= 60) {
                        if (minutes < 60) {
                            tvSecs.setText("0");
                            minutes++;
                            tvMinutes.setText(String.valueOf(minutes));
                        } else {
                            tvMinutes.setText("0");
                            minutes = 0;
                            hours++;
                            tvHrs.setText(String.valueOf(hours));
                        }
                        timer.cancel();
                        timer.start();
                    }
                    seconds = second;
                    tvSecs.setText(String.valueOf(second));
                }
            };
            //endregion $Counter
        }
        //endregion $OnCreate


        //region $Buttons
            //start the run
            public void doStart(View view) {
                if (runInProgress != true && runReset == true) {
                    ResetAll();
                    timer.start();
                    Toast.makeText(this, "Run Started", Toast.LENGTH_LONG).show();
                    PlayStart();
                    runInProgress = true;
                    runReset = false;
                } else {
                    Toast.makeText(this, "There is already a run in progress!         Reset to start a new run", Toast.LENGTH_LONG).show();
                }
            }

            //stop the run button
            public void doStop(View view) {

                timer.cancel();
                Toast.makeText(this, "Stopped Run", Toast.LENGTH_LONG).show();
                PlayStop();
                runInProgress = false;
                runPaused = true;

            }

            //reset everything button
            public void doReset(View view) {
                if (runPaused == true && runInProgress == false) {
                    ResetAll();
                    Toast.makeText(this, "Reset", Toast.LENGTH_LONG).show();
                    runReset = true;
                    PlayReset();
                }
            }
        //endregion $Buttons

    //method to reset all textViews and variables in order to begin a new run
        private void ResetAll() {
            tvSecs.setText("0");
            tvMinutes.setText("0");
            tvHrs.setText("0");
            hours = 0;
            minutes = 0;
            counter = 0;
            tvSteps.setText(String.valueOf(counter));
        }

        //region $MediaPlayers
            private void PlayStart() {
                player = MediaPlayer.create(this, R.raw.start);
                player.start();
            }

            private void PlayStop() {
                player = MediaPlayer.create(this, R.raw.complete);
                player.start();
            }

            private void PlayReset() {
                player = MediaPlayer.create(this, R.raw.reset);
                runInProgress = false;
                player.start();
            }
        //endregion $MediaPlayers


        // When the app is brought to the foreground - using app on screen
        protected void onResume() {
            super.onResume();
            // turn on the sensor
            mSensorManager.registerListener(this, mSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        //App running but not on screen - in the background
        protected void onPause() {
            super.onPause();
            mSensorManager.unregisterListener(this);    // turn off listener to save power
        }


        @Override
        public void onSensorChanged(SensorEvent event) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];


            // get a magnitude number using Pythagoras's Theorem
            double mag = round(Math.sqrt((x * x) + (y * y) + (z * z)), 2);


            // for me if msg > 11 and then drops below 9, we have a step
            // you need to do your own mag calculating
            if ((mag > HI_STEP) && (highLimit == false)) {
                highLimit = true;
            }
            if ((mag < LO_STEP) && (highLimit == true)) {
                // we have a step
                counter++;
                if (runInProgress == true)
                {
                    tvSteps.setText(String.valueOf(counter));
                    highLimit = false;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // not used
        }

        public static double round(double value, int places) {
            if (places < 0) throw new IllegalArgumentException();

            long factor = (long) Math.pow(10, places);
            value = value * factor;
            long tmp = Math.round(value);
            return (double) tmp / factor;
        }

    public void doSummary(View view) {
        //create a new intent
            Intent summary = new Intent(view.getContext(), SummaryActivity.class);

        //pass the values
            summary.putExtra("Steps", counter);
            summary.putExtra("Seconds", seconds);

        //starts the new page
            startActivity(summary);
    }
}