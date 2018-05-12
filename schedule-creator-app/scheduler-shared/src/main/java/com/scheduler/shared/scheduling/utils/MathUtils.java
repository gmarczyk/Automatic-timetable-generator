package com.scheduler.shared.scheduling.utils;

import java.util.Arrays;
import java.util.List;

public class MathUtils {

    public static Double getMean(Double data[])
    {
        double sum = 0.0;
        for(double a : data)
            sum += a;
        return sum/data.length;
    }

    public static Double getVariance(Double data[])
    {
        double mean = getMean(data);
        double temp = 0;
        for(double a :data)
            temp += (a-mean)*(a-mean);
        return temp/(data.length);
    }

    public static Double getStdDev(Double data[])
    {
        return Math.sqrt(getVariance(data));
    }

    public static Double median(double data[])
    {
        Arrays.sort(data);

        if (data.length % 2 == 0)
        {
            return (data[(data.length / 2) - 1] + data[data.length / 2]) / 2.0;
        }
        return data[data.length / 2];
    }

    public static Double sum(final List<Double> data) {
        Double sum = new Double(0);
        for (final Double datum : data) {
            sum+= datum;
        }
        return sum;
    }
}
