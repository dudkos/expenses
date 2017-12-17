package com.finance.expensesservice.dto;

import java.util.List;

/**
 * Created by siarh on 7/2/2017.
 */
public class Chart {

    private List<ChartData> chartData;

    private List<String> labels;

    public List<ChartData> getChartData() {
        return chartData;
    }

    public void setChartData(List<ChartData> chartData) {
        this.chartData = chartData;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
}
