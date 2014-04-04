package com.loadimpact.teamcity_plugin.eval;

import com.loadimpact.resource.test_result.StandardMetricResult;
import com.loadimpact.teamcity_plugin.Debug;
import com.loadimpact.teamcity_plugin.LoadTestResult;
import com.loadimpact.teamcity_plugin.Operator;
import com.loadimpact.util.ListUtils;

import java.util.List;

/**
 * DESCRIPTION
 *
 * @author jens
 */
public class Threshold {
    private final int                           id;
    private final StandardMetricResult.Metrics  metric;
    private final int                           thresholdValue;
    private final LoadTestResult                result;
    private final Operator                      operator;
    private final BoundedDroppingQueue<Integer> values;
    private       int                           lastOffset;
    private       int                           lastAggregatedValue;
    private       boolean                       lastExceededValue;

    public Threshold(int id, StandardMetricResult.Metrics metric, Operator operator, int thresholdValue, LoadTestResult result) {
        this.id = id;
        this.metric = metric;
        this.operator = operator;
        this.thresholdValue = thresholdValue;
        this.result = result;
        this.values = new BoundedDroppingQueue<Integer>();
        this.lastOffset = -1;
    }

    public int getId() { return id; }

    public StandardMetricResult.Metrics getMetric() { return metric; }

    public LoadTestResult getResult() { return result; }

    public int getAggregatedValue() {return ListUtils.median(values.toList());}

    public void accumulate(List<? extends StandardMetricResult> metricValues) {
        if (metricValues == null || metricValues.isEmpty()) return;
        for (StandardMetricResult v : metricValues) {
            if (lastOffset < v.offset) values.put(v.value.intValue());
        }
        lastOffset = ListUtils.last(metricValues).offset;
    }

    public boolean isExceeded() {
        lastAggregatedValue = getAggregatedValue();
        lastExceededValue = false;
        switch (operator) {
            case lessThan:
                lastExceededValue = (lastAggregatedValue < thresholdValue);
                break;
            case greaterThan: lastExceededValue = (lastAggregatedValue > thresholdValue);
                break;
        }
        return lastExceededValue;
    }

    @Override
    public String toString() {
        return "Threshold[id:" + id
                + ", metric:" + metric.name()
                + ", aggregatedValue:" + lastAggregatedValue
                + ", thresholdValue:" + thresholdValue
                + ", operator:" + operator.symbol
                + ", result=" + result
                + ", lastExceededValue:" + lastExceededValue
                + ", lastOffset:" + lastOffset
                + "]";
    }

    public String getReason() {
        if (lastExceededValue) {
            return String.format("Metric '%s' has aggregated-value=%d %s %d as threshold", metric.name(), lastAggregatedValue, operator.symbol, thresholdValue);
        } else {
            return String.format("Metric %s: aggregated-value=%d", metric.name(), lastAggregatedValue);
        }
    }
}
