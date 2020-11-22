package com.generictoaster.lambdatest;

import com.generictoaster.lambdatest.data.Stats;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StatHandlerTest {
    private static final double EPSILON = 0.0000001;
    private StatHandler handler;
    private Random rand;
    private List<Integer> valuesToTest;
    private DescriptiveStatistics stats;

    @BeforeAll
    public void setupClass() {
        handler = new StatHandler();
        rand = new Random();
    }

    @AfterAll
    public void tearDownClass() {
        handler = null;
    }

    @BeforeEach
    public void setupTest() {
        int numOfElements = rand.nextInt(1000);
        stats = new DescriptiveStatistics();
        valuesToTest = new ArrayList<>();

        for(int i = 0; i < numOfElements; i++) {
            int randoVal = rand.nextInt(100000);
            valuesToTest.add(randoVal);
            stats.addValue(randoVal);
        }
    }

    @AfterEach
    public void tearDownTest() {
        valuesToTest = null;
    }

    @Test
    void findMean() {
        double mean = handler.findMean(valuesToTest);
        double calcMean = stats.getMean();

        //doubles man??!!
        assertTrue(Math.abs(mean - calcMean) < EPSILON);
    }

    @Test
    void findMedian() {
        double median = handler.findMedian(valuesToTest);
        double realMedian = stats.getPercentile(50);

        assertEquals(realMedian, median);

        stats = new DescriptiveStatistics();
        //need to ensure the even case works
        if(valuesToTest.size() % 2 == 1) {
            valuesToTest.remove(0);
        } else { //also test odd case if it didn't get tested
            valuesToTest.add(rand.nextInt(100000));
        }

        for(int v : valuesToTest) {
            stats.addValue(v);
        }

        median = handler.findMedian(valuesToTest);
        realMedian = stats.getPercentile(50);

        assertEquals(realMedian, median);
    }

    /*
        I couldn't find a library that calculated the mode. In this case my general thought process is, if I
        implement the same thing 2 different ways and they match, then they're probably right.
     */
    @Test
    void findMode() {
        valuesToTest = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            valuesToTest.add(i);
        }

        valuesToTest.add(1);

        List<Integer> modes = handler.findMode(valuesToTest);
        List<Integer> realModes = getModes(valuesToTest);

        assertEquals(realModes, modes);

        valuesToTest = getModeTestList();

        modes = handler.findMode(valuesToTest);
        realModes = getModes(valuesToTest);

        assertEquals(realModes, modes);
    }

    @Test
    void getAllStats() {
        Stats mystats = handler.getAllStats(valuesToTest);
        double mean = stats.getMean();
        double median = stats.getPercentile(50);
        List<Integer> modes = getModes(valuesToTest);
        //doubles man??!!
        assertTrue(Math.abs(mean - mystats.getMean()) < EPSILON);
        assertEquals(median, mystats.getMedian());

        Collections.sort(modes);
        Collections.sort(mystats.getModes());
        assertEquals(modes, mystats.getModes());
    }

    @Test
    void getAllStatsOptimized() {
        Stats mystats = handler.getAllStatsOptimized(valuesToTest);
        double mean = stats.getMean();
        double median = stats.getPercentile(50);
        List<Integer> modes = getModes(valuesToTest);

        //doubles man??!!
        assertTrue(Math.abs(mean - mystats.getMean()) < EPSILON);
        assertEquals(median, mystats.getMedian());

        Collections.sort(modes);
        Collections.sort(mystats.getModes());
        assertEquals(modes, mystats.getModes());
    }

    private List<Integer> getModes(final List<Integer> numbers) {
        final Map<Integer, Long> countFrequencies = numbers.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        final long maxFrequency = countFrequencies.values().stream()
                .mapToLong(count -> count)
                .max().orElse(-1);

        return countFrequencies.entrySet().stream()
                .filter(tuple -> tuple.getValue() == maxFrequency)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private List<Integer> getModeTestList() {
        List<Integer> testList = new ArrayList<>();

        for(int i = 0; i < 10; i++) {
            testList.add(i);
        }

        for(int i = 0; i < 5; i++) {
            testList.add(i);
        }

        for(int i  = 0; i <= 2; i++) {
            testList.add(i);
        }

        return testList;
    }
}