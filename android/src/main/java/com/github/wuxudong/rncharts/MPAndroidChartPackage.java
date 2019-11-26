package com.github.wuxudong.rncharts;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.github.wuxudong.rncharts.charts.BarChartManager;
import com.github.wuxudong.rncharts.charts.BubbleChartManager;
import com.github.wuxudong.rncharts.charts.CandleStickChartManager;
import com.github.wuxudong.rncharts.charts.CircularRadarArcChartManager;
import com.github.wuxudong.rncharts.charts.CircularRadarChartManager;
import com.github.wuxudong.rncharts.charts.CombinedChartManager;
import com.github.wuxudong.rncharts.charts.HorizontalBarChartManager;
import com.github.wuxudong.rncharts.charts.LineChartManager;
import com.github.wuxudong.rncharts.charts.PieChartManager;
import com.github.wuxudong.rncharts.charts.RadarChartManager;
import com.github.wuxudong.rncharts.charts.RoundedBarChartManager;
import com.github.wuxudong.rncharts.charts.RoundedHorizontalBarChartManager;
import com.github.wuxudong.rncharts.charts.ScatterChartManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MPAndroidChartPackage implements ReactPackage {

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        return Arrays.asList();
    }

    // Deprecated in RN 0.47
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Arrays.<ViewManager>asList(
                new BarChartManager(),
                new RoundedBarChartManager(),
                new HorizontalBarChartManager(),
                new RoundedHorizontalBarChartManager(),
                new BubbleChartManager(),
                new CandleStickChartManager(),
                new LineChartManager(),
                new PieChartManager(),
                new RadarChartManager(),
                new CircularRadarChartManager(),
                new CircularRadarArcChartManager(),
                new ScatterChartManager(),
                new CombinedChartManager()
        );
    }

}
