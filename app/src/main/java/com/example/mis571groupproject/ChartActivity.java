package com.example.mis571groupproject;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mis571groupproject.constant.SQLCommand;
import com.example.mis571groupproject.util.DBOperator;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends Activity {

    private LinearLayout chartContainer;
    private TextView chartTitleView;
    private Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_page);

        chartContainer = findViewById(R.id.chart_container);
        chartTitleView = findViewById(R.id.chart_title);
        backBtn = findViewById(R.id.chart_back_btn);

        // Back button returns to QueryActivity
        backBtn.setOnClickListener(v -> finish());

        // Make sure DB is copied
        try {
            DBOperator.copyDB(getBaseContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        int pos = getIntent().getIntExtra("query_pos", -1);
        String title = getIntent().getStringExtra("query_title");
        if (title == null) title = "Chart";

        chartTitleView.setText(title);

        if (pos == -1) {
            Toast.makeText(this, "No query selected.", Toast.LENGTH_LONG).show();
            return;
        }

        String sql = getSqlForPosition(pos);
        if (sql == null || sql.isEmpty()) {
            Toast.makeText(this, "No SQL defined for this query.", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d("ChartActivity", "Executing SQL: " + sql);

        Cursor cursor = null;
        try {
            cursor = DBOperator.getInstance().execQuery(sql);
            if (cursor != null && cursor.moveToFirst()) {
                GraphicalView chartView = buildChartFromCursor(cursor, title);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        (int) (300 * getResources().getDisplayMetrics().density)
                );
                lp.setMargins(16, 16, 16, 16);
                chartView.setLayoutParams(lp);

                chartContainer.addView(chartView);
            } else {
                Toast.makeText(this, "No data available for this chart.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("ChartActivity", "Error building chart", e);
            Toast.makeText(this, "Error building chart. Check logs.", Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    // Same mapping as QueryActivity
    private String getSqlForPosition(int pos) {
        switch (pos) {
            case 0:  return SQLCommand.QUERY_1;
            case 1:  return SQLCommand.QUERY_2;
            case 2:  return SQLCommand.QUERY_3;
            case 3:  return SQLCommand.QUERY_4;
            case 4:  return SQLCommand.QUERY_5;
            case 5:  return SQLCommand.QUERY_6;
            case 6:  return SQLCommand.QUERY_7;
            case 7:  return SQLCommand.QUERY_8;
            case 8:  return SQLCommand.QUERY_9;
            case 9:  return SQLCommand.QUERY_10;
            case 10: return SQLCommand.QUERY_11;
            case 11: return SQLCommand.QUERY_12;
            case 12: return SQLCommand.QUERY_13;
            default: return "";
        }
    }

    // Same chart builder as in QueryActivity
    private GraphicalView buildChartFromCursor(Cursor cursor, String title) {
        XYSeries series = new XYSeries(title);
        List<String> labels = new ArrayList<>();

        int labelIndex = 0;
        int valueIndex = cursor.getColumnCount() - 1;

        int xIndex = 0;
        double maxY = 0;

        do {
            String label;
            try {
                label = cursor.getString(labelIndex);
            } catch (Exception e) {
                label = "Row " + (cursor.getPosition() + 1);
            }

            double value;
            try {
                value = cursor.getDouble(valueIndex);
            } catch (Exception e) {
                continue;
            }

            if (label != null && label.length() > 12) {
                label = label.substring(0, 12) + "...";
            }

            labels.add(label);
            series.add(xIndex, value);

            if (value > maxY) {
                maxY = value;
            }

            xIndex++;
        } while (cursor.moveToNext());

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setChartTitle(title);
        renderer.setChartTitleTextSize(40);
        renderer.setLabelsTextSize(24);
        renderer.setLegendTextSize(24);
        renderer.setMargins(new int[]{20, 40, 20, 20});
        renderer.setShowGrid(true);
        renderer.setZoomButtonsVisible(false);
        renderer.setPanEnabled(true, false);

        renderer.setXLabels(0);
        for (int i = 0; i < labels.size(); i++) {
            renderer.addXTextLabel(i, labels.get(i));
        }

        renderer.setXAxisMin(-0.5);
        renderer.setXAxisMax(labels.size() - 0.5);
        renderer.setYAxisMin(0);
        renderer.setYAxisMax(maxY * 1.1);

        XYSeriesRenderer seriesRenderer = new XYSeriesRenderer();
        seriesRenderer.setDisplayChartValues(true);
        seriesRenderer.setChartValuesTextSize(24f);
        seriesRenderer.setColor(0xFF64B5F6);
        renderer.addSeriesRenderer(seriesRenderer);

        return ChartFactory.getBarChartView(
                this,
                dataset,
                renderer,
                BarChart.Type.DEFAULT
        );
    }
}
