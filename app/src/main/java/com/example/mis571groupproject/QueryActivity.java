package com.example.mis571groupproject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mis571groupproject.constant.SQLCommand;
import com.example.mis571groupproject.util.DBOperator;
import com.example.mis571groupproject.view.TableView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.List;

public class QueryActivity extends Activity implements View.OnClickListener {

    Button backBtn, resultBtn, chartPageBtn;
    Spinner querySpinner;
    ScrollView scrollView;
    LinearLayout resultsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query);

        // Make sure DB is copied
        try {
            DBOperator.copyDB(getBaseContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        backBtn = findViewById(R.id.goback_btn);
        resultBtn = findViewById(R.id.showresult_btn);
        chartPageBtn = findViewById(R.id.viewchart_btn); // NEW BUTTON
        querySpinner = findViewById(R.id.querylist_spinner);
        scrollView = findViewById(R.id.scrollview_queryresults);
        resultsContainer = findViewById(R.id.results_container);

        backBtn.setOnClickListener(this);
        resultBtn.setOnClickListener(this);
        chartPageBtn.setOnClickListener(this); // NEW CLICK LISTENER
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.showresult_btn) {
            runSelectedQuery();

        } else if (id == R.id.goback_btn) {
            startActivity(new Intent(this, GroupActivity.class));

        } else if (id == R.id.viewchart_btn) {
            openChartPageForSelectedQuery(); // NEW ACTION
        }
    }

    /** NEW: Opens ChartActivity with the selected query */
    private void openChartPageForSelectedQuery() {
        int pos = querySpinner.getSelectedItemPosition();
        if (pos == Spinner.INVALID_POSITION) {
            Toast.makeText(this, "Please choose a query first!", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = querySpinner.getSelectedItem().toString();

        Intent intent = new Intent(this, ChartActivity.class);
        intent.putExtra("query_pos", pos);
        intent.putExtra("query_title", title);
        startActivity(intent);
    }

    /** Original: runs chart + table in the scroll area */
    private void runSelectedQuery() {
        try {
            int pos = querySpinner.getSelectedItemPosition();
            if (pos == Spinner.INVALID_POSITION) {
                Toast.makeText(this, "Please choose a query!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Clear old chart + table
            resultsContainer.removeAllViews();

            String sql = getSqlForPosition(pos);
            if (sql.isEmpty()) {
                Toast.makeText(this, "Invalid SQL for this query.", Toast.LENGTH_LONG).show();
                return;
            }

            Log.d("QueryActivity", "Executing SQL: " + sql);
            String chartTitle = querySpinner.getSelectedItem().toString();

            // 1) Build chart
            Cursor chartCursor = DBOperator.getInstance().execQuery(sql);
            if (chartCursor != null && chartCursor.moveToFirst()) {
                GraphicalView chartView = buildChartFromCursor(chartCursor, chartTitle);

                // Give chart a visible size
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        (int)(300 * getResources().getDisplayMetrics().density)
                );
                lp.setMargins(16,16,16,16);
                chartView.setLayoutParams(lp);

                resultsContainer.addView(chartView);
                chartCursor.close();
            } else {
                Toast.makeText(this, "No data available for chart.", Toast.LENGTH_SHORT).show();
            }

            // 2) Build table
            Cursor tableCursor = DBOperator.getInstance().execQuery(sql);
            if (tableCursor != null) {
                TableView tableView = new TableView(this, tableCursor);
                resultsContainer.addView(tableView);
                // TableView closes cursor internally
            }

        } catch (Exception e) {
            Log.e("QueryActivity", "Error executing query", e);
            Toast.makeText(this, "Error while showing results. Check logs.", Toast.LENGTH_LONG).show();
        }
    }

    /** Map spinner index → SQL command */
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

    /** Build a bar chart from SQL result */
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
                continue; // skip non-numeric data
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
