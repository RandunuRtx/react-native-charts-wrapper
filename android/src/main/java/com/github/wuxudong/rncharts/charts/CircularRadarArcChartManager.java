package com.github.wuxudong.rncharts.charts;


import android.util.Log;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.wuxudong.rncharts.data.DataExtract;
import com.github.wuxudong.rncharts.data.RadarDataExtract;
import com.github.wuxudong.rncharts.listener.RNOnChartGestureListener;
import com.github.wuxudong.rncharts.listener.RNOnChartValueSelectedListener;

public class CircularRadarArcChartManager extends YAxisChartBase<CircularRadarArcChart, RadarEntry> {

    @Override
    public String getName() {
        return "RNCircularRadarArcChart";
    }

    @Override
    protected CircularRadarArcChart createViewInstance(ThemedReactContext reactContext) {
        CircularRadarArcChart radarChart = new CircularRadarArcChart(reactContext);
        radarChart.setOnChartValueSelectedListener(new RNOnChartValueSelectedListener(radarChart));
        radarChart.setOnChartGestureListener(new RNOnChartGestureListener(radarChart));
        return radarChart;
    }

    @Override
    DataExtract getDataExtract() {
        return new RadarDataExtract();
    }

    @Override
    public void setYAxis(Chart chart, ReadableMap propMap) {
        CircularRadarArcChart radarChart = (CircularRadarArcChart) chart;
        YAxis axis = radarChart.getYAxis();
        setCommonAxisConfig(chart, axis, propMap);
        setYAxisConfig(axis, propMap);        
    }

    @ReactProp(name = "skipWebLineCount")
    public void setSkipWebLineCount(CircularRadarArcChart chart, int count) {
        chart.setSkipWebLineCount(count);
    }

    @ReactProp(name = "webLineWidth")
    public void setWebLineWidth(CircularRadarArcChart chart, float width) {
        chart.setWebLineWidth(width);
    }

    @ReactProp(name = "webLineWidthInner")
    public void setWebLineWidthInner(CircularRadarArcChart chart, float width) {
        chart.setWebLineWidthInner(width);
    }

    @ReactProp(name = "webAlpha")
    public void setWebAlpha(CircularRadarArcChart chart, int alpha) {
        chart.setWebAlpha(alpha);
    }

    @ReactProp(name = "webColor")
    public void setWebColor(CircularRadarArcChart chart, int color) {
        chart.setWebColor(color);
    }


    @ReactProp(name = "webColorInner")
    public void setWebColorInner(CircularRadarArcChart chart, int color) {
        chart.setWebColorInner(color);
    }

    @ReactProp(name = "drawWeb")
    public void setDrawWeb(CircularRadarArcChart chart, boolean enabled) {
        chart.setDrawWeb(enabled);
    }

    @ReactProp(name = "drawCircularWeb")
    public void setDrawCircularWeb(CircularRadarArcChart chart, boolean enabled) {
        chart.setDrawWeb(enabled);
    }

    @ReactProp(name = "minOffset")
    public void setMinOffset(CircularRadarArcChart chart, float minOffset) {
        chart.setMinOffset(minOffset);
    }

    @ReactProp(name = "rotationEnabled")
    public void setRotationEnabled(CircularRadarArcChart chart, boolean enabled) {
        chart.setRotationEnabled(enabled);
    }

    @ReactProp(name = "rotationAngle")
    public void setRotationAngle(CircularRadarArcChart chart, float angle) {
        chart.setRotationAngle(angle);
    }


}
