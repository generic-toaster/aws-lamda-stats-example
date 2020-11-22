package com.generictoaster.lambdatest;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.generictoaster.lambdatest.data.Stats;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;

public class StatHandler {
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    /**
     * Fine the <a href="https://www.mathsisfun.com/mean.html">mean</a> of a list of integers
     *
     * @param values list of intergers
     * @return the mean
     */
    public double findMean(List<Integer> values) {
        validateInput(values);

        double mean = 0;

        for(double val : values) {
            mean += val;
        }

        mean /= (values.size() * 1.0);

        return mean;
    }

    /**
     * Fine the <a href="https://www.mathsisfun.com/median.html">median</a> of a list of integers
     *
     * @param values list of integers
     * @return the median value
     */
    public double findMedian(List<Integer> values) {
        validateInput(values);

        Collections.sort(values);

        //if we're odd ;) just return the middle
        if(values.size() % 2 == 1)
            return values.get(values.size() / 2);

        //if we're even, average the "middles"
        int firstMedian = values.get(values.size() / 2);
        int secondMedian = values.get((values.size() / 2) - 1);

        return (firstMedian + secondMedian) / 2.0;
    }

    /**
     * Find the <a href="https://www.mathsisfun.com/mode.html">mode</a> of a list of numbers
     * @param values list of integers
     * @return the mode in a list in case it's multimodal
     */
    public List<Integer> findMode(List<Integer> values) {
        validateInput(values);

        Map<Integer, Integer> valueCount = new HashMap<>();

        for(int val : values) {
            if( !valueCount.containsKey(val)) {
                valueCount.put(val, 1);
            } else {
                valueCount.put(val, valueCount.get(val) + 1);
            }
        }

        int maxCount = Collections.max(valueCount.values());
        List<Integer> modes = new ArrayList<>();

        for(Map.Entry<Integer, Integer> entry : valueCount.entrySet()) {
            if(entry.getValue() == maxCount) {
                modes.add(entry.getKey());
            }
        }

        Collections.sort(modes);

        return modes;
    }

    /**
     * Collect all the stats (<a href="https://www.mathsisfun.com/mean.html">mean</a>,
     *                        <a href="https://www.mathsisfun.com/median.html">median</a>,
     *                        <a href="https://www.mathsisfun.com/mode.html">mode</a>, package them in a POJO and return.
     * While not optimized, this is the cleanest way to implement and would easier to maintain.
     * @param values list of numbers
     * @return - the mean, median and mode
     */
    public Stats getAllStats(List<Integer> values) {
        validateInput(values);

        double mean = this.findMean(values);
        double median = this.findMedian((values));
        List<Integer> modes = this.findMode(values);

        return new Stats(mean, median, modes);
    }

    /**
     * So what if we WANT/NEED it to be fast??!! This method does just that, but it is UGLY! It also
     * breaks a couple of OO design principles, but sometimes we do that when we need it to be as fast as possible.
     *
     * @param values list of numbers
     * @return - the mean, median and mode
     */
    public Stats getAllStatsOptimized(List<Integer> values) {
        validateInput(values);

        Collections.sort(values);
        double median = (values.size() % 2 == 1 ? values.get(values.size() / 2) :
                ((values.get(values.size() / 2)) + (values.get(((values.size() / 2) - 1)))) / 2.0);
        double mean = 0;
        Map<Integer, Integer> valueCount = new HashMap<>();

        for(int val : values) {
            mean += val;

            if(valueCount.containsKey(val)) {
                valueCount.put(val, valueCount.get(val) + 1);
            } else {
                valueCount.put(val, 1);
            }
        }

        mean /= (values.size() * 1.0);

        int max = Collections.max(valueCount.values());
        List<Integer> modes = new ArrayList<>();

        for(Map.Entry<Integer, Integer> entry : valueCount.entrySet()) {
            if( entry.getValue() == max ) {
                modes.add(entry.getKey());
            }
        }

        //no final of the mode because WE NEED SPEED!!!
        return new Stats(mean, median, modes);
    }

    private void validateInput(List<Integer> values) {
        if(values == null || values.size() == 0)
            throw new IllegalArgumentException();
    }

    /**
     * Exposed function for aws lambda
     *
     * @param input
     * @param context
     * @return
     */
    public Stats handler(List<Integer> input, Context context) {
        LambdaLogger logger = context.getLogger();

        Stats returnVal = getAllStats(input);

        // process input
        logger.log("EVENT: " + gson.toJson(input));
        logger.log("RETURN: " + gson.toJson(returnVal));

        return returnVal;
    }
}
