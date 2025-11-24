package com.example.mis571groupproject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import com.example.mis571groupproject.constant.SQLCommand;
import com.example.mis571groupproject.util.DBOperator;
import com.example.mis571groupproject.util.Pair;
import com.example.mis571groupproject.view.ChartGenerator;
import com.example.mis571groupproject.view.TableView;

import java.util.ArrayList;

public class QueryActivity extends Activity implements OnClickListener {

    Button backBtn, resultBtn;
    Spinner querySpinner;
    ScrollView scrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query);

        // Copy database
        try {
            DBOperator.copyDB(getBaseContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        backBtn = findViewById(R.id.goback_btn);
        backBtn.setOnClickListener(this);

        resultBtn = findViewById(R.id.showresult_btn);
        resultBtn.setOnClickListener(this);

        querySpinner = findViewById(R.id.querylist_spinner);
        scrollView = findViewById(R.id.scrollview_queryresults);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.showresult_btn) {
            try {
                int pos = querySpinner.getSelectedItemPosition();
                if (pos == Spinner.INVALID_POSITION) {
                    Toast.makeText(this, "Please choose a query!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // This is where the change happens.
                // Instead of resultsContainer.removeAllViews(), you clear the ScrollView.
                scrollView.removeAllViews();

                String sql = "";
                if (pos == 0) sql = SQLCommand.QUERY_1;
                else if (pos == 1) sql = SQLCommand.QUERY_2;
                else if (pos == 2) sql = SQLCommand.QUERY_3;
                else if (pos == 3) sql = SQLCommand.QUERY_4;
                else if (pos == 4) sql = SQLCommand.QUERY_5;
                else if (pos == 5) sql = SQLCommand.QUERY_6;
                else if (pos == 6) sql = SQLCommand.QUERY_7;
                else if (pos == 7) sql = SQLCommand.QUERY_8;
                else if (pos == 8) sql = SQLCommand.QUERY_9;
                else if (pos == 9) sql = SQLCommand.QUERY_10;
                else if (pos == 10) sql = SQLCommand.QUERY_11;
                else if (pos == 11) sql = SQLCommand.QUERY_12;
                else if (pos == 12) sql = SQLCommand.QUERY_13;

                Log.d("QueryActivity", "Executing SQL: " + sql);

                // --- THIS IS THE REPLACEMENT LOGIC ---
                // 1. Execute the query
                Cursor cursor = DBOperator.getInstance().execQuery(sql);

                // 2. Check if the cursor is valid
                if (cursor != null) {
                    // 3. Create your new result view
                    TableView resultView = new TableView(this, cursor);

                    // 4. Add the new view to the ScrollView
                    scrollView.addView(resultView);
                } else {
                    // Handle the case where the query fails
                    Toast.makeText(this, "Error executing query.", Toast.LENGTH_LONG).show();
                }
                // --- END OF REPLACEMENT ---

            } catch (Exception e) {
                Log.e("QueryActivity", "Error executing query", e);
                Toast.makeText(this, "Failed to display results. Check logs.", Toast.LENGTH_LONG).show();
            }

        } else if (id == R.id.goback_btn) {
            Intent intent = new Intent(this, GroupActivity.class);
            startActivity(intent);
        }
    }

//    @Override
//    public void onClick(View v) {
//        int id = v.getId();
//
//        if (id == R.id.showresult_btn) {
//            try {
//                int pos = querySpinner.getSelectedItemPosition();
//                if (pos == Spinner.INVALID_POSITION) {
//                    Toast.makeText(this, "Please choose a query!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                scrollView.removeAllViews();
//
//                String sql = "";
//                if (pos == 0) sql = SQLCommand.QUERY_1;
//                else if (pos == 1) sql = SQLCommand.QUERY_2;
//                else if (pos == 2) sql = SQLCommand.QUERY_3;
//                else if (pos == 3) sql = SQLCommand.QUERY_4;
//                else if (pos == 4) sql = SQLCommand.QUERY_5;
//                else if (pos == 5) sql = SQLCommand.QUERY_6;
//                else if (pos == 6) sql = SQLCommand.QUERY_7;
//                else if (pos == 7) sql = SQLCommand.QUERY_8;
//                else if (pos == 8) sql = SQLCommand.QUERY_9;
//                else if (pos == 9) sql = SQLCommand.QUERY_10;
//                else if (pos == 10) sql = SQLCommand.QUERY_11;
//                else if (pos == 11) sql = SQLCommand.QUERY_12;
//                else if (pos == 12) sql = SQLCommand.QUERY_13;
//
//
//                Log.d("QueryActivity", "Executing SQL: " + sql);
//
////                // Special case: CHECKOUT_SUMMARY shows chart
////                if (pos == 8) {
////                    Cursor cursor = DBOperator.
////                            getInstance().execQuery(
////                                    SQLCommand.
////                                            CHECKOUT_SUMMARY);
////                    List<Pair> pairList = new LinkedList<Pair>();
////                    for (int i = 1; i <= 12; i++) {
////                        Pair pair = new Pair(i, 0);
////                        pairList.add(pair);
////                    }
////                    while (cursor.moveToNext()) {
////                        int location = Integer.
////                                parseInt(cursor.getString(0));
////                        pairList.get(location - 1).setNumber(
////                                Double.
////                                        parseDouble(cursor.getString(1)));
////                    }
////                    Intent intent = ChartGenerator.
////                            getBarChart(getBaseContext(),
////                                    "Checkout Summary in 2019", pairList);
////                    this.startActivity(intent);
////                    }
////            else {
//                // Default: display results in table
////                Cursor cursor = DBOperator.getInstance().execQuery(sql);
////                if (cursor != null) {
////                    scrollView.addView(new TableView(this, cursor));
////                } else {
////                    Toast.makeText(this, "Error executing query.", Toast.LENGTH_LONG).show();
////                }
//
////                }
//
//            } catch (Exception e) {
//                Log.e("QueryActivity", "Error executing query", e);
//                Toast.makeText(this, "Failed to display results. Check logs.", Toast.LENGTH_LONG).show();
//            }
//
//        } else if (id == R.id.goback_btn) {
//            Intent intent = new Intent(this, GroupActivity.class);
//            startActivity(intent);
//        }
//    }
}
