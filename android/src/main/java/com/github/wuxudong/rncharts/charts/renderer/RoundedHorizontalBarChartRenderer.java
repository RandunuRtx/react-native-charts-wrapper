package com.github.wuxudong.rncharts.charts.renderer;

import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.buffer.HorizontalBarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.dataprovider.ChartInterface;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.HorizontalBarChartRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.HashMap;
import java.util.List;

public class RoundedHorizontalBarChartRenderer extends HorizontalBarChartRenderer {
    public RoundedHorizontalBarChartRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    private float mRadius = 5f;

    /**
     * bar corner radius automatically assigned to barWidth/4
     *
     * @param mRadius
     */
    private void setmRadius(float mRadius) {
        this.mRadius = mRadius;
    }

    @Override
    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());
        mShadowPaint.setColor(dataSet.getBarShadowColor());
        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        // initialize the buffer
        BarBuffer buffer = mBarBuffers[index];
        buffer.setPhases(phaseX, phaseY);
        buffer.setDataSet(index);
        buffer.setBarWidth(mChart.getBarData().getBarWidth());
        buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));
        buffer.feed(dataSet);
        trans.pointValuesToPixel(buffer.buffer);

        /*
        set bar corner radius
        @TODO improve this
        */

        if (buffer.buffer.length > 2) {
            float barCornerRadius = Math.abs(buffer.buffer[2] - buffer.buffer[0]) / 4;
            mRadius = barCornerRadius;
        }

        // if multiple colors
        if (dataSet.getColors().size() > 1) {

            for (int j = 0; j < buffer.size(); j += 4) {

                if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                    continue;

                if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                    break;

                if (mChart.isDrawBarShadowEnabled()) {
                    if (mRadius > 0)
                        c.drawRoundRect(new RectF(buffer.buffer[j], mViewPortHandler.contentTop(),
                                buffer.buffer[j + 2],
                                mViewPortHandler.contentBottom()), mRadius, mRadius, mShadowPaint);
                    else
                        c.drawRect(buffer.buffer[j], mViewPortHandler.contentTop(),
                                buffer.buffer[j + 2],
                                mViewPortHandler.contentBottom(), mShadowPaint);
                }

                // Set the color for the currently drawn value. If the index
                // is
                // out of bounds, reuse colors.
                mRenderPaint.setColor(dataSet.getColor(j / 4));
                if (mRadius > 0)
                    c.drawRoundRect(new RectF(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3]), mRadius, mRadius, mRenderPaint);
                else
                    c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3], mRenderPaint);
            }
        } else {

            mRenderPaint.setColor(dataSet.getColor());

            for (int j = 0; j < buffer.size(); j += 4) {

                if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                    continue;

                if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                    break;

                if (mChart.isDrawBarShadowEnabled()) {
                    if (mRadius > 0)
                        c.drawRoundRect(new RectF(buffer.buffer[j], mViewPortHandler.contentTop(),
                                buffer.buffer[j + 2],
                                mViewPortHandler.contentBottom()), mRadius, mRadius, mShadowPaint);
                    else
                        c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                                buffer.buffer[j + 3], mRenderPaint);
                }

                if (mRadius > 0)
                    c.drawRoundRect(new RectF(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3]), mRadius, mRadius, mRenderPaint);
                else
                    c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3], mRenderPaint);
            }
        }
    }

    @Override
    public void drawValues(Canvas c) {
        // if values are drawn
        if (isDrawingValuesAllowed(mChart)) {

            List<IBarDataSet> dataSets = mChart.getBarData().getDataSets();

            final float valueOffsetPlus = Utils.convertDpToPixel(5f);
            float posOffset = 0f;
            float negOffset = 0f;
            final boolean drawValueAboveBar = mChart.isDrawValueAboveBarEnabled();

            for (int i = 0; i < mChart.getBarData().getDataSetCount(); i++) {

                IBarDataSet dataSet = dataSets.get(i);

                if (!shouldDrawValues(dataSet))
                    continue;

                boolean isInverted = mChart.isInverted(dataSet.getAxisDependency());

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);
                final float halfTextHeight = Utils.calcTextHeight(mValuePaint, "10") / 2f;

                ValueFormatter formatter = dataSet.getValueFormatter();

                // get the buffer
                BarBuffer buffer = mBarBuffers[i];

                final float phaseY = mAnimator.getPhaseY();

                MPPointF iconsOffset = MPPointF.getInstance(dataSet.getIconsOffset());
                iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x);
                iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y);

                // if only single values are drawn (sum)
                if (!dataSet.isStacked()) {

                    for (int j = 0; j < buffer.buffer.length * mAnimator.getPhaseX(); j += 4) {

                        float y = (buffer.buffer[j + 1] + buffer.buffer[j + 3]) / 2f;

                        if (!mViewPortHandler.isInBoundsTop(buffer.buffer[j + 1]))
                            break;

                        if (!mViewPortHandler.isInBoundsX(buffer.buffer[j]))
                            continue;

                        if (!mViewPortHandler.isInBoundsBottom(buffer.buffer[j + 1]))
                            continue;

                        BarEntry entry = dataSet.getEntryForIndex(j / 4);
                        float val = entry.getY();
                        HashMap<String, String> entryDataMap = (HashMap<String, String>) entry.getData();

                        String marker = entryDataMap.containsKey("marker") ? entryDataMap.get("marker") : "";
                        marker =  val > 0.0f ? marker : " ";
                        String formattedValue = marker.length() > 0 ? marker : formatter.getBarLabel(entry);

                        // calculate the correct offset depending on the draw position of the value
                        float valueTextWidth = Utils.calcTextWidth(mValuePaint, formattedValue);
                        posOffset = (drawValueAboveBar ? valueOffsetPlus : -(valueTextWidth + valueOffsetPlus));
                        negOffset = (drawValueAboveBar ? -(valueTextWidth + valueOffsetPlus) : valueOffsetPlus);

                        if (isInverted) {
                            posOffset = -posOffset - valueTextWidth;
                            negOffset = -negOffset - valueTextWidth;
                        }

                        if (dataSet.isDrawValuesEnabled()) {
                            drawValue(c,
                                    formattedValue,
                                    buffer.buffer[j + 2] + (val >= 0 ? posOffset : negOffset),
                                    y + halfTextHeight,
                                    dataSet.getValueTextColor(j / 2));
                        }

                        if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {

                            Drawable icon = entry.getIcon();

                            float px = buffer.buffer[j + 2] + (val >= 0 ? posOffset : negOffset);
                            float py = y;

                            px += iconsOffset.x;
                            py += iconsOffset.y;

                            Utils.drawImage(
                                    c,
                                    icon,
                                    (int) px,
                                    (int) py,
                                    icon.getIntrinsicWidth(),
                                    icon.getIntrinsicHeight());
                        }
                    }

                    // if each value of a potential stack should be drawn
                } else {

                    Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

                    int bufferIndex = 0;
                    int index = 0;

                    while (index < dataSet.getEntryCount() * mAnimator.getPhaseX()) {

                        BarEntry entry = dataSet.getEntryForIndex(index);

                        int color = dataSet.getValueTextColor(index);
                        float[] vals = entry.getYVals();

                        // we still draw stacked bars, but there is one
                        // non-stacked
                        // in between
                        if (vals == null) {

                            if (!mViewPortHandler.isInBoundsTop(buffer.buffer[bufferIndex + 1]))
                                break;

                            if (!mViewPortHandler.isInBoundsX(buffer.buffer[bufferIndex]))
                                continue;

                            if (!mViewPortHandler.isInBoundsBottom(buffer.buffer[bufferIndex + 1]))
                                continue;

                            String formattedValue = formatter.getBarLabel(entry);

                            // calculate the correct offset depending on the draw position of the value
                            float valueTextWidth = Utils.calcTextWidth(mValuePaint, formattedValue);
                            posOffset = (drawValueAboveBar ? valueOffsetPlus : -(valueTextWidth + valueOffsetPlus));
                            negOffset = (drawValueAboveBar ? -(valueTextWidth + valueOffsetPlus) : valueOffsetPlus);

                            if (isInverted) {
                                posOffset = -posOffset - valueTextWidth;
                                negOffset = -negOffset - valueTextWidth;
                            }

                            if (dataSet.isDrawValuesEnabled()) {
                                drawValue(c, formattedValue,
                                        buffer.buffer[bufferIndex + 2]
                                                + (entry.getY() >= 0 ? posOffset : negOffset),
                                        buffer.buffer[bufferIndex + 1] + halfTextHeight, color);
                            }

                            if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {

                                Drawable icon = entry.getIcon();

                                float px = buffer.buffer[bufferIndex + 2]
                                        + (entry.getY() >= 0 ? posOffset : negOffset);
                                float py = buffer.buffer[bufferIndex + 1];

                                px += iconsOffset.x;
                                py += iconsOffset.y;

                                Utils.drawImage(
                                        c,
                                        icon,
                                        (int) px,
                                        (int) py,
                                        icon.getIntrinsicWidth(),
                                        icon.getIntrinsicHeight());
                            }

                        } else {

                            float[] transformed = new float[vals.length * 2];

                            float posY = 0f;
                            float negY = -entry.getNegativeSum();

                            for (int k = 0, idx = 0; k < transformed.length; k += 2, idx++) {

                                float value = vals[idx];
                                float y;

                                if (value == 0.0f && (posY == 0.0f || negY == 0.0f)) {
                                    // Take care of the situation of a 0.0 value, which overlaps a non-zero bar
                                    y = value;
                                } else if (value >= 0.0f) {
                                    posY += value;
                                    y = posY;
                                } else {
                                    y = negY;
                                    negY -= value;
                                }

                                transformed[k] = y * phaseY;
                            }

                            trans.pointValuesToPixel(transformed);

                            for (int k = 0; k < transformed.length; k += 2) {

                                final float val = vals[k / 2];
                                String formattedValue = formatter.getBarStackedLabel(val, entry);

                                // calculate the correct offset depending on the draw position of the value
                                float valueTextWidth = Utils.calcTextWidth(mValuePaint, formattedValue);
                                posOffset = (drawValueAboveBar ? valueOffsetPlus : -(valueTextWidth + valueOffsetPlus));
                                negOffset = (drawValueAboveBar ? -(valueTextWidth + valueOffsetPlus) : valueOffsetPlus);

                                if (isInverted) {
                                    posOffset = -posOffset - valueTextWidth;
                                    negOffset = -negOffset - valueTextWidth;
                                }

                                final boolean drawBelow =
                                        (val == 0.0f && negY == 0.0f && posY > 0.0f) ||
                                                val < 0.0f;

                                float x = transformed[k]
                                        + (drawBelow ? negOffset : posOffset);
                                float y = (buffer.buffer[bufferIndex + 1] + buffer.buffer[bufferIndex + 3]) / 2f;

                                if (!mViewPortHandler.isInBoundsTop(y))
                                    break;

                                if (!mViewPortHandler.isInBoundsX(x))
                                    continue;

                                if (!mViewPortHandler.isInBoundsBottom(y))
                                    continue;

                                if (dataSet.isDrawValuesEnabled()) {
                                    drawValue(c, formattedValue, x, y + halfTextHeight, color);
                                }

                                if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {

                                    Drawable icon = entry.getIcon();

                                    Utils.drawImage(
                                            c,
                                            icon,
                                            (int) (x + iconsOffset.x),
                                            (int) (y + iconsOffset.y),
                                            icon.getIntrinsicWidth(),
                                            icon.getIntrinsicHeight());
                                }
                            }
                        }

                        bufferIndex = vals == null ? bufferIndex + 4 : bufferIndex + 4 * vals.length;
                        index++;
                    }
                }

                MPPointF.recycleInstance(iconsOffset);
            }
        }
    }
}
