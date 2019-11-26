package com.github.wuxudong.rncharts.charts;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.wuxudong.rncharts.data.BarDataExtract;
import com.github.wuxudong.rncharts.data.DataExtract;
import com.github.wuxudong.rncharts.listener.RNOnChartGestureListener;
import com.github.wuxudong.rncharts.listener.RNOnChartValueSelectedListener;

public class RoundedBarChartManager extends BarLineChartBaseManager<RoundedBarChart, BarEntry> {

    @Override
    public String getName() {
        return "RNRoundedBarChart";
    }

    @Override
    protected RoundedBarChart createViewInstance(ThemedReactContext reactContext) {
        RoundedBarChart barChart = new RoundedBarChart(reactContext);
        barChart.setOnChartValueSelectedListener(new RNOnChartValueSelectedListener(barChart));
        barChart.setOnChartGestureListener(new RNOnChartGestureListener(barChart));
        return barChart;
    }

    @Override
    DataExtract getDataExtract() {
        return new BarDataExtract();
    }

    @ReactProp(name = "drawValueAboveBar")
    public void setDrawValueAboveBar(RoundedBarChart chart, boolean enabled) {
        chart.setDrawValueAboveBar(enabled);
    }

    @ReactProp(name = "drawBarShadow")
    public void setDrawBarShadow(RoundedBarChart chart, boolean enabled) {
        chart.setDrawBarShadow(enabled);
    }

    @ReactProp(name = "highlightFullBarEnabled")
    public void setHighlightFullBarEnabled(RoundedBarChart chart, boolean enabled) {
        chart.setHighlightFullBarEnabled(enabled);
    }
}