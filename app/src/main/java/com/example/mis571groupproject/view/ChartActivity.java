package com.example.mis571groupproject.view;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mis571groupproject.util.DBOperator;
import com.example.mis571groupproject.QueryActivity;
import com.example.mis571groupproject.R;

import org.achartengine.GraphicalView;

public class ChartActivity extends Activity {

    TextView chartTitleView;
    LinearLayout chartContainer;
    Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_page);

        chartTitleView = findViewById(R.id.chart_title);
        chartContainer = findViewById(R.id.chart_container);
        backBtn = findViewById(R.id.chart_back_btn);

        backBtn.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        int pos = intent.getIntExtra("queryPos", -1);
        String title = intent.getStringExtra("chartTitle");
        String sql = intent.getStringExtra("sql");

        if (title != null) {
            chartTitleView.setText(title);
        }

        if (pos == -1 || sql == null || sql.isEmpty()) {
            Toast.makeText(this, "No query selected for chart.", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            Cursor cursor = DBOperator.getInstance().execQuery(sql);
            if (cursor != null && cursor.moveToFirst()) {

                GraphicalView chartView;

                switch (pos) {
                    case 0:
                        chartView = QueryActivity.buildQuery1ReviewHistogram(this, cursor);
                        break;
                    case 1:
                        chartView = QueryActivity.buildQuery2CategoryChart(this, cursor);
                        break;
                    case 2:
                        chartView = QueryActivity.buildQuery3CategoryChart(this, cursor);
                        break;
                    case 3:
                        chartView = QueryActivity.buildQuery4DeliveryHistogram(this, cursor);
                        break;
                    case 4:
                        chartView = QueryActivity.buildQuery5SalesOrdersChart(this, cursor);
                        break;
                    case 6:
                        chartView = QueryActivity.buildQuery7CustomerOrdersChart(this, cursor);
                        break;
                    case 7:
                        chartView = QueryActivity.buildQuery8BinnedCategoryValueChart(this, cursor);
                        break;
                    case 8:
                        chartView = QueryActivity.buildQuery9BinnedCategoryReviewChart(this, cursor);
                        break;
                    case 9:
                        chartView = QueryActivity.buildQuery10BinnedSellerRevenueChart(this, cursor);
                        break;
                    case 10:
                        chartView = QueryActivity.buildQuery11BinnedDeliveryChart(this, cursor);
                        break;
                    case 11:
                        chartView = QueryActivity.buildQuery12HourlyChart(this, cursor);
                        break;
                    case 12:
                        chartView = QueryActivity.buildQuery13CustomersByStateChart(this, cursor);
                        break;
                    default:
                        chartView = QueryActivity.buildChartFromCursor(this, cursor, title);
                        break;
                }

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                chartView.setLayoutParams(lp);

                chartContainer.removeAllViews();
                chartContainer.addView(chartView);

                cursor.close();

            } else {
                Toast.makeText(this, "No data for chart.", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e("ChartActivity", "Error building chart", e);
            Toast.makeText(this, "Error building chart. See logs.", Toast.LENGTH_LONG).show();
        }
    }
}
