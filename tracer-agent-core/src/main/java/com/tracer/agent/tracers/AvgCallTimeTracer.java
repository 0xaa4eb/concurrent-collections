package com.tracer.agent.tracers;

import com.tracer.agent.util.Duration;
import org.HdrHistogram.Histogram;

import java.util.concurrent.TimeUnit;

public class AvgCallTimeTracer {

    private Histogram histogram;
    private Histogram unusedHistogram;

    public AvgCallTimeTracer() {
        this.histogram = new Histogram(1, TimeUnit.MINUTES.toNanos(5), 5);
        this.unusedHistogram = new Histogram(1, TimeUnit.MINUTES.toNanos(5), 5);
    }

    public void record(long nanoTime) {
        histogram.recordValue(nanoTime);
    }

    public boolean hasSomething() {
        return this.histogram.getTotalCount() > 0L;
    }

    public String report() {
        return "count: " + histogram.getTotalCount() +
                ", median: " + new Duration(histogram.getValueAtPercentile(50.0)) +
                ", p90: " + new Duration(histogram.getValueAtPercentile(90.0)) +
                ", p99: " + new Duration(histogram.getValueAtPercentile(99.0)) +
                ", max: " + new Duration(histogram.getMaxValue());
    }

    public void reset() {
        Histogram currentHistogram = this.histogram;
        this.histogram = this.unusedHistogram;
        this.unusedHistogram = currentHistogram;
        this.histogram.reset();
    }
}
