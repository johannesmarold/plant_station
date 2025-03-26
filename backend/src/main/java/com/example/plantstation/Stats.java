package com.example.plantstation;

public class Stats {
    private float min;
    private float max;
    private float mean;

    public Stats(float min, float max, float mean) {
        this.min = min;
        this.max = max;
        this.mean = mean;
    }

    public float getMin() {
        return this.min;
    }

    public float getMax() {
        return this.max;
    }

    public float getMean() {
        return this.mean;
    }

}
