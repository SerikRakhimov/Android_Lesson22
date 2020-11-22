package com.example.ball;


import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


public class MainActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private Timer timer;
    TextView textView;
    ImageView imageView;
    ConstraintLayout.LayoutParams param;
    private StringBuilder stringBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        param = (ConstraintLayout.LayoutParams) imageView.getLayoutParams();


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        stringBuilder = new StringBuilder();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(listener, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showInfo();
                    }
                });
            }
        };
        timer.schedule(task, 0, 400);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listener);
        timer.cancel();
    }

    private String formatTwo(int values[]) {
        return String.format("%d\t\t%d", values[0], values[1]);
    }

    private String formatThree(float values[]) {
        return String.format("%1$.1f\t\t%2$.1f\t\t%3$.1f", values[0], values[1], values[2]);
    }

    private float[] valuesAccelerometer = new float[3];

    private void showInfo() {
        int[] coordsBall = getCoordsBall(valuesAccelerometer[0], valuesAccelerometer[1]);
        stringBuilder.setLength(0);
        stringBuilder.append("Accelerometer: " + formatThree(valuesAccelerometer))
                .append("\nx, y = " + formatTwo(coordsBall));
        textView.setText(stringBuilder.toString());
        param.setMargins(coordsBall[0], coordsBall[1], 0, 0);
        imageView.setLayoutParams(param);
    }

    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    for (int i = 0; i < 3; i++) {
                        valuesAccelerometer[i] = event.values[i];
                    }
                    break;

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

    };

    private int[] getCoordsBall(float a, float b) {
        int[] screenSize = getScreenSIze();
        int widthScreen = (int) Math.round(0.9 * screenSize[0]);
        int heightScreen = (int) Math.round(0.9 * screenSize[1]);
        float max = 9.8f;
        int x = (int) Math.round(((-a) / max) * widthScreen + widthScreen / 2);
        if (x < 0) {
            x = 0;
        } else if (x > widthScreen) {
            x = widthScreen;
        }
        int y = (int) Math.round((b / max) * heightScreen + heightScreen / 2);
        if (y < 0) {
            y = 0;
        } else if (y > heightScreen) {
            y = heightScreen;
        }
        int[] result = {x, y};
        return result;
    }

    private int[] getScreenSIze() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int h = displaymetrics.heightPixels;
        int w = displaymetrics.widthPixels;

        int[] size = {w, h};
        return size;

    }
}