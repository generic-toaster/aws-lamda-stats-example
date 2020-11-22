package com.generictoaster.lambdatest.data;

import java.util.List;
import java.util.Objects;

public class Stats {
    private double mean;
    private double median;
    private List<Integer> modes;

    public Stats(double mean, double median, List<Integer> modes) {
        this.mean = mean;
        this.median = median;
        this.modes = modes;
    }

    public double getMean() {
        return mean;
    }

    public double getMedian() {
        return median;
    }

    public List<Integer> getModes() {
        return modes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stats stats = (Stats) o;
        return Double.compare(stats.mean, mean) == 0 &&
                Double.compare(stats.median, median) == 0 &&
                Objects.equals(modes, stats.modes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mean, median, modes);
    }
}
