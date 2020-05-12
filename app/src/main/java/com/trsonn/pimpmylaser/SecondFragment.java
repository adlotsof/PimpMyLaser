package com.trsonn.pimpmylaser;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SecondFragment extends Fragment implements SensorEventListener {
    private static final String TAG = "SecondFragment";

    //Sensors and such
    private SensorManager mSensorManager;
    private Sensor mSensorAccelerometer;
    private Sensor mSensorMagnetometer;

    //the important values
    //current measured angle
    private float mAngle;
    //the last saved angle
    private float mSavedAngle;
    private int mDistance;
    private double mCalculatedHeight;

    //fields for the last recorded magnetometer /accelerometer datas
    float[] mAccelerometerData = new float[3];
    float[] mMagnetometerData = new float[3];

    //the views to be updated in every step of measurement
    private Button mButton;
    private TextView mTextAngle;
    private TextView mTextLabelInput;
    private EditText mEditText;
    private TextView mTextCalculatedHeight;

    //the display to calculate proper coordinate system for the device.
    //orientation will still be locked for easy of use
    Display mDisplay;

    //constant for the state of the measurement
    private static final int START = 0;
    private static final int ANGLE_SAVED = 1;
    private static final int CALCULATED_HEIGHT = 2;
    //array list for measurements
    ArrayList<Measurement> mMeasurements = new ArrayList<>();

    private int mCurrentState = START;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_second, container, false);


        return v;
    }

    public void onViewCreated(@NonNull View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        mButton = v.findViewById(R.id.button_second);
        mTextAngle = v.findViewById(R.id.currentAngle);
        mTextLabelInput = v.findViewById(R.id.distance_label);
        mEditText = v.findViewById(R.id.inputDistance);

        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });

        mTextCalculatedHeight = v.findViewById(R.id.calculatedHeight);

        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            mDisplay = wm.getDefaultDisplay();
        }

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mCurrentState) {
                    case 0:
                        mSavedAngle = mAngle;
                        mCurrentState = ANGLE_SAVED;
                        mTextAngle.setText(getResources().getString(R.string.current_angle, mSavedAngle) + " SAVED");
                        mEditText.setVisibility(View.VISIBLE);
                        mTextLabelInput.setVisibility(View.VISIBLE);
                        mButton.setText(R.string.save_distance_button);
                        break;
                    case 1:
                        if (mEditText.getText().toString().isEmpty()) {
                            Toast.makeText(getContext(), "CANNOT SAVE EMPTY NUMBER", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        mDistance = Integer.parseInt(mEditText.getText().toString());
                        mTextLabelInput.setText("Saved Distance: " + mDistance);
                        mEditText.setVisibility(View.INVISIBLE);
                        double sin = Math.sin(Math.toRadians(mSavedAngle));
                        mCalculatedHeight = sin * mDistance;
                        mTextCalculatedHeight.setText("Calculated height: \n" + mCalculatedHeight);
                        mTextCalculatedHeight.setVisibility(View.VISIBLE);
                        mButton.setText("restart");
                        mCurrentState = CALCULATED_HEIGHT;
                        Measurement currentMeasurement = new Measurement((float) mCalculatedHeight, (float) mDistance, (float) mSavedAngle, (System.currentTimeMillis() / 1000L));
                        mMeasurements.add(currentMeasurement);

                        break;
                    case 2:
                        mTextCalculatedHeight.setVisibility(View.INVISIBLE);
                        mTextLabelInput.setVisibility(View.INVISIBLE);
                        mTextLabelInput.setText(R.string.distance_input_label);
                        mButton.setText("Save angle");
                        mCurrentState = START;

                        break;
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mSensorAccelerometer != null) {
            mSensorManager.registerListener(this, mSensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorMagnetometer != null) {
            Log.d(TAG, "onStart: found magnetometer");
            mSensorManager.registerListener(this, mSensorMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mCurrentState == START) {

            int sensorType = event.sensor.getType();

//        Log.d(TAG, "onSensorChanged: sensor event fired");
            switch (sensorType) {
                case Sensor.TYPE_ACCELEROMETER:
//                Log.d(TAG, "onSensorChanged: accelerometer fires");
                    mAccelerometerData = event.values.clone();
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
//               Log.d(TAG, "onSensorChanged: magnetometer fires");
                    mMagnetometerData = event.values.clone();
                    break;
                default:
                    return;
            }
            float[] rotationMatrix = new float[9];

            boolean rotationOk = SensorManager.getRotationMatrix(rotationMatrix, null, mAccelerometerData, mMagnetometerData);
//        Log.d(TAG, "onSensorChanged: rotation is ok:"+rotationOk);
            float[] rotationMatrixAdjusted = new float[9];

            switch (mDisplay.getRotation()) {
                case Surface
                        .ROTATION_0:
                    rotationMatrixAdjusted = rotationMatrix.clone();
                    break;
                case Surface.ROTATION_90:
                    SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, rotationMatrixAdjusted);
                    break;
                case Surface.ROTATION_180:
                    SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y, rotationMatrixAdjusted);
                    break;
                case Surface.ROTATION_270:
                    SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X, rotationMatrixAdjusted);
                    break;
            }

            float orientationValues[] = new float[3];
            if (rotationOk) {
//            Log.d(TAG, "onSensorChanged: rotation ok");
                SensorManager.getOrientation(rotationMatrixAdjusted, orientationValues);
            }

            mAngle = (float) (orientationValues[1] * 180 / Math.PI * -1);
            mTextAngle.setText(getResources().getString(R.string.current_angle, mAngle));
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: called");

        super.onSaveInstanceState(outState);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mMeasurements != null && mMeasurements.size() > 0) {
            Log.d(TAG, "onSaveInstanceState: saving measurements, current size of mMeasuremetns:" + mMeasurements.size());
            ArrayList<Measurement> measurements = new ArrayList<>();
            SharedPreferences prefs = getActivity().getSharedPreferences(Measurement.PREFSFILE, 0);
            SharedPreferences.Editor editor = prefs.edit();
            Gson gson = new Gson();
            String jsonPreferences = prefs.getString(Measurement.PREFSKEY, null);
            if (jsonPreferences != null) {
                Type type = new TypeToken<ArrayList<Measurement>>() {
                }.getType();
                measurements = gson.fromJson(jsonPreferences, type);
                Log.d(TAG, "onSaveInstanceState: added from prefsfile " + measurements.size());
            }
            measurements.addAll(mMeasurements);
            Log.d(TAG, "onSaveInstanceState: all measurements to be saved: " + measurements.size());
            jsonPreferences = gson.toJson(measurements);
            editor.putString(Measurement.PREFSKEY, jsonPreferences);
            editor.apply();
        }
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
