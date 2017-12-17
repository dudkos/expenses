package com.finance.expensesservice.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by siarh on 6/27/2017.
 */
public class ChartData {

    private List<BigDecimal> data;

    private String label;

    public ChartData(List<BigDecimal> data, String label) {
        this.data = data;
        this.label = label;
    }

    public List<BigDecimal> getData() {
        return data;
    }

    public void setData(List<BigDecimal> data) {
        this.data = data;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
