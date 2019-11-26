package com.github.wuxudong.rncharts.charts.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.renderer.LineRadarRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.github.wuxudong.rncharts.charts.CircularRadarArcChart;

import java.util.List;

public class CircularRadarArcChartRenderer extends LineRadarRenderer {


    private static final String TAG = "CRadarArcChartRenderer";
    protected CircularRadarArcChart mChart;

    /**
     * paint for drawing the web
     */
    protected Paint mWebPaint;
    protected Paint mHighlightCirclePaint;
    final RectF oval = new RectF();


    public CircularRadarArcChartRenderer(CircularRadarArcChart chart, ChartAnimator animator,
                                         ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;

        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Paint.Style.STROKE);
        mHighlightPaint.setStrokeWidth(2f);
        mHighlightPaint.setColor(Color.rgb(255, 187, 115));

        mWebPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWebPaint.setStyle(Paint.Style.STROKE);

        mHighlightCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public Paint getWebPaint() {
        return mWebPaint;
    }

    @Override
    public void initBuffers() {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawData(Canvas c) {

        RadarData radarData = mChart.getData();

        int mostEntries = radarData.getMaxEntryCountSet().getEntryCount();
        int dataSetCount = radarData.getDataSets().size();
        int i = 0;
        for (IRadarDataSet set : radarData.getDataSets()) {
            i++;
            if (set.isVisible()) {
                drawDataSet(c, set, mostEntries, i == dataSetCount);
            }
        }
    }

    protected Path mDrawDataSetSurfacePathBuffer = new Path();

    /**
     * Calculate angle between two lines with two given points
     *
     * @param A1 First point first line
     * @param A2 Second point first line
     * @param B1 First point second line
     * @param B2 Second point second line
     * @return Angle between two lines in degrees
     * <p>
     * credits : https://stackoverflow.com/questions/3365171/calculating-the-angle-between-two-lines-without-having-to-calculate-the-slope
     */

    public static float angleBetween2Lines(MPPointF A1, MPPointF A2, MPPointF B1, MPPointF B2) {
        float angle1 = (float) Math.atan2(A2.y - A1.y, A1.x - A2.x);
        float angle2 = (float) Math.atan2(B2.y - B1.y, B1.x - B2.x);
        float calculatedAngle = (float) Math.toDegrees(angle1 - angle2);
        if (calculatedAngle < 0) calculatedAngle += 360;
        return calculatedAngle;
    }

    /**
     * Draws the RadarDataSet
     *
     * @param c
     * @param dataSet
     * @param mostEntries the entry count of the dataset with the most entries
     */
    protected void drawDataSet(Canvas c, IRadarDataSet dataSet, int mostEntries, boolean isLastDataSet) {

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();
        float rotationAngle = mChart.getRotationAngle();
        MPPointF center = mChart.getCenterOffsets();
        MPPointF pOut = MPPointF.getInstance(0, 0);
        MPPointF pOutPrevious = MPPointF.getInstance(0, 0);
        MPPointF pOutFirstForLast = MPPointF.getInstance(0, 0);
        Path surface = mDrawDataSetSurfacePathBuffer;
        surface.reset();

        float r = mChart.getRadius();

        boolean hasMovedToPoint = false;
        float entryCount = dataSet.getEntryCount();

        for (int j = 0; j < entryCount; j++) {
            mRenderPaint.setColor(dataSet.getColor(j));

            RadarEntry e = dataSet.getEntryForIndex(j);

            Utils.getPosition(
                    center,
                    (e.getY() - mChart.getYChartMin()) * factor * phaseY,
                    sliceangle * j * phaseX + mChart.getRotationAngle(), pOut);

            if (Float.isNaN(pOut.x))
                continue;

            if (!hasMovedToPoint) {
                surface.moveTo(pOut.x, pOut.y);
                if (isLastDataSet)
                    pOutFirstForLast = MPPointF.getInstance(pOut.x, pOut.y);
                hasMovedToPoint = true;

            } else {
                boolean xNotFromCenter = Math.abs(pOutPrevious.x - center.x) > 0;
                boolean yNotFromCenter = Math.abs(pOutPrevious.y - center.y) > 0;
                boolean xIsNotCenter = Math.abs(pOut.x - center.x) > 0;
                boolean yIsNotCenter = Math.abs(pOut.y - center.y) > 0;
                boolean xIsNotPrevious = Math.abs(pOutPrevious.x - pOut.x) > 0;
                boolean yIsNotPrevious = Math.abs(pOutPrevious.y - pOut.y) > 0;

                if ((xNotFromCenter || yNotFromCenter) && (xIsNotCenter || yIsNotCenter)) {
                    float radius = (float) Math.sqrt(Math.pow(pOut.x - center.x, 2) + Math.pow(pOut.y - center.y, 2));
                    oval.set(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
                    float sweepAngle = sliceangle;
                    float startAngle = sliceangle * (j - 1) + rotationAngle;
                    surface.arcTo(oval, startAngle, sweepAngle, false);
                } else if (isLastDataSet && j == entryCount - 1) {
                    boolean _xIsNotCenter = Math.abs(pOutFirstForLast.x - center.x) > 0;
                    boolean _yIsNotCenter = Math.abs(pOutFirstForLast.y - center.y) > 0;
                    if (_xIsNotCenter || _yIsNotCenter) {
                        float radius = (float) Math.sqrt(Math.pow(pOut.x - center.x, 2) + Math.pow(pOut.y - center.y, 2));
                        oval.set(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
                        float sweepAngle = sliceangle;
                        float startAngle = sliceangle * (j) + rotationAngle;
                        surface.arcTo(oval, startAngle, sweepAngle, false);
                    }
                } else if (yIsNotPrevious || xIsNotPrevious) {
                    surface.lineTo(pOut.x, pOut.y);
                }
            }

            pOutPrevious.x = pOut.getX();
            pOutPrevious.y = pOut.getY();

        }

        if (dataSet.getEntryCount() > mostEntries) {
            
            // if this is not the largest set, draw a line to the center before closing
            surface.lineTo(center.x, center.y);
        }

        surface.close();

        if (dataSet.isDrawFilledEnabled()) {

            final Drawable drawable = dataSet.getFillDrawable();
            if (drawable != null) {
                drawFilledPath(c, surface, drawable);
            } else {
                drawFilledPath(c, surface, dataSet.getFillColor(), dataSet.getFillAlpha());
            }
        }

        mRenderPaint.setStrokeWidth(dataSet.getLineWidth());
        mRenderPaint.setStyle(Paint.Style.STROKE);

        // draw the line (only if filled is disabled or alpha is below 255)
        if (!dataSet.isDrawFilledEnabled() || dataSet.getFillAlpha() < 255)
            c.drawPath(surface, mRenderPaint);

        MPPointF.recycleInstance(center);
        MPPointF.recycleInstance(pOut);
    }

    private boolean clipPathSupported() {
        return Utils.getSDKInt() >= 18;
    }

    /**
     * Draws the provided path in filled mode with the provided color and alpha.
     * Special thanks to Angelo Suzuki (https://github.com/tinsukE) for this.
     *
     * @param c
     * @param filledPath
     * @param fillColor
     * @param fillAlpha
     */
    @Override
    protected void drawFilledPath(Canvas c, Path filledPath, int fillColor, int fillAlpha) {

        int color = (fillAlpha << 24) | (fillColor & 0xffffff);

        if (clipPathSupported()) {

            int save = c.save();

            c.clipPath(filledPath);

            c.drawColor(color);
            c.restoreToCount(save);
        } else {

            // save
            Paint.Style previous = mRenderPaint.getStyle();
            int previousColor = mRenderPaint.getColor();

            // set
            mRenderPaint.setStyle(Paint.Style.FILL);
            mRenderPaint.setColor(color);

            c.drawPath(filledPath, mRenderPaint);

            // restore
            mRenderPaint.setColor(previousColor);
            mRenderPaint.setStyle(previous);
        }
    }

    @Override
    public void drawValues(Canvas c) {

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();

        MPPointF center = mChart.getCenterOffsets();
        MPPointF pOut = MPPointF.getInstance(0, 0);
        MPPointF pIcon = MPPointF.getInstance(0, 0);

        float yoffset = Utils.convertDpToPixel(5f);

        for (int i = 0; i < mChart.getData().getDataSetCount(); i++) {

            IRadarDataSet dataSet = mChart.getData().getDataSetByIndex(i);

            if (!shouldDrawValues(dataSet))
                continue;

            // apply the text-styling defined by the DataSet
            applyValueTextStyle(dataSet);

            ValueFormatter formatter = dataSet.getValueFormatter();

            MPPointF iconsOffset = MPPointF.getInstance(dataSet.getIconsOffset());
            iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x);
            iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y);

            for (int j = 0; j < dataSet.getEntryCount(); j++) {

                RadarEntry entry = dataSet.getEntryForIndex(j);

                Utils.getPosition(
                        center,
                        (entry.getY() - mChart.getYChartMin()) * factor * phaseY,
                        sliceangle * j * phaseX + mChart.getRotationAngle(),
                        pOut);

                if (dataSet.isDrawValuesEnabled()) {
                    drawValue(c, formatter.getRadarLabel(entry), pOut.x, pOut.y - yoffset, dataSet.getValueTextColor(j));
                }

                if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {

                    Drawable icon = entry.getIcon();

                    Utils.getPosition(
                            center,
                            (entry.getY()) * factor * phaseY + iconsOffset.y,
                            sliceangle * j * phaseX + mChart.getRotationAngle(),
                            pIcon);

                    //noinspection SuspiciousNameCombination
                    pIcon.y += iconsOffset.x;

                    Utils.drawImage(
                            c,
                            icon,
                            (int) pIcon.x,
                            (int) pIcon.y,
                            icon.getIntrinsicWidth(),
                            icon.getIntrinsicHeight());
                }
            }

            MPPointF.recycleInstance(iconsOffset);
        }

        MPPointF.recycleInstance(center);
        MPPointF.recycleInstance(pOut);
        MPPointF.recycleInstance(pIcon);
    }

    @Override
    public void drawValue(Canvas c, String valueText, float x, float y, int color) {
        mValuePaint.setColor(color);
        c.drawText(valueText, x, y, mValuePaint);
    }

    @Override
    public void drawExtras(Canvas c) {
        drawCircularWebAndValuePercentages(c);
    }

    protected void drawCircularWebAndValuePercentages(Canvas c) {
        float sliceangle = mChart.getSliceAngle();
        float legendSize = mChart.getLegend().getTextSize();
        float webLineWidth = mChart.getWebLineWidth();
        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();
        float rotationangle = mChart.getRotationAngle();
        RadarData radarData = mChart.getData();
        List<IRadarDataSet> dataSets = radarData.getDataSets();
        int[] colors = radarData.getColors();
        Path textPath = mDrawDataSetSurfacePathBuffer;
        textPath.reset();
        MPPointF center = mChart.getCenterOffsets();

        // draw the web lines that come from the center
        mWebPaint.setStrokeWidth(webLineWidth);
        mWebPaint.setColor(mChart.getWebColor());
        mWebPaint.setAlpha(mChart.getWebAlpha());
        final int xIncrements = 1 + mChart.getSkipWebLineCount();
        int maxEntryCount = radarData.getMaxEntryCountSet().getEntryCount();
        MPPointF p = MPPointF.getInstance(0, 0);

        for (int i = 0; i < maxEntryCount; i += xIncrements) {
            Utils.getPosition(
                    center,
                    mChart.getYRange() * factor,
                    sliceangle * i + rotationangle,
                    p);
            c.drawLine(center.x, center.y, p.x, p.y, mWebPaint);
        }
        MPPointF.recycleInstance(p);

        // draw the inner-web
        mWebPaint.setStrokeWidth(mChart.getWebLineWidthInner());
        mWebPaint.setColor(mChart.getWebColorInner());
        mWebPaint.setAlpha(mChart.getWebAlpha());
        DashPathEffect dashPath = new DashPathEffect(new float[]{15, 15}, (float) 15.0);
        mWebPaint.setPathEffect(dashPath);
        int labelCount = mChart.getYAxis().mEntryCount;
        MPPointF p1out = MPPointF.getInstance(0, 0);
        MPPointF p2out = MPPointF.getInstance(0, 0);
        int colorsCount = colors.length;
        for (int j = 0; j < labelCount; j++) {
            for (int i = 0; i < colorsCount; i++) {
                float r = (mChart.getYAxis().mEntries[j] - mChart.getYChartMin()) * factor;
                Utils.getPosition(center, r, sliceangle * i + rotationangle, p1out);
                Utils.getPosition(center, r, sliceangle * (i + 1) + rotationangle, p2out);

                if (labelCount - 1 == j) {
                    textPath.reset();
                    mWebPaint.setPathEffect(null);
                    mWebPaint.setStyle(Paint.Style.FILL);
                    int fillColor = dataSets.get(i).getFillColor();
                    mWebPaint.setColor(fillColor);
                    mWebPaint.setAntiAlias(true);
                    mWebPaint.setAlpha(mChart.getWebAlpha());
                    float radius = r;
                    oval.set(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
                    float sweepAngle = sliceangle;//angleBetween2Lines(center,pOutPrevious,center,pOut);
                    float startAngle = sliceangle * (i) + rotationangle;
                    c.drawArc(oval, startAngle, sweepAngle, true, mWebPaint);
                    mWebPaint.setTextSize(legendSize);
                    mWebPaint.setTextAlign(Paint.Align.CENTER);
                    mWebPaint.setAlpha(255);
                    Paint.FontMetrics fm = mWebPaint.getFontMetrics();
                    float textHeight = fm.descent - fm.ascent;
                    float lineHeight = fm.bottom - fm.top + fm.leading;
                    IRadarDataSet ds = radarData.getDataSetByIndex(i);
                    RadarEntry rdata = ds.getEntryForIndex(i);
                    float value = rdata.getValue();
                    StringBuffer valueBuffer = new StringBuffer(String.format("%.1f", value < 0 ? 0f : value)).append("%");

                    if(i>1 && i<4){
                        textPath.addArc (oval,startAngle+sweepAngle , -sweepAngle);
                        c.drawTextOnPath(ds.getLabel(), textPath, 0, textHeight-(legendSize*2/webLineWidth), mWebPaint);
                        c.drawTextOnPath(valueBuffer.toString(), textPath, 0, textHeight-(legendSize*3/webLineWidth)+lineHeight, mWebPaint);
                    }else{
                        textPath.addArc (oval,startAngle , sweepAngle);
                        c.drawTextOnPath(ds.getLabel(), textPath, 0, -(legendSize/webLineWidth), mWebPaint);
                        c.drawTextOnPath(valueBuffer.toString(), textPath, 0, -lineHeight, mWebPaint);
                    }
                    mWebPaint.setColor(mChart.getWebColorInner());
                    mWebPaint.setStyle(Paint.Style.STROKE);
                } else {
                    mWebPaint.setPathEffect(dashPath);
                }
            }
        }
        MPPointF.recycleInstance(p1out);
        MPPointF.recycleInstance(p2out);
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();

        MPPointF center = mChart.getCenterOffsets();
        MPPointF pOut = MPPointF.getInstance(0, 0);

        RadarData radarData = mChart.getData();

        for (Highlight high : indices) {

            IRadarDataSet set = radarData.getDataSetByIndex(high.getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            RadarEntry e = set.getEntryForIndex((int) high.getX());

            if (!isInBoundsX(e, set))
                continue;

            float y = (e.getY() - mChart.getYChartMin());

            Utils.getPosition(center,
                    y * factor * mAnimator.getPhaseY(),
                    sliceangle * high.getX() * mAnimator.getPhaseX() + mChart.getRotationAngle(),
                    pOut);

            high.setDraw(pOut.x, pOut.y);

            // draw the lines
            drawHighlightLines(c, pOut.x, pOut.y, set);

            if (set.isDrawHighlightCircleEnabled()) {

                if (!Float.isNaN(pOut.x) && !Float.isNaN(pOut.y)) {

                    int strokeColor = set.getHighlightCircleStrokeColor();
                    if (strokeColor == ColorTemplate.COLOR_NONE) {
                        strokeColor = set.getColor(0);
                    }

                    if (set.getHighlightCircleStrokeAlpha() < 255) {
                        strokeColor = ColorTemplate.colorWithAlpha(strokeColor, set.getHighlightCircleStrokeAlpha());
                    }

                    drawHighlightCircle(c,
                            pOut,
                            set.getHighlightCircleInnerRadius(),
                            set.getHighlightCircleOuterRadius(),
                            set.getHighlightCircleFillColor(),
                            strokeColor,
                            set.getHighlightCircleStrokeWidth());
                }
            }
        }

        MPPointF.recycleInstance(center);
        MPPointF.recycleInstance(pOut);
    }

    protected Path mDrawHighlightCirclePathBuffer = new Path();

    public void drawHighlightCircle(Canvas c,
                                    MPPointF point,
                                    float innerRadius,
                                    float outerRadius,
                                    int fillColor,
                                    int strokeColor,
                                    float strokeWidth) {
        c.save();

        outerRadius = Utils.convertDpToPixel(outerRadius);
        innerRadius = Utils.convertDpToPixel(innerRadius);

        if (fillColor != ColorTemplate.COLOR_NONE) {
            Path p = mDrawHighlightCirclePathBuffer;
            p.reset();
            p.addCircle(point.x, point.y, outerRadius, Path.Direction.CW);
            if (innerRadius > 0.f) {
                p.addCircle(point.x, point.y, innerRadius, Path.Direction.CCW);
            }
            mHighlightCirclePaint.setColor(fillColor);
            mHighlightCirclePaint.setStyle(Paint.Style.FILL);
            c.drawPath(p, mHighlightCirclePaint);
        }

        if (strokeColor != ColorTemplate.COLOR_NONE) {
            mHighlightCirclePaint.setColor(strokeColor);
            mHighlightCirclePaint.setStyle(Paint.Style.STROKE);
            mHighlightCirclePaint.setStrokeWidth(Utils.convertDpToPixel(strokeWidth));
            c.drawCircle(point.x, point.y, outerRadius, mHighlightCirclePaint);
        }

        c.restore();
    }
}
