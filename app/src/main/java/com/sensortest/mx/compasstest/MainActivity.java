package com.sensortest.mx.compasstest;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

    /*
        TODO
        - low pass with sind, cos, atan2
        - Use BRIR instead of HRTF
        - Wait for player to load complete
        */

    // INIT GLOBAL
        // SENSOR
        private SensorManager mSensorManager;
        Sensor accelerometer;
        Sensor magnetometer;
        long azimut;  // angle difference to north pole
        float alpha = 0.5f; // Smoothing factor
        long lastUpdate;

        // Text
        TextView firstTextView;

        // AUDIO
        SoundPool mySound;
        int[] IDs = new int[361];

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // BUTTON
        setContentView(R.layout.activity_main);

        final TextView firstTextView = (TextView) findViewById(R.id.textView);

        Button firstButton = (Button) findViewById(R.id.firstButton);

        firstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ANGLE TEST
                firstTextView.setText(String.valueOf(azimut));

            }
        });

        // SENSORS
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // AUDIO
        // Init SoundPool
        mySound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        // Load all files
        for (int i = 0; i < 361; i++) {
            IDs[i] = this.getResources().getIdentifier("sound" + i, "raw", getPackageName());
            mySound.load(this, IDs[i], 1);
        }
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {  }

    float[] mGravity;
    float[] mGeomagnetic;
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = lowPass(event.values.clone(), mGravity);
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = lowPass(event.values.clone(), mGeomagnetic);
        }
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);

                // counter-clockwise, North = 0°
                //azimut = Math.round(-orientation[0] * 360 / (2 * Math.PI)); // orientation contains: azimut, pitch and roll
                //if (azimut < 0){
                //    azimut = 360 + azimut;
                //}

                // counter-clockwise, North = 180°
                //azimut = Math.round(-orientation[0] * 360 / (2 * Math.PI)) + 180; // orientation contains: azimut, pitch and roll

                // clockwise, North = 0°
                //azimut = Math.round(-orientation[0] * 360 / (2 * Math.PI)); // orientation contains: azimut, pitch and roll
                //if (azimut < 0) {
                //    azimut = -1 * azimut;
                //} else {
                //    azimut = 360 - azimut;
                //}

                // clockwise, North = 180°
                azimut = Math.round(-orientation[0] * 360 / (2 * Math.PI)); // orientation contains: azimut, pitch and roll
                if (azimut < 0) {
                    azimut = -1 * azimut + 180;
                } else {
                    azimut = 180 - azimut;
                }

                // Get time
                long curTime = System.currentTimeMillis();

                // Compare time and trigger sound
                if ((curTime - lastUpdate) > 100) {
                    mySound.play((int) (long) azimut, 1, 1, 1, 0, 1);
                    lastUpdate = curTime;
                }
            }
        }
    }

    // Low pass
    protected float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;

        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + alpha * (input[i] - output[i]);
        }
        return output;
    }
}

