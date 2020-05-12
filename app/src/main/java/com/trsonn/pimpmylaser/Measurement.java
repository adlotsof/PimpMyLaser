package com.trsonn.pimpmylaser;

public class Measurement {
    public static final String PREFSFILE = "com.trsonn.pimpmylaser.prefs";
    public static final String PREFSKEY = "MEASUREMENTS_LIST";
    private float mCalculatedHeight;
    private float mMeasuredDistance;
    private float mAngle;
    private long mTime;

    public Measurement(float calculatedHeight, float measuredDistance, float angle, long time) {
        mCalculatedHeight = calculatedHeight;
        mMeasuredDistance = measuredDistance;
        mAngle = angle;
        mTime = time;
    }

    public float getCalculatedHeight() {
        return mCalculatedHeight;
    }

    public void setCalculatedHeight(float calculatedHeight) {
        mCalculatedHeight = calculatedHeight;
    }

    public float getMeasuredDistance() {
        return mMeasuredDistance;
    }

    public void setMeasuredDistance(float measuredDistance) {
        mMeasuredDistance = measuredDistance;
    }

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public long getTime() {
        return mTime;
    }
}
