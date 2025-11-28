package com.example.mis571groupproject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
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

    Button backBtn, resultBtn;
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
        querySpinner = findViewById(R.id.querylist_spinner);
        scrollView = findViewById(R.id.scrollview_queryresults);
        resultsContainer = findViewById(R.id.results_container);

        backBtn.setOnClickListener(this);
        resultBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.showresult_btn) {
            runSelectedQuery();

        } else if (id == R.id.goback_btn) {
            startActivity(new Intent(this, GroupActivity.class));

        }
    }

    /**
     * NEW: Opens ChartActivity with the selected query
     */
    private void openChartPageForSelectedQuery() {
        int pos = querySpinner.getSelectedItemPosition();
        if (pos == Spinner.INVALID_POSITION) {
            Toast.makeText(this, "Please choose a query first!", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = querySpinner.getSelectedItem().toString();

    }

    /**
     * Original: runs chart + table in the scroll area
     */
    private void runSelectedQuery() {
        try {
            int pos = querySpinner.getSelectedItemPosition();
            if (pos == Spinner.INVALID_POSITION) {
                Toast.makeText(this, "Please choose a query!", Toast.LENGTH_SHORT).show();
                return;
            }

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
                GraphicalView chartView;

                if (pos == 0) {
                    // Query 1: show top 10 + bottom 10 sellers
                    chartView = buildTopBottomChartFromCursor(chartCursor, chartTitle);

                } else if (pos == 1) {
                    // Query 2: top 2 product categories by month
                    chartView = buildQuery2CategoryChart(chartCursor);

                } else if (pos == 2) {
                    // Query 3: best and worst product categories
                    chartView = buildQuery3CategoryChart(chartCursor);

                } else if (pos == 3) {
                    // Query 4: show top 10 + bottom 10 avg. delivery time
                    chartView = buildTopBottomChartFromCursor(chartCursor, chartTitle);

                } else if (pos == 4) {
                    //Query 5: sales and orders by state
                    chartView = buildChartFromCursor(chartCursor, chartTitle);

                    //REMOVE BAR LABELS FOR QUERY 5
                    XYMultipleSeriesRenderer renderer =
                            (XYMultipleSeriesRenderer) ((BarChart) chartView.getChart()).getRenderer();
                    for (int i = 0; i < renderer.getSeriesRendererCount(); i++) {
                        ((XYSeriesRenderer) renderer.getSeriesRendererAt(i))
                                .setDisplayChartValues(false);
                    }

                }else if (pos == 6) {
                    //Query 7: customers with multiple orders
                    chartView = buildQuery7CustomerOrdersChart(chartCursor);

                }else if (pos == 7) {
                    //Query 8: show top 10 + bottom 10 order value by product category
                    chartView = buildTopBottomChartFromCursor(chartCursor, chartTitle);

                }else if (pos == 8) {
                    //Query 9: show top 10 + bottom 10 review scores by product category
                    chartView = buildTopBottomChartFromCursor(chartCursor, chartTitle);

                }else if (pos == 9) {
                    //Query 10: show top 10 + bottom 10 revenue by seller
                    chartView = buildTopBottomChartFromCursor(chartCursor, chartTitle);

                }else if (pos == 11) {
                    //Query 12: Orders by time of day
                    chartView = buildQuery12HourlyChart(chartCursor);

            } else {
                    // All other queries: generic bar chart
                    chartView = buildChartFromCursor(chartCursor, chartTitle);
                }

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        (int) (300 * getResources().getDisplayMetrics().density)
                );
                lp.setMargins(16, 16, 16, 16);
                chartView.setLayoutParams(lp);

                resultsContainer.addView(chartView);
                chartCursor.close();
            } else {
                Toast.makeText(this, "No data available for chart.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2) Build table
            Cursor tableCursor = DBOperator.getInstance().execQuery(sql);
            if (tableCursor != null) {
                TableView tableView = new TableView(this, tableCursor);
                resultsContainer.addView(tableView);
            }

        } catch (Exception e) {
            Log.e("QueryActivity", "Error executing query", e);
            Toast.makeText(this, "Error while showing results. Check logs.", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Map spinner index → SQL command
     */
    private String getSqlForPosition(int pos) {
        switch (pos) {
            case 0:
                return SQLCommand.QUERY_1;
            case 1:
                return SQLCommand.QUERY_2;
            case 2:
                return SQLCommand.QUERY_3;
            case 3:
                return SQLCommand.QUERY_4;
            case 4:
                return SQLCommand.QUERY_5;
            case 5:
                return SQLCommand.QUERY_6;
            case 6:
                return SQLCommand.QUERY_7;
            case 7:
                return SQLCommand.QUERY_8;
            case 8:
                return SQLCommand.QUERY_9;
            case 9:
                return SQLCommand.QUERY_10;
            case 10:
                return SQLCommand.QUERY_11;
            case 11:
                return SQLCommand.QUERY_12;
            case 12:
                return SQLCommand.QUERY_13;
            default:
                return "";
        }
    }

    /**
     * Generic bar chart for queries that have:
     */
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
        renderer.setLabelsTextSize(22);
        renderer.setLegendTextSize(26);
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
        seriesRenderer.setColor(Color.BLUE);

        renderer.addSeriesRenderer(seriesRenderer);

        return ChartFactory.getBarChartView(
                this,
                dataset,
                renderer,
                BarChart.Type.DEFAULT
        );
    }

    /**
     * Special chart for QUERY_1 and Query_4:
     * shows only Top 10 and Bottom 10
     */
    private GraphicalView buildTopBottomChartFromCursor(Cursor cursor, String title) {

        List<String> sellerIds = new ArrayList<>();
        List<Double> scores = new ArrayList<>();

        do {
            try {
                String seller = cursor.getString(0);
                double score = cursor.getDouble(1);
                sellerIds.add(seller);
                scores.add(score);
            } catch (Exception e) {

            }
        } while (cursor.moveToNext());

        int total = sellerIds.size();
        if (total == 0) {
            return buildChartFromCursor(cursor, title);
        }

        int topCount = Math.min(10, total);
        int bottomCount = Math.min(10, total - topCount);

        XYSeries topSeries = new XYSeries("Top " + topCount);
        XYSeries bottomSeries = new XYSeries("Bottom " + bottomCount);
        List<String> xLabels = new ArrayList<>();

        int xIndex = 0;
        double maxY = 0;

        for (int i = 0; i < topCount; i++) {
            String seller = sellerIds.get(i);
            double score = scores.get(i);

            if (seller != null && seller.length() > 10) {
                seller = seller.substring(0, 10) + "...";
            }

            xLabels.add(seller + " (T)");
            topSeries.add(xIndex, score);
            if (score > maxY) maxY = score;
            xIndex++;
        }

        int startBottom = total - bottomCount;
        for (int i = startBottom; i < total; i++) {
            String seller = sellerIds.get(i);
            double score = scores.get(i);

            if (seller != null && seller.length() > 10) {
                seller = seller.substring(0, 10) + "...";
            }

            xLabels.add(seller + " (B)");
            bottomSeries.add(xIndex, score);
            if (score > maxY) maxY = score;
            xIndex++;
        }

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(topSeries);
        dataset.addSeries(bottomSeries);


        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setChartTitle(title + " (Top & Bottom 10)");
        renderer.setChartTitleTextSize(30);
        renderer.setLabelsTextSize(24);
        renderer.setLegendTextSize(24);
        renderer.setMargins(new int[]{20, 40, 20, 20});
        renderer.setShowGrid(true);
        renderer.setZoomButtonsVisible(false);
        renderer.setPanEnabled(true, false);

        renderer.setXLabels(0);
        for (int i = 0; i < xLabels.size(); i++) {
            renderer.addXTextLabel(i, xLabels.get(i));
        }
        renderer.setXAxisMin(-0.5);
        renderer.setXAxisMax(xLabels.size() - 0.5);
        renderer.setXLabelsAngle(90f);

        renderer.setYAxisMin(0);
        renderer.setYAxisMax(maxY * 1.1);

        XYSeriesRenderer topRenderer = new XYSeriesRenderer();
        topRenderer.setColor(Color.GREEN);
        topRenderer.setDisplayChartValues(true);
        topRenderer.setChartValuesTextSize(22f);

        XYSeriesRenderer bottomRenderer = new XYSeriesRenderer();
        bottomRenderer.setColor(Color.RED);
        bottomRenderer.setDisplayChartValues(true);
        bottomRenderer.setChartValuesTextSize(22f);

        renderer.addSeriesRenderer(topRenderer);
        renderer.addSeriesRenderer(bottomRenderer);

        return ChartFactory.getBarChartView(
                this,
                dataset,
                renderer,
                BarChart.Type.DEFAULT
        );
    }

    /**
     * Custom chart for QUERY_2:
     */
    private GraphicalView buildQuery2CategoryChart(Cursor cursor) {

        XYSeries series = new XYSeries("Total Sales");
        List<String> labels = new ArrayList<>();

        int xIndex = 0;
        double maxY = 0;

        do {
            try {
                String category = cursor.getString(1);
                double sales = cursor.getDouble(2);

                if (category != null && category.length() > 15) {
                    category = category.substring(0, 15) + "...";
                }

                labels.add(category);
                series.add(xIndex, sales);

                if (sales > maxY) maxY = sales;

                xIndex++;

            } catch (Exception e) {

            }
        } while (cursor.moveToNext());

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setChartTitle("Top Categories by Month");
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

        renderer.setXLabelsAngle(90f);

        renderer.setYAxisMin(0);
        renderer.setYAxisMax(maxY * 1.1);

        XYSeriesRenderer sRenderer = new XYSeriesRenderer();
        sRenderer.setDisplayChartValues(true);
        sRenderer.setChartValuesTextSize(22f);
        sRenderer.setColor(Color.BLUE);

        renderer.addSeriesRenderer(sRenderer);

        return ChartFactory.getBarChartView(
                this,
                dataset,
                renderer,
                BarChart.Type.DEFAULT
        );
    }

    /**
     * Custom chart for QUERY_3:
     */
    private GraphicalView buildQuery3CategoryChart(Cursor cursor) {

        XYSeries series = new XYSeries("Total Sales");
        List<String> labels = new ArrayList<>();

        int xIndex = 0;
        double maxY = 0;

        int stateIndex = cursor.getColumnIndex("state");
        int categoryIndex = cursor.getColumnIndex("category");
        int salesIndex = cursor.getColumnIndex("total_sales");

        if (categoryIndex == -1) categoryIndex = 1;
        if (salesIndex == -1) salesIndex = cursor.getColumnCount() - 1;

        do {
            try {
                String category = cursor.getString(categoryIndex);
                double sales = cursor.getDouble(salesIndex);

                if (category != null && category.length() > 15) {
                    category = category.substring(0, 15) + "...";
                }

                labels.add(category);
                series.add(xIndex, sales);

                if (sales > maxY) maxY = sales;

                xIndex++;

            } catch (Exception e) {
                Log.e("QueryActivity", "Error parsing Query 3 row", e);
            }

        } while (cursor.moveToNext());

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setChartTitle("Best & Worst Categories by State");
        renderer.setChartTitleTextSize(40);
        renderer.setLabelsTextSize(24);
        renderer.setLegendTextSize(24);
        renderer.setMargins(new int[]{20, 40, 120, 20});
        renderer.setShowGrid(true);
        renderer.setZoomButtonsVisible(false);
        renderer.setPanEnabled(true, false);

        renderer.setXLabels(0);
        for (int i = 0; i < labels.size(); i++) {
            renderer.addXTextLabel(i, labels.get(i));
        }

        renderer.setXAxisMin(-0.5);
        renderer.setXAxisMax(labels.size() - 0.5);

        renderer.setXLabelsAngle(90f);

        renderer.setYAxisMin(0);
        renderer.setYAxisMax(maxY * 1.1);

        XYSeriesRenderer sRenderer = new XYSeriesRenderer();
        sRenderer.setDisplayChartValues(true);
        sRenderer.setChartValuesTextSize(22f);
        sRenderer.setColor(Color.BLUE);

        renderer.addSeriesRenderer(sRenderer);

        return ChartFactory.getBarChartView(
                this,
                dataset,
                renderer,
                BarChart.Type.DEFAULT
        );
    }
    /**
     * Custom chart for QUERY 7:
     */
    private GraphicalView buildQuery7CustomerOrdersChart(Cursor cursor) {

        XYSeries series = new XYSeries("Total Orders");

        List<String> labels = new ArrayList<>();
        int xIndex = 1;
        double maxY = 0;

        do {
            try {
                int totalOrders = cursor.getInt(1);
                series.add(xIndex, totalOrders);

                labels.add(String.valueOf(xIndex));

                if (totalOrders > maxY) maxY = totalOrders;

                xIndex++;

            } catch (Exception e) {

            }
        } while (cursor.moveToNext());

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setChartTitle("Customer Order Counts");
        renderer.setChartTitleTextSize(40);
        renderer.setLabelsTextSize(24);
        renderer.setLegendTextSize(24);
        renderer.setMargins(new int[]{20, 40, 20, 20});
        renderer.setShowGrid(true);
        renderer.setZoomButtonsVisible(false);
        renderer.setPanEnabled(true, false);

        renderer.setXLabels(0);
        int step = Math.max(1, labels.size() / 20);
        for (int i = 1; i < labels.size(); i++) {
            renderer.addXTextLabel(i, labels.get(i - 1));
        }

        renderer.setXAxisMin(0.5);
        renderer.setXAxisMax(labels.size() + 0.5);
        renderer.setXLabelsAngle(90f);

        renderer.setYAxisMin(0);
        renderer.setYAxisMax(maxY * 1.1);

        XYSeriesRenderer sRenderer = new XYSeriesRenderer();
        sRenderer.setDisplayChartValues(true);
        sRenderer.setChartValuesTextSize(22f);
        sRenderer.setColor(Color.BLUE);

        renderer.addSeriesRenderer(sRenderer);

        return ChartFactory.getBarChartView(
                this,
                dataset,
                renderer,
                BarChart.Type.DEFAULT
        );
    }
    /**
     * Custom chart for QUERY 12:
     */
    private GraphicalView buildQuery12HourlyChart(Cursor cursor) {

        XYSeries series = new XYSeries("Total Orders");
        List<String> labels = new ArrayList<>();

        int xIndex = 0;
        double maxY = 0;

        int timeLabelIndex = cursor.getColumnIndex("time_of_day");
        int hourIndex = cursor.getColumnIndex("hour_24");
        int totalIndex = cursor.getColumnIndex("total_orders");

        if (timeLabelIndex == -1) timeLabelIndex = 0;
        if (hourIndex == -1) hourIndex = 1;
        if (totalIndex == -1) totalIndex = 2;

        do {
            try {
                String timeLabel = cursor.getString(timeLabelIndex);
                int hour = cursor.getInt(hourIndex);
                int totalOrders = cursor.getInt(totalIndex);

                labels.add(timeLabel);
                series.add(xIndex, totalOrders);

                if (totalOrders > maxY) maxY = totalOrders;

                xIndex++;

            } catch (Exception e) {
                Log.e("QueryActivity", "Error parsing Query 12 row", e);
            }
        } while (cursor.moveToNext());

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setChartTitle("Orders by Hour of Day");
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(14);
        renderer.setLegendTextSize(20);
        renderer.setMargins(new int[]{20, 40, 80, 20});
        renderer.setShowGrid(true);
        renderer.setZoomButtonsVisible(false);
        renderer.setPanEnabled(true, false);

        renderer.setXLabels(0);

        int step = 3;
        for (int i = 0; i < labels.size(); i+= step) {
            renderer.addXTextLabel(i, labels.get(i));
        }

        if (!labels.isEmpty()) {
            int lastIndex = labels.size() - 1;
            renderer.addXTextLabel(lastIndex, labels.get(lastIndex));
        }

        renderer.setXAxisMin(-0.5);
        renderer.setXAxisMax(labels.size() - 0.5);
        renderer.setXLabelsAngle(90f);

        renderer.setYAxisMin(0);
        renderer.setYAxisMax(maxY * 1.15);

        XYSeriesRenderer sRenderer = new XYSeriesRenderer();
        sRenderer.setDisplayChartValues(true);
        sRenderer.setChartValuesTextSize(22f);
        sRenderer.setColor(Color.BLUE);

        renderer.addSeriesRenderer(sRenderer);

        return ChartFactory.getBarChartView(
                this,
                dataset,
                renderer,
                BarChart.Type.DEFAULT
        );
    }
}

