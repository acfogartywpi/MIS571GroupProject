package com.example.mis571groupproject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.Context;

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
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;

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
        chartPageBtn = findViewById(R.id.chartpage_btn);
        querySpinner = findViewById(R.id.querylist_spinner);
        scrollView = findViewById(R.id.scrollview_queryresults);
        resultsContainer = findViewById(R.id.results_container);

        backBtn.setOnClickListener(this);
        resultBtn.setOnClickListener(this);
        chartPageBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.showresult_btn) {
            runSelectedQuery();

        } else if (id == R.id.chartpage_btn) {
            openChartPageForSelectedQuery();

        } else if (id == R.id.goback_btn) {
            startActivity(new Intent(this, GroupActivity.class));

        }
    }

    private void openChartPageForSelectedQuery() {
        int pos = querySpinner.getSelectedItemPosition();
        if (pos == Spinner.INVALID_POSITION) {
            Toast.makeText(this, "Please choose a query first!", Toast.LENGTH_SHORT).show();
            return;
        }

        String sql = getSqlForPosition(pos);
        if (sql.isEmpty()) {
            Toast.makeText(this, "Invalid SQL for this query.", Toast.LENGTH_LONG).show();
            return;
        }

        String title = querySpinner.getSelectedItem().toString();

        Intent intent = new Intent(this, com.example.mis571groupproject.view.ChartActivity.class);
        intent.putExtra("queryPos", pos);
        intent.putExtra("chartTitle", title);
        intent.putExtra("sql", sql);
        startActivity(intent);

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
            /*String chartTitle = querySpinner.getSelectedItem().toString();

            // 1) Build chart
            Cursor chartCursor = DBOperator.getInstance().execQuery(sql);
            if (chartCursor != null && chartCursor.moveToFirst()) {
                GraphicalView chartView;

                if (pos == 0) {
                    // Query 1: histogram of average review scores
                    chartView = buildQuery1ReviewHistogram(this, chartCursor);

                } else if (pos == 1) {
                    // Query 2: top 2 product categories by month
                    chartView = buildQuery2CategoryChart(this, chartCursor);

                } else if (pos == 2) {
                    // Query 3: best and worst product categories
                    chartView = buildQuery3CategoryChart(this, chartCursor);

                } else if (pos == 3) {
                    // Query 4: histogram of average delivery time per seller
                    chartView = buildQuery4DeliveryHistogram(this, chartCursor);

                } else if (pos == 4) {
                    // Query 5: sales and orders by state (two-color chart)
                    chartView = buildQuery5SalesOrdersChart(this, chartCursor);

                }else if (pos == 6) {
                    //Query 7: customers with multiple orders
                    chartView = buildQuery7CustomerOrdersChart(this, chartCursor);

                }else if (pos == 7) {
                    //Query 8: bin product category by order value
                    chartView = buildQuery8TopBottomCategoryChart(this, chartCursor);

                }else if (pos == 8) {
                    //Query 9: bin product category by average review score
                    chartView = buildQuery9TopBottomCategoryChart(this, chartCursor);

                }else if (pos == 9) {
                    //Query 10: show top 10 + bottom 10 revenue by seller
                    chartView = buildQuery10BinnedSellerRevenueChart(this, chartCursor);

                }else if (pos == 10) {
                    //Query 11: bins states by delivery speed
                    chartView = buildQuery11BinnedDeliveryChart(this, chartCursor);

                }else if (pos == 11) {
                    //Query 12: Orders by time of day
                    chartView = buildQuery12HourlyChart(this, chartCursor);

                } else if (pos == 12) {
                    // Query 13: total customers by state
                    chartView = buildQuery13CustomersByStateChart(this, chartCursor);

            } else {
                    // All other queries: generic bar chart
                    chartView = buildChartFromCursor(this, chartCursor, chartTitle);
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
            }*/

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
    public static GraphicalView buildChartFromCursor(Context context, Cursor cursor, String title) {

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
        renderer.setLabelsTextSize(26);
        renderer.setLegendTextSize(26);
        renderer.setMargins(new int[]{60, 100, 40, 40});
        renderer.setShowGrid(true);
        renderer.setZoomButtonsVisible(false);
        renderer.setPanEnabled(true, false);
        renderer.setBarWidth(50);
        renderer.setBarSpacing(20);

        renderer.setXLabels(0);
        for (int i = 0; i < labels.size(); i++) {
            renderer.addXTextLabel(i, labels.get(i));
        }

        renderer.setXAxisMin(-0.5);
        renderer.setXAxisMax(labels.size() - 0.5);
        renderer.setYAxisMin(0);
        renderer.setYAxisMax(maxY * 1.1);

        renderer.setYLabelsPadding(20f);

        XYSeriesRenderer seriesRenderer = new XYSeriesRenderer();
        seriesRenderer.setDisplayChartValues(true);
        seriesRenderer.setChartValuesTextSize(24f);
        seriesRenderer.setColor(Color.BLUE);

        renderer.addSeriesRenderer(seriesRenderer);

        return ChartFactory.getBarChartView(
                context,
                dataset,
                renderer,
                BarChart.Type.DEFAULT
        );
    }

    /**
     * Special chart for QUERY_1
     * average review scores by seller
     */
    public static GraphicalView buildQuery1ReviewHistogram(Context context, Cursor cursor) {

        int avgIndex = cursor.getColumnIndex("avg_review_score");
        if (avgIndex == -1) avgIndex = 1;

        final double MIN_SCORE = 1.0;
        final double MAX_SCORE = 5.0;
        final double BIN_WIDTH = 0.5;
        final int BIN_COUNT = (int) ((MAX_SCORE - MIN_SCORE) / BIN_WIDTH);

        int[] counts = new int[BIN_COUNT];

        do {
            try {
                double avgScore = cursor.getDouble(avgIndex);

                if (avgScore < MIN_SCORE) avgScore = MIN_SCORE;
                if (avgScore > MAX_SCORE) avgScore = MAX_SCORE;

                int bin = (int) ((avgScore - MIN_SCORE) / BIN_WIDTH);
                if (bin < 0) bin = 0;
                if (bin >= BIN_COUNT) bin = BIN_COUNT - 1;

                counts[bin]++;

            } catch (Exception e) {
            }
        } while (cursor.moveToNext());

        XYSeries series = new XYSeries("Sellers");

        String[] labels = new String[BIN_COUNT];
        int maxY = 0;

        for (int i = 0; i < BIN_COUNT; i++) {
            double low = MIN_SCORE + i * BIN_WIDTH;
            double high = low + BIN_WIDTH;
            labels[i] = String.format("%.1f–%.1f", low, high);

            series.add(i, counts[i]);
            if (counts[i] > maxY) maxY = counts[i];
        }

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setChartTitle("Histogram of Seller Avg Review Scores");
        renderer.setYTitle("Number of Sellers");
        renderer.setChartTitleTextSize(34f);
        renderer.setAxisTitleTextSize(30f);
        renderer.setLabelsTextSize(24f);
        renderer.setLegendTextSize(24f);
        renderer.setMargins(new int[]{50, 140, 80, 40});

        renderer.setShowGrid(true);
        renderer.setZoomButtonsVisible(false);
        renderer.setPanEnabled(true, false);

        renderer.setBarWidth(35);
        renderer.setBarSpacing(5);

        renderer.setXLabels(0);
        for (int i = 0; i < labels.length; i++) {
            renderer.addXTextLabel(i, labels[i]);
        }
        renderer.setXLabelsAngle(0f);
        renderer.setXLabelsPadding(20f);

        renderer.setXAxisMin(-0.5);
        renderer.setXAxisMax(labels.length - 0.5);

        renderer.setYAxisMin(0);
        renderer.setYAxisMax(maxY * 1.2);
        renderer.setYLabelsPadding(35f);

        XYSeriesRenderer sRenderer = new XYSeriesRenderer();
        sRenderer.setColor(Color.BLUE);
        sRenderer.setDisplayChartValues(true);
        sRenderer.setChartValuesTextSize(22f);

        renderer.addSeriesRenderer(sRenderer);

        return ChartFactory.getBarChartView(
                context,
                dataset,
                renderer,
                BarChart.Type.DEFAULT
        );
    }

    /**
     * Custom chart for QUERY_2:
     * To product categories by sales and by month
     */
    public static GraphicalView buildQuery2CategoryChart(Context context, Cursor cursor) {

        XYSeries topSeries = new XYSeries("Top Category");
        XYSeries secondSeries = new XYSeries("2nd Category");

        List<String> monthLabels = new ArrayList<>();

        double maxY = 0;
        String currentMonth = null;
        int monthIndex = -1;
        int rankInMonth = 0;

        do {
            try {
                String month = cursor.getString(0);
                String category = cursor.getString(1);
                double sales = cursor.getDouble(2);

                if (month == null) month = "(Unknown)";
                if (category == null) category = "(Category)";

                if (!month.equals(currentMonth)) {
                    currentMonth = month;
                    monthIndex++;
                    rankInMonth = 1;
                    monthLabels.add(month);
                } else {
                    rankInMonth++;
                }

                if (rankInMonth == 1) {
                    topSeries.add(monthIndex - 0.15, sales);
                    topSeries.addAnnotation(category, (monthIndex - 0.15) + 0.05, sales * 1.02);
                }
                else if (rankInMonth == 2) {
                    secondSeries.add(monthIndex + 0.15, sales);
                    secondSeries.addAnnotation(category, (monthIndex + 0.15) + 0.05, sales * 1.02);
                }

                if (sales > maxY) maxY = sales;

            } catch (Exception e) {
                Log.e("QueryActivity", "Error parsing Query 2 row", e);
            }
        } while (cursor.moveToNext());

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(topSeries);
        dataset.addSeries(secondSeries);

        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setChartTitle("Top 2 Product Category Sales by Month");

        renderer.setXTitle("Month");
        renderer.setYTitle("Total Sales");

        renderer.setChartTitleTextSize(36f);
        renderer.setAxisTitleTextSize(28f);
        renderer.setLabelsTextSize(26f);
        renderer.setLegendTextSize(26f);

        renderer.setOrientation(Orientation.VERTICAL);

        renderer.setMargins(new int[]{20, 120, 140, 60});
        renderer.setShowGrid(true);
        renderer.setZoomButtonsVisible(false);
        renderer.setPanEnabled(true, false);

        renderer.setBarWidth(12);
        renderer.setBarSpacing(0);

        renderer.setXLabels(0);
        for (int i = 0; i < monthLabels.size(); i++) {
            renderer.addXTextLabel(i, monthLabels.get(i));
        }
        renderer.setXAxisMin(-0.65);
        renderer.setXAxisMax(monthLabels.size() - 0.5);
        renderer.setXLabelsPadding(35f);

        renderer.setYAxisMin(0);
        renderer.setYAxisMax(maxY * 1.1);
        renderer.setYLabelsPadding(40f);

        XYSeriesRenderer topRenderer = new XYSeriesRenderer();
        topRenderer.setDisplayChartValues(false);
        topRenderer.setChartValuesTextSize(20f);
        topRenderer.setColor(Color.BLUE);
        topRenderer.setAnnotationsColor(Color.BLACK);
        topRenderer.setAnnotationsTextSize(20f);
        topRenderer.setAnnotationsTextAlign(Paint.Align.CENTER);

        XYSeriesRenderer secondRenderer = new XYSeriesRenderer();
        secondRenderer.setDisplayChartValues(false);
        secondRenderer.setChartValuesTextSize(20f);
        secondRenderer.setColor(Color.GREEN);
        secondRenderer.setAnnotationsColor(Color.BLACK);
        secondRenderer.setAnnotationsTextSize(20f);
        secondRenderer.setAnnotationsTextAlign(Paint.Align.CENTER);

        renderer.addSeriesRenderer(topRenderer);
        renderer.addSeriesRenderer(secondRenderer);

        return ChartFactory.getBarChartView(
                context,
                dataset,
                renderer,
                BarChart.Type.DEFAULT
        );
    }

    /**
     * Custom chart for QUERY_3:
     * best and worst product categories by state
     */
    public static GraphicalView buildQuery3CategoryChart(Context context, Cursor cursor) {

        int stateIndex = cursor.getColumnIndex("state");
        int salesIndex = cursor.getColumnIndex("total_sales");
        if (stateIndex == -1) stateIndex = 0;
        if (salesIndex == -1) salesIndex = cursor.getColumnCount() - 1;

        java.util.Map<String, Double> stateTotals = new java.util.HashMap<>();

        do {
            try {
                String state = cursor.getString(stateIndex);
                double sales = cursor.getDouble(salesIndex);

                if (state == null) state = "(Unknown)";

                Double current = stateTotals.get(state);
                if (current == null) current = 0.0;
                stateTotals.put(state, current + sales);

            } catch (Exception e) {
                Log.e("QueryActivity", "Error parsing Query 3 row", e);
            }
        } while (cursor.moveToNext());

        java.util.List<java.util.Map.Entry<String, Double>> entries =
                new java.util.ArrayList<>(stateTotals.entrySet());

        java.util.Collections.sort(entries,
                new java.util.Comparator<java.util.Map.Entry<String, Double>>() {
                    @Override
                    public int compare(java.util.Map.Entry<String, Double> a,
                                       java.util.Map.Entry<String, Double> b) {
                        return Double.compare(b.getValue(), a.getValue());
                    }
                });

        int totalStates = entries.size();
        if (totalStates == 0) {
            return null;
        }

        int topCount = Math.min(5, totalStates);
        int bottomCount = Math.min(5, totalStates - topCount);

        XYSeries series = new XYSeries("Total Sales");
        java.util.List<String> labels = new java.util.ArrayList<>();

        double maxY = 0;
        int xIndex = 0;

        for (int i = 0; i < topCount; i++) {
            String state = entries.get(i).getKey();
            double sales = entries.get(i).getValue();

            labels.add("Top-" + state);
            series.add(xIndex, sales);
            if (sales > maxY) maxY = sales;
            xIndex++;
        }

        int startBottom = totalStates - bottomCount;
        for (int i = startBottom; i < totalStates; i++) {
            String state = entries.get(i).getKey();
            double sales = entries.get(i).getValue();

            labels.add("Bottom-" + state);
            series.add(xIndex, sales);
            if (sales > maxY) maxY = sales;
            xIndex++;
        }

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setChartTitle("Top & Bottom States by Total Sales");
        renderer.setChartTitleTextSize(34f);
        renderer.setLabelsTextSize(22f);
        renderer.setLegendTextSize(24f);
        renderer.setMargins(new int[]{60, 120, 60, 40});
        renderer.setShowGrid(true);
        renderer.setZoomButtonsVisible(false);
        renderer.setPanEnabled(true, false);

        renderer.setBarWidth(40);
        renderer.setBarSpacing(20);

        renderer.setXLabels(0);
        for (int i = 0; i < labels.size(); i++) {
            renderer.addXTextLabel(i, labels.get(i));
        }
        renderer.setXLabelsAngle(45f);

        renderer.setXAxisMin(-0.5);
        renderer.setXAxisMax(labels.size() - 0.5);

        renderer.setXLabelsPadding(25f);
        renderer.setYLabelsPadding(50f);

        renderer.setYAxisMin(0);
        renderer.setYAxisMax(maxY * 1.15);

        XYSeriesRenderer sRenderer = new XYSeriesRenderer();
        sRenderer.setDisplayChartValues(true);
        sRenderer.setChartValuesTextSize(24f);
        sRenderer.setChartValuesFormat(new java.text.DecimalFormat("#,###"));
        sRenderer.setColor(Color.BLUE);

        renderer.addSeriesRenderer(sRenderer);

        return ChartFactory.getBarChartView(
                context,
                dataset,
                renderer,
                BarChart.Type.DEFAULT
        );

    }
    /**
     * Custom chart for QUERY_4:
     * seller avg. delivery time
     */
    public static GraphicalView buildQuery4DeliveryHistogram(Context context, Cursor cursor) {

        int avgIndex = cursor.getColumnIndex("average_delivery_time");
        if (avgIndex == -1) {
            avgIndex = 1;
        }

        int[] bins = new int[4];

        do {
            try {
                double avgDays = cursor.getDouble(avgIndex);

                int bin;
                if (avgDays <= 3) {
                    bin = 0;
                } else if (avgDays <= 7) {
                    bin = 1;
                } else if (avgDays <= 14) {
                    bin = 2;
                } else {
                    bin = 3;
                }
                bins[bin]++;

            } catch (Exception e) {
                Log.e("QueryActivity", "Error parsing Query 4 row", e);
            }
        } while (cursor.moveToNext());

        XYSeries series = new XYSeries("Sellers");
        String[] labels = {"0–3", "4–7", "8–14", "15+"};

        int maxY = 0;
        for (int i = 0; i < bins.length; i++) {
            series.add(i, bins[i]);
            if (bins[i] > maxY) maxY = bins[i];
        }

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setChartTitle("Histogram of Avg Delivery Time per Seller");
        renderer.setYTitle("Number of Sellers");
        renderer.setXTitle("Average Delivery Time (days)");
        renderer.setChartTitleTextSize(34f);
        renderer.setAxisTitleTextSize(30f);
        renderer.setLabelsTextSize(24f);
        renderer.setLegendTextSize(24f);

        renderer.setMargins(new int[]{60, 140, 80, 40});
        renderer.setShowGrid(true);
        renderer.setZoomButtonsVisible(false);
        renderer.setPanEnabled(true, false);

        renderer.setBarWidth(40);
        renderer.setBarSpacing(10);

        renderer.setXLabels(0);
        for (int i = 0; i < labels.length; i++) {
            renderer.addXTextLabel(i, labels[i]);
        }
        renderer.setXLabelsAngle(0f);
        renderer.setXLabelsPadding(20f);

        renderer.setXAxisMin(-0.5);
        renderer.setXAxisMax(labels.length - 0.5);

        renderer.setYAxisMin(0);
        renderer.setYAxisMax(maxY * 1.2);
        renderer.setYLabelsPadding(35f);

        XYSeriesRenderer sRenderer = new XYSeriesRenderer();
        sRenderer.setColor(Color.BLUE);
        sRenderer.setDisplayChartValues(true);
        sRenderer.setChartValuesTextSize(22f);

        renderer.addSeriesRenderer(sRenderer);

        return ChartFactory.getBarChartView(
                context,
                dataset,
                renderer,
                BarChart.Type.DEFAULT
        );
    }

    /**
     * Custom chart for QUERY_5:
     * Total sales and orders by state
     */
    public static GraphicalView buildQuery5SalesOrdersChart(Context context, Cursor cursor) {

        int stateIndex = cursor.getColumnIndex("state");
        if (stateIndex == -1) stateIndex = 0;

        int salesIndex = cursor.getColumnIndex("total_sales");
        if (salesIndex == -1) salesIndex = 1;

        int ordersIndex = cursor.getColumnIndex("total_orders");
        if (ordersIndex == -1) {
            if (cursor.getColumnCount() >= 3) {
                ordersIndex = 2;
            } else {
                cursor.moveToFirst();
                return buildChartFromCursor(context, cursor,"Sales & Orders by State");
            }
        }

        XYSeries salesSeries = new XYSeries("Total Sales", 0);
        XYSeries ordersSeries = new XYSeries("Total Orders", 1);

        List<String> stateLabels = new ArrayList<>();

        double maxSales = 0;
        double maxOrders = 0;
        int xIndex = 0;

        do {
            try {
                String state = cursor.getString(stateIndex);
                double sales = cursor.getDouble(salesIndex);
                double orders = cursor.getDouble(ordersIndex);

                if (state == null) state = "(Unknown)";

                stateLabels.add(state);

                salesSeries.add(xIndex, sales);
                ordersSeries.add(xIndex, orders);

                if (sales > maxSales) maxSales = sales;
                if (orders > maxOrders) maxOrders = orders;

                xIndex++;

            } catch (Exception e) {
                Log.e("QueryActivity", "Query 5 parse error", e);
            }
        } while (cursor.moveToNext());

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(salesSeries);
        dataset.addSeries(ordersSeries);

        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer(2);
        renderer.setChartTitle("Sales & Orders by State");
        renderer.setChartTitleTextSize(40f);
        renderer.setAxisTitleTextSize(26f);
        renderer.setLabelsTextSize(26f);
        renderer.setLegendTextSize(26f);
        renderer.setMargins(new int[]{50, 160, 60, 120});
        renderer.setShowGrid(true);
        renderer.setZoomButtonsVisible(false);
        renderer.setPanEnabled(true, false);

        renderer.setYAxisAlign(Paint.Align.LEFT, 0);
        renderer.setYAxisAlign(Paint.Align.RIGHT, 1);

        renderer.setYLabelsAlign(Paint.Align.RIGHT, 0);
        renderer.setYLabelsAlign(Paint.Align.LEFT, 1);

        renderer.setYLabels(0);

        renderer.setYLabelsPadding(20f);

        double leftMax = maxSales;
        int leftStep = (int)(leftMax / 5);

        for (int i = 0; i <= leftMax; i += leftStep) {
            String label = String.format("%.1fK", i / 1000.0);
            renderer.addYTextLabel(i, label, 0);
        }

        double rightMax = maxOrders;
        int rightStep = (int)(rightMax / 5);

        for (int i = 0; i <= rightMax; i += rightStep) {
            String label = String.format("%.1fK", i / 1000.0);
            renderer.addYTextLabel(i, label, 1);
        }

        renderer.setYLabelsColor(0, Color.BLUE);
        renderer.setYLabelsColor(1, Color.GREEN);

        renderer.setYAxisMin(0, 0);
        renderer.setYAxisMax(maxSales * 1.15, 0);

        renderer.setYTitle("Total Sales", 0);
        renderer.setYTitle("Total Orders", 1);

        renderer.setBarWidth(10);
        renderer.setBarSpacing(5);

        renderer.setXLabels(0);
        for (int i = 0; i < stateLabels.size(); i++) {
            renderer.addXTextLabel(i, stateLabels.get(i));
        }
        renderer.setXLabelsAngle(45f);
        renderer.setXAxisMin(-0.5);
        renderer.setXAxisMax(stateLabels.size() - 0.5);
        renderer.setXLabelsPadding(25f);

        renderer.setYAxisMin(0, 0);
        renderer.setYAxisMax(maxSales * 1.15, 0);
        renderer.setYTitle("Total Sales", 0);
        renderer.setYLabelsColor(0, Color.WHITE);

        renderer.setYAxisMin(0, 1);
        renderer.setYAxisMax(maxOrders * 1.15, 1);
        renderer.setYTitle("Total Orders", 1);
        renderer.setYLabelsColor(1, Color.GRAY);

        XYSeriesRenderer salesRenderer = new XYSeriesRenderer();
        salesRenderer.setColor(Color.BLUE);
        salesRenderer.setDisplayChartValues(false);

        XYSeriesRenderer ordersRenderer = new XYSeriesRenderer();
        ordersRenderer.setColor(Color.GREEN);
        ordersRenderer.setDisplayChartValues(false);
        ordersRenderer.setChartValuesSpacing(10f);

        renderer.addSeriesRenderer(salesRenderer);
        renderer.addSeriesRenderer(ordersRenderer);

        return ChartFactory.getBarChartView(
                context,
                dataset,
                renderer,
                BarChart.Type.DEFAULT
        );
    }

    /**
     * Custom chart for QUERY 7:
     * Orders by hour of day
     */

    public static GraphicalView buildQuery7CustomerOrdersChart(Context context, Cursor cursor) {

        int[] bins = new int[5];

        int totalIndex = cursor.getColumnIndex("total_orders");
        if (totalIndex == -1) totalIndex = 1;

        do {
            try {
                int orders = cursor.getInt(totalIndex);

                int bin;
                if (orders <= 2)      bin = 0;
                else if (orders <= 5) bin = 1;
                else if (orders <= 10)bin = 2;
                else if (orders <= 20)bin = 3;
                else                  bin = 4;

                bins[bin]++;

            } catch (Exception e) { }
        } while (cursor.moveToNext());

        XYSeries series = new XYSeries("Customers");
        String[] labels = {"1–2", "3–5", "6–10", "11–20", "20+"};

        int maxY = 0;
        for (int i = 0; i < bins.length; i++) {
            series.add(i, bins[i]);
            if (bins[i] > maxY) maxY = bins[i];
        }

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setChartTitle("Customer Order Count Distribution");
        renderer.setChartTitleTextSize(38f);
        renderer.setLabelsTextSize(26f);
        renderer.setLegendTextSize(26f);
        renderer.setMargins(new int[]{60, 120, 50, 40});
        renderer.setShowGrid(true);
        renderer.setZoomButtonsVisible(false);
        renderer.setPanEnabled(true, false);
        renderer.setYTitle("Total Orders");
        renderer.setAxisTitleTextSize(32f);

        renderer.setBarWidth(50);
        renderer.setBarSpacing(10);

        renderer.setXLabels(0);
        for (int i = 0; i < labels.length; i++) {
            renderer.addXTextLabel(i, labels[i]);
        }
        renderer.setXLabelsAngle(0);
        renderer.setXLabelsPadding(15f);

        renderer.setXAxisMin(-0.5);
        renderer.setXAxisMax(labels.length - 0.5);

        renderer.setYAxisMin(0);
        renderer.setYAxisMax(maxY * 1.15);

        renderer.setYLabelsPadding(40f);

        XYSeriesRenderer sRenderer = new XYSeriesRenderer();
        sRenderer.setDisplayChartValues(true);
        sRenderer.setChartValuesTextSize(24f);
        sRenderer.setColor(Color.BLUE);

        renderer.addSeriesRenderer(sRenderer);

        return ChartFactory.getBarChartView(
                context,
                dataset,
                renderer,
                BarChart.Type.DEFAULT
        );
    }

    /**
     * Custom chart for QUERY 7:
     */
    public static GraphicalView buildQuery12HourlyChart(Context context, Cursor cursor) {

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
        renderer.setChartTitleTextSize(35);
        renderer.setLabelsTextSize(24);
        renderer.setLegendTextSize(35);
        renderer.setMargins(new int[]{60, 80, 110, 40});
        renderer.setShowGrid(true);
        renderer.setZoomButtonsVisible(false);
        renderer.setPanEnabled(true, false);
        renderer.setBarWidth(14);
        renderer.setBarSpacing(0);
        renderer.setShowLegend(true);

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
        renderer.setXLabelsAngle(75f);
        renderer.setXLabelsPadding(40f);

        renderer.setYAxisMin(0);
        renderer.setYAxisMax(maxY * 1.15);
        renderer.setYLabelsPadding(40f);

        XYSeriesRenderer sRenderer = new XYSeriesRenderer();
        sRenderer.setDisplayChartValues(false);
        sRenderer.setColor(Color.BLUE);
        sRenderer.setPointStrokeWidth(0);
        sRenderer.setGradientEnabled(false);

        renderer.addSeriesRenderer(sRenderer);

        return ChartFactory.getBarChartView(
                context,
                dataset,
                renderer,
                BarChart.Type.STACKED
        );
    }

    /**
     * Custom chart for QUERY_8:
     * Top + bottom 10 product categories by total order value.
     */
    public static GraphicalView buildQuery8TopBottomCategoryChart(Context context, Cursor cursor) {

        int nameIndex = 0;
        int valueIndex = cursor.getColumnCount() - 1;

        if (valueIndex < 0) {
            cursor.moveToFirst();
            return buildChartFromCursor(context, cursor, "Top/Bottom Product Categories");
        }

        java.util.List<java.util.Map.Entry<String, Double>> rows = new java.util.ArrayList<>();

        do {
            try {
                String category = cursor.getString(nameIndex);
                double value = cursor.getDouble(valueIndex);

                if (category == null) category = "(Unknown)";
                rows.add(new java.util.AbstractMap.SimpleEntry<>(category, value));

            } catch (Exception ignored) {}
        } while (cursor.moveToNext());

        if (rows.isEmpty()) {
            cursor.moveToFirst();
            return buildChartFromCursor(context, cursor, "Top/Bottom Product Categories");
        }

        java.util.Collections.sort(rows, (a, b) ->
                Double.compare(b.getValue(), a.getValue())
        );

        int total = rows.size();
        int topCount = Math.min(10, total);
        int bottomCount = Math.min(10, total - topCount);

        XYSeries topSeries = new XYSeries("Top 10");
        XYSeries bottomSeries = new XYSeries("Bottom 10");
        java.util.List<String> labels = new java.util.ArrayList<>();

        double maxY = 0;
        int xIndex = 0;

        for (int i = 0; i < topCount; i++) {
            String cat = rows.get(i).getKey();
            double val = rows.get(i).getValue();

            labels.add(cat);
            topSeries.add(xIndex, val);

            if (val > maxY) maxY = val;
            xIndex++;
        }

        int startBottom = total - bottomCount;
        for (int i = startBottom; i < total; i++) {
            String cat = rows.get(i).getKey();
            double val = rows.get(i).getValue();

            labels.add(cat);
            bottomSeries.add(xIndex, val);

            if (val > maxY) maxY = val;
            xIndex++;
        }

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(topSeries);
        dataset.addSeries(bottomSeries);

        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setChartTitle("Top 10 & Bottom 10 Product Categories by Value");
        renderer.setChartTitleTextSize(38f);
        renderer.setLabelsTextSize(22f);
        renderer.setLegendTextSize(26f);
        renderer.setMargins(new int[]{80, 120, 220, 40});
        renderer.setShowGrid(true);
        renderer.setZoomButtonsVisible(false);
        renderer.setPanEnabled(true, false);

        renderer.setBarWidth(25);
        renderer.setBarSpacing(4);

        renderer.setXLabels(0);
        for (int i = 0; i < labels.size(); i++) {
            renderer.addXTextLabel(i, labels.get(i));
        }
        renderer.setXLabelsAngle(90f);
        renderer.setXLabelsPadding(150f);

        renderer.setXAxisMin(-0.8);
        renderer.setXAxisMax(labels.size() - 0.2);

        renderer.setYAxisMin(0);
        renderer.setYAxisMax(maxY * 1.15);
        renderer.setYLabelsPadding(40f);

        XYSeriesRenderer topRenderer = new XYSeriesRenderer();
        topRenderer.setColor(Color.GREEN);
        topRenderer.setDisplayChartValues(false);
        topRenderer.setChartValuesTextSize(24f);

        XYSeriesRenderer bottomRenderer = new XYSeriesRenderer();
        bottomRenderer.setColor(Color.RED);
        bottomRenderer.setDisplayChartValues(false);
        bottomRenderer.setChartValuesTextSize(24f);

        renderer.addSeriesRenderer(topRenderer);
        renderer.addSeriesRenderer(bottomRenderer);

        return ChartFactory.getBarChartView(
                context,
                dataset,
                renderer,
                BarChart.Type.DEFAULT
        );
    }


    /*public static GraphicalView buildQuery8BinnedCategoryValueChart(Context context, Cursor cursor) {

        int valueIndex = cursor.getColumnCount() - 1;
        if (valueIndex < 0) {
            cursor.moveToFirst();
            return buildChartFromCursor(context, cursor, "Product Category Order Value Distribution");
        }

        java.util.List<Double> values = new java.util.ArrayList<>();

        double minVal = Double.MAX_VALUE;
        double maxVal = -Double.MAX_VALUE;

        do {
            try {
                double v = cursor.getDouble(valueIndex);
                values.add(v);
                if (v < minVal) minVal = v;
                if (v > maxVal) maxVal = v;
            } catch (Exception e) {
            }
        } while (cursor.moveToNext());

        if (values.isEmpty() || minVal == Double.MAX_VALUE || maxVal <= minVal) {
            cursor.moveToFirst();
            return buildChartFromCursor(context, cursor, "Product Category Order Value Distribution");
        }

        int binCount = 5;
        double range = maxVal - minVal;
        double binWidth = range / binCount;
        if (binWidth <= 0) {
            cursor.moveToFirst();
            return buildChartFromCursor(context, cursor, "Product Category Order Value Distribution");
        }

        int[] binCounts = new int[binCount];

        for (double v : values) {
            int bin = (int) ((v - minVal) / binWidth);
            if (bin < 0) bin = 0;
            if (bin >= binCount) bin = binCount - 1;
            binCounts[bin]++;
        }

        XYSeries series = new XYSeries("Product Categories");

        String[] binLabels = new String[binCount];
        int maxY = 0;

        for (int i = 0; i < binCount; i++) {
            double low = minVal + i * binWidth;
            double high = (i == binCount - 1) ? maxVal : (minVal + (i + 1) * binWidth);

            binLabels[i] = String.format("%.0f–%.0f", low, high);

            int count = binCounts[i];
            series.add(i, count);
            if (count > maxY) maxY = count;
        }

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setChartTitle("Product Categories by Order Value (Binned)");
        renderer.setAxisTitleTextSize(30f);
        renderer.setYTitle("Number of Categories");
        renderer.setChartTitleTextSize(34f);
        renderer.setLabelsTextSize(24f);
        renderer.setLegendTextSize(24f);
        renderer.setMargins(new int[]{60, 120, 80, 40});
        renderer.setShowGrid(true);
        renderer.setZoomButtonsVisible(false);
        renderer.setPanEnabled(true, false);

        renderer.setBarWidth(40);
        renderer.setBarSpacing(10);

        renderer.setXLabels(0);
        for (int i = 0; i < binLabels.length; i++) {
            renderer.addXTextLabel(i, binLabels[i]);
        }
        renderer.setXLabelsAngle(30f);
        renderer.setXLabelsPadding(20f);

        renderer.setXAxisMin(-0.5);
        renderer.setXAxisMax(binLabels.length - 0.5);

        renderer.setYAxisMin(0);
        renderer.setYAxisMax(maxY * 1.15);
        renderer.setYLabelsPadding(35f);

        XYSeriesRenderer sRenderer = new XYSeriesRenderer();
        sRenderer.setColor(Color.BLUE);
        sRenderer.setDisplayChartValues(true);
        sRenderer.setChartValuesTextSize(22f);

        renderer.addSeriesRenderer(sRenderer);

        return ChartFactory.getBarChartView(
                context,
                dataset,
                renderer,
                BarChart.Type.DEFAULT
        );
    }*/

    /**
     * Custom chart for QUERY_9:
     * Tp + bottom 10 product categories by average review score.
     */
    public static GraphicalView buildQuery9TopBottomCategoryChart(Context context, Cursor cursor) {

        int nameIndex = 0;
        int valueIndex = cursor.getColumnIndex("avg_review_score");
        if (valueIndex == -1) {
            valueIndex = cursor.getColumnCount() - 1;
        }

        if (valueIndex < 0) {
            cursor.moveToFirst();
            return buildChartFromCursor(context, cursor, "Top/Bottom Product Categories by Review Score");
        }

        java.util.List<java.util.Map.Entry<String, Double>> rows = new java.util.ArrayList<>();

        do {
            try {
                String category = cursor.getString(nameIndex);
                double value = cursor.getDouble(valueIndex);

                if (category == null) category = "(Unknown)";
                rows.add(new java.util.AbstractMap.SimpleEntry<>(category, value));

            } catch (Exception ignored) { }
        } while (cursor.moveToNext());

        if (rows.isEmpty()) {
            cursor.moveToFirst();
            return buildChartFromCursor(context, cursor, "Top/Bottom Product Categories by Review Score");
        }

        java.util.Collections.sort(rows, (a, b) ->
                Double.compare(b.getValue(), a.getValue())
        );

        int total = rows.size();
        int topCount = Math.min(10, total);
        int bottomCount = Math.min(10, total - topCount);

        XYSeries topSeries = new XYSeries("Top 10");
        XYSeries bottomSeries = new XYSeries("Bottom 10");
        java.util.List<String> labels = new java.util.ArrayList<>();

        double maxY = 0;
        int xIndex = 0;

        for (int i = 0; i < topCount; i++) {
            String cat = rows.get(i).getKey();
            double val = rows.get(i).getValue();

            labels.add(cat);
            topSeries.add(xIndex, val);

            if (val > maxY) maxY = val;
            xIndex++;
        }

        int startBottom = total - bottomCount;
        for (int i = startBottom; i < total; i++) {
            String cat = rows.get(i).getKey();
            double val = rows.get(i).getValue();

            labels.add(cat);
            bottomSeries.add(xIndex, val);

            if (val > maxY) maxY = val;
            xIndex++;
        }

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(topSeries);
        dataset.addSeries(bottomSeries);

        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setChartTitle("Top 10 & Bottom 10 Categories by Avg Review Score");
        renderer.setChartTitleTextSize(38f);
        renderer.setLabelsTextSize(22f);
        renderer.setLegendTextSize(26f);
        renderer.setMargins(new int[]{80, 120, 220, 40});
        renderer.setShowGrid(true);
        renderer.setZoomButtonsVisible(false);
        renderer.setPanEnabled(true, false);

        renderer.setBarWidth(25);
        renderer.setBarSpacing(4);

        renderer.setXLabels(0);
        for (int i = 0; i < labels.size(); i++) {
            renderer.addXTextLabel(i, labels.get(i));
        }
        renderer.setXLabelsAngle(90f);
        renderer.setXLabelsPadding(150f);

        renderer.setXAxisMin(-0.8);
        renderer.setXAxisMax(labels.size() - 0.2);

        renderer.setYAxisMin(0);
        renderer.setYAxisMax(maxY * 1.15);
        renderer.setYLabelsPadding(40f);

        XYSeriesRenderer topRenderer = new XYSeriesRenderer();
        topRenderer.setColor(Color.GREEN);
        topRenderer.setDisplayChartValues(true);
        topRenderer.setChartValuesTextSize(24f);

        XYSeriesRenderer bottomRenderer = new XYSeriesRenderer();
        bottomRenderer.setColor(Color.RED);
        bottomRenderer.setDisplayChartValues(true);
        bottomRenderer.setChartValuesTextSize(24f);

        renderer.addSeriesRenderer(topRenderer);
        renderer.addSeriesRenderer(bottomRenderer);

        return ChartFactory.getBarChartView(
                context,
                dataset,
                renderer,
                BarChart.Type.DEFAULT
        );
    }



    /*public static GraphicalView buildQuery9BinnedCategoryReviewChart(Context context, Cursor cursor) {

        int reviewIndex = cursor.getColumnIndex("avg_review_score");
        if (reviewIndex == -1) {
            reviewIndex = cursor.getColumnCount() - 1;
        }
        if (reviewIndex < 0) {
            cursor.moveToFirst();
            return buildChartFromCursor(context, cursor, "Product Categories by Review Score");
        }

        int[] counts = new int[5];

        do {
            try {
                double score = cursor.getDouble(reviewIndex);

                int rounded = (int) Math.round(score);
                if (rounded < 1) rounded = 1;
                if (rounded > 5) rounded = 5;

                counts[rounded - 1]++;

            } catch (Exception e) {
                Log.e("QueryActivity", "Error parsing Query 9 row", e);
            }
        } while (cursor.moveToNext());

        XYSeries series = new XYSeries("Product Categories");
        String[] labels = {"1", "2", "3", "4", "5"};

        int maxY = 0;
        for (int i = 0; i < counts.length; i++) {
            series.add(i, counts[i]);
            if (counts[i] > maxY) maxY = counts[i];
        }

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setChartTitle("Product Categories by Avg Review Score");
        renderer.setAxisTitleTextSize(30f);
        renderer.setYTitle("Number of Categories");
        renderer.setChartTitleTextSize(34f);
        renderer.setLabelsTextSize(24f);
        renderer.setLegendTextSize(24f);
        renderer.setMargins(new int[]{50, 120, 80, 40});
        renderer.setShowGrid(true);
        renderer.setZoomButtonsVisible(false);
        renderer.setPanEnabled(true, false);

        renderer.setBarWidth(40);
        renderer.setBarSpacing(5);

        renderer.setXLabels(0);
        for (int i = 0; i < labels.length; i++) {
            renderer.addXTextLabel(i, labels[i]);
        }
        renderer.setXLabelsAngle(0);
        renderer.setXLabelsPadding(20f);

        renderer.setXAxisMin(-0.5);
        renderer.setXAxisMax(labels.length - 0.5);

        renderer.setYAxisMin(0);
        renderer.setYAxisMax(maxY * 1.2);
        renderer.setYLabelsPadding(35f);

        XYSeriesRenderer sRenderer = new XYSeriesRenderer();
        sRenderer.setColor(Color.BLUE);
        sRenderer.setDisplayChartValues(true);
        sRenderer.setChartValuesTextSize(22f);

        renderer.addSeriesRenderer(sRenderer);

        return ChartFactory.getBarChartView(
                context,
                dataset,
                renderer,
                BarChart.Type.DEFAULT
        );
    }*/

    /**
     * Custom chart for QUERY_10:
     * Bins sellers by total revenue.
     */
    public static GraphicalView buildQuery10BinnedSellerRevenueChart(Context context, Cursor cursor) {

        int revenueIndex = cursor.getColumnCount() - 1;
        if (revenueIndex < 0) {
            cursor.moveToFirst();
            return buildChartFromCursor(context, cursor, "Seller Revenue Distribution");
        }
        int[] bins = new int[5];

        do {
            try {
                double revenue = cursor.getDouble(revenueIndex);

                if (revenue < 10_000)       bins[0]++;
                else if (revenue < 50_000)  bins[1]++;
                else if (revenue < 200_000) bins[2]++;
                else if (revenue < 1_000_000) bins[3]++;
                else                        bins[4]++;

            } catch (Exception e) {
                Log.e("QueryActivity", "Query 10 revenue parse error", e);
            }
        } while (cursor.moveToNext());

        XYSeries series = new XYSeries("Sellers");

        String[] labels = {
                "0–10K",
                "10K–50K",
                "50K–200K",
                "200K–1M",
                "1M+"
        };

        int maxY = 0;
        for (int i = 0; i < bins.length; i++) {
            series.add(i, bins[i]);
            if (bins[i] > maxY) maxY = bins[i];
        }

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setChartTitle("Seller Revenue Distribution (Binned)");
        renderer.setChartTitleTextSize(38f);
        renderer.setYTitle("Number of Sellers");
        renderer.setAxisTitleTextSize(32f);
        renderer.setLabelsTextSize(26f);
        renderer.setLegendTextSize(26f);
        renderer.setMargins(new int[]{60, 130, 80, 60});
        renderer.setShowGrid(true);
        renderer.setZoomButtonsVisible(false);
        renderer.setPanEnabled(true, false);

        renderer.setBarWidth(35);
        renderer.setBarSpacing(0);

        renderer.setXLabels(0);
        for (int i = 0; i < labels.length; i++) {
            renderer.addXTextLabel(i, labels[i]);
        }

        renderer.setXLabelsAngle(0);
        renderer.setXLabelsPadding(25f);

        renderer.setXAxisMin(-0.5);
        renderer.setXAxisMax(labels.length - 0.5);

        renderer.setYAxisMin(0);
        renderer.setYAxisMax(maxY * 1.15);
        renderer.setYLabelsPadding(40f);

        XYSeriesRenderer sRenderer = new XYSeriesRenderer();
        sRenderer.setColor(Color.BLUE);
        sRenderer.setDisplayChartValues(true);
        sRenderer.setChartValuesTextSize(24f);

        renderer.addSeriesRenderer(sRenderer);

        return ChartFactory.getBarChartView(
                context,
                dataset,
                renderer,
                BarChart.Type.DEFAULT
        );
    }

    /**
     * Custom chart for QUERY_11:
     * Bins states by delivery time.
     */
    public static GraphicalView buildQuery11BinnedDeliveryChart(Context context, Cursor cursor) {

        int avgIndex = cursor.getColumnIndex("average_delivery_time");
        if (avgIndex == -1) {
            avgIndex = cursor.getColumnCount() - 1;
        }

        int[] bins = new int[4];

        do {
            try {
                double avg = cursor.getDouble(avgIndex);

                if (avg <= 5)          bins[0]++;
                else if (avg <= 10)   bins[1]++;
                else if (avg <= 15)   bins[2]++;
                else                  bins[3]++;

            } catch (Exception e) {
                Log.e("QueryActivity", "Error parsing Query 11 row", e);
            }
        } while (cursor.moveToNext());

        String[] labels = {
                "0–5 Days",
                "6–10 Days",
                "11–15 Days",
                "16+ Days"
        };

        XYSeries series = new XYSeries("States");
        int maxY = 0;

        for (int i = 0; i < bins.length; i++) {
            series.add(i, bins[i]);
            if (bins[i] > maxY) maxY = bins[i];
        }

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setChartTitle("State Delivery Speed Distribution");
        renderer.setAxisTitleTextSize(32f);
        renderer.setYTitle("Number of States");
        renderer.setChartTitleTextSize(36f);
        renderer.setLabelsTextSize(26f);
        renderer.setLegendTextSize(30f);
        renderer.setMargins(new int[]{70, 120, 80, 60});
        renderer.setShowGrid(true);

        renderer.setBarWidth(35);
        renderer.setBarSpacing(10);
        renderer.setZoomButtonsVisible(false);
        renderer.setPanEnabled(true, false);

        renderer.setXAxisMin(-0.5);
        renderer.setXAxisMax(labels.length - 0.5);

        renderer.setXLabels(0);
        renderer.setXLabelsAngle(45f);
        renderer.setXLabelsPadding(35f);

        renderer.setYAxisMin(0);
        renderer.setYAxisMax(maxY * 1.2);
        renderer.setYLabelsPadding(40f);

        for (int i = 0; i < labels.length; i++) {
            renderer.addXTextLabel(i, labels[i]);
        }

        XYSeriesRenderer sRenderer = new XYSeriesRenderer();
        sRenderer.setColor(Color.BLUE);
        sRenderer.setDisplayChartValues(true);
        sRenderer.setChartValuesTextSize(26f);

        renderer.addSeriesRenderer(sRenderer);

        return ChartFactory.getBarChartView(
                context,
                dataset,
                renderer,
                BarChart.Type.DEFAULT
        );
    }

    /**
     * Special chart for QUERY_13
     * Total customers by state
     */
    public static GraphicalView buildQuery13CustomersByStateChart(Context context, Cursor cursor) {

        int stateIndex = cursor.getColumnIndex("state");
        int totalIndex = cursor.getColumnIndex("total_customers");

        if (stateIndex == -1) stateIndex = 0;
        if (totalIndex == -1) totalIndex = cursor.getColumnCount() - 1;

        final int TOP_N = 10;

        XYSeries series = new XYSeries("Total Customers");
        java.util.List<String> labels = new java.util.ArrayList<>();

        int rowCount = 0;
        double otherTotal = 0;
        double maxY = 0;
        int xIndex = 0;

        do {
            try {
                String state = cursor.getString(stateIndex);
                double total = cursor.getDouble(totalIndex);

                if (state == null) state = "(Unknown)";

                if (rowCount < TOP_N) {
                    labels.add(state);
                    series.add(xIndex, total);
                    if (total > maxY) maxY = total;
                    xIndex++;
                } else {
                    otherTotal += total;
                }

                rowCount++;

            } catch (Exception e) {
                Log.e("QueryActivity", "Query 13 parse error", e);
            }

        } while (cursor.moveToNext());

        if (otherTotal > 0) {
            labels.add("Other");
            series.add(xIndex, otherTotal);
            if (otherTotal > maxY) maxY = otherTotal;
        }

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setChartTitle("Total Customers by State (Top 10 + Other)");
        renderer.setChartTitleTextSize(34f);
        renderer.setLabelsTextSize(22f);
        renderer.setLegendTextSize(24f);
        renderer.setMargins(new int[]{60, 120, 70, 40});
        renderer.setShowGrid(true);
        renderer.setZoomButtonsVisible(false);
        renderer.setPanEnabled(true, false);

        renderer.setBarWidth(40);
        renderer.setBarSpacing(20);

        renderer.setXLabels(0);
        for (int i = 0; i < labels.size(); i++) {
            renderer.addXTextLabel(i, labels.get(i));
        }
        renderer.setXLabelsAngle(45f);
        renderer.setXLabelsPadding(25f);
        renderer.setYLabelsPadding(45f);

        renderer.setXAxisMin(-0.5);
        renderer.setXAxisMax(labels.size() - 0.5);

        renderer.setYAxisMin(0);
        renderer.setYAxisMax(maxY * 1.15);

        XYSeriesRenderer sRenderer = new XYSeriesRenderer();
        sRenderer.setDisplayChartValues(true);
        sRenderer.setChartValuesTextSize(24f);
        sRenderer.setChartValuesFormat(new java.text.DecimalFormat("#,###"));
        sRenderer.setColor(Color.BLUE);

        renderer.addSeriesRenderer(sRenderer);

        return ChartFactory.getBarChartView(
                context,
                dataset,
                renderer,
                BarChart.Type.DEFAULT
        );
    }
}

