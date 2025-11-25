package com.example.mis571groupproject;

import com.example.mis571groupproject.util.DBOperator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import andoird.database.Cursor;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

public class GroupActivity extends Activity implements OnClickListener {
    Button BrowseDataBtn, queryBtn;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize buttons
        BrowseDataBtn = findViewById(R.id.goBrowseData_btn);
        BrowseDataBtn.setOnClickListener(this);

        queryBtn = findViewById(R.id.goDoQuery_btn);
        queryBtn.setOnClickListener(this);

        // Copy database file
        try {
            DBOperator.copyDB(getBaseContext());
        } catch(Exception e){
            e.printStackTrace();
        }

        showTop5ReviewedProducts();
        showBottom5ReviewedProducts();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.goBrowseData_btn) {
            // Start BrowseDataActivity when Browse Data button is clicked
            Intent intent = new Intent(this, BrowseDataActivity.class);
            startActivity(intent);
        }

        if (id == R.id.goDoQuery_btn) {
            Intent intent = new Intent(this, QueryActivity.class);
            startActivity(intent);
        }
    }

    // TOP 5 REVIEWED PRODUCTS

    private void showTop5ReviewedProducts() {

        LinearLayout container = findViewById(R.id.chartTopReviews);
        if (container == null) return;

        String sql =
                "SELECT oi.product_id, AVG(orv.review_score) AS avg_score, COUNT(*) AS num_reviews " +
                        "FROM Order_Item oi " +
                        "JOIN Orders o ON o.order_id = oi.order_id " +
                        "JOIN Order_Review orv ON orv.order_id = o.order_id " +
                        "GROUP BY oi.product_id " +
                        "HAVING num_reviews > 5 " +
                        "ORDER BY avg_score DESC " +
                        "LIMIT 5";

        Cursor cursor = DBOperator.getInstance().execQuery(sql);
        if (cursor == null || cursor.getCount() == 0) return;

        CategorySeries series = new CategorySeries("Top Rated Products");

        if (cursor.moveToFirst()) {
            do {
                String productId = cursor.getString(0);
                double avgScore = cursor.getDouble(1);

                if (productId.length() > 6)
                    productId = productId.substring(0, 6) + "...";

                series.add(productId, avgScore);

            } while (cursor.moveToNext());
        }
        cursor.close();

        DefaultRenderer renderer = new DefaultRenderer();
        renderer.setChartTitle("Top 5 Highest Rated Products");
        renderer.setChartTitleTextSize(40);
        renderer.setLabelsTextSize(24);
        renderer.setLegendTextSize(24);
        renderer.setMargins(new int[]{20, 40, 20, 20});
        renderer.setShowLabels(true);

        int[] colors = {0xFF81C784, 0xFF64B5F6, 0xFF9575CD, 0xFFFFB74D, 0xFFE57373};
        for (int c : colors) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(c);
            renderer.addSeriesRenderer(r);
        }

        GraphicalView chartView =
                ChartFactory.getBarChartView(this, series, renderer,
                        org.achartengine.chart.BarChart.Type.DEFAULT);

        container.removeAllViews();
        container.addView(chartView);
    }

    // BOTTOM 5 REVIEWED PRODUCTS

    private void showBottom5ReviewedProducts() {

        LinearLayout container = findViewById(R.id.chartBottomReviews);
        if (container == null) return;

        String sql =
                "SELECT oi.product_id, AVG(orv.review_score) AS avg_score, COUNT(*) AS num_reviews " +
                        "FROM Order_Item oi " +
                        "JOIN Orders o ON o.order_id = oi.order_id " +
                        "JOIN Order_Review orv ON orv.order_id = o.order_id " +
                        "GROUP BY oi.product_id " +
                        "HAVING num_reviews > 5 " +
                        "ORDER BY avg_score ASC " +
                        "LIMIT 5";

        Cursor cursor = DBOperator.getInstance().execQuery(sql);
        if (cursor == null || cursor.getCount() == 0) return;

        CategorySeries series = new CategorySeries("Lowest Rated Products");

        if (cursor.moveToFirst()) {
            do {
                String productId = cursor.getString(0);
                double avgScore = cursor.getDouble(1);

                if (productId.length() > 6)
                    productId = productId.substring(0, 6) + "...";

                series.add(productId, avgScore);

            } while (cursor.moveToNext());
        }
        cursor.close();

        DefaultRenderer renderer = new DefaultRenderer();
        renderer.setChartTitle("Bottom 5 Lowest Rated Products");
        renderer.setChartTitleTextSize(40);
        renderer.setLabelsTextSize(24);
        renderer.setLegendTextSize(24);
        renderer.setMargins(new int[]{20, 40, 20, 20});
        renderer.setShowLabels(true);

        int[] colors = {0xFFE57373, 0xFFFF8A65, 0xFFFFB74D, 0xFFFFD54F, 0xFFFDD835};
        for (int c : colors) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(c);
            renderer.addSeriesRenderer(r);
        }

        GraphicalView chartView =
                ChartFactory.getBarChartView(this, series, renderer,
                        org.achartengine.chart.BarChart.Type.DEFAULT);

        container.removeAllViews();
        container.addView(chartView);
    }

}
