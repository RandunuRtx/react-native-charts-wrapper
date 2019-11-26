package com.github.wuxudong.rncharts.charts;

import com.facebook.react.uimanager.ThemedReactContext;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.wuxudong.rncharts.listener.RNOnChartGestureListener;
import com.github.wuxudong.rncharts.listener.RNOnChartValueSelectedListener;

public class RoundedHorizontalBarChartManager extends BarChartManager {

    @Override
    public String getName() {
        return "RNRoundedHorizontalBarChart";
    }

    @Override
    protected RoundedHorizontalBarChart createViewInstance(ThemedReactContext reactContext) {
        RoundedHorizontalBarChart horizontalBarChart = new RoundedHorizontalBarChart(reactContext);
        horizontalBarChart.setOnChartValueSelectedListener(new RNOnChartValueSelectedListener(horizontalBarChart));
        horizontalBarChart.setOnChartGestureListener(new RNOnChartGestureListener(horizontalBarChart));
        return horizontalBarChart;
    }
}