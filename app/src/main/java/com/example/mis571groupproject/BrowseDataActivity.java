package com.example.mis571groupproject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.mis571groupproject.util.DBOperator;
import com.example.mis571groupproject.util.Pair;
import com.example.mis571groupproject.view.ChartGenerator;

import java.util.ArrayList;

public class BrowseDataActivity extends Activity implements View.OnClickListener {

    Spinner tableSpinner;
    Button showTableBtn;
    Button summaryBtn;
    Button goBackBtn;
    TextView tableDisplay;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_data);

        tableSpinner = findViewById(R.id.tableSpinner);
        showTableBtn = findViewById(R.id.showTableBtn);
//        summaryBtn = findViewById(R.id.summary_btn);
        tableDisplay = findViewById(R.id.tableDisplay);
        goBackBtn = findViewById(R.id.goback_btn);

        showTableBtn.setOnClickListener(this);
//        summaryBtn.setOnClickListener(this);
        goBackBtn.setOnClickListener(this);

        try {
            DBOperator.copyDB(getBaseContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String dbPath = getDatabasePath("Brazilian_Ecommerce.db").getPath();
        db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);

        ArrayList<String> tables = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name!='android_metadata'", null);
        if (c.moveToFirst()) {
            do {
                tables.add(c.getString(0));
            } while (c.moveToNext());
        }
        c.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                tables
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tableSpinner.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.showTableBtn) {
            String tableName = tableSpinner.getSelectedItem().toString();
            displayTable(tableName);
////        } else if (id == R.id.summary_btn) {
////            String tableName = tableSpinner.getSelectedItem().toString();
////            if ("CheckOut".equals(tableName)) {
////                ArrayList<Pair> pairList = new ArrayList<>();
////                Cursor c = db.rawQuery(
////                        "SELECT month, count(*) FROM CheckOut GROUP BY month", null);
////                if (c.moveToFirst()) {
////                    do {
////                        int month = c.getInt(0); // assumes month is stored as integer 1-12
////                        double count = c.getDouble(1);
////                        pairList.add(new Pair(month, count));
////                    } while (c.moveToNext());
////                }
////                c.close();
////
////                Intent chartIntent = ChartGenerator.getBarChart(
////                        this, "CheckOuts per Month", pairList);
////                startActivity(chartIntent);
//            } else {
//                tableDisplay.setText("Chart summary is only available for the CheckOut table.");
//            }
        } else if (id == R.id.goback_btn) {
            Intent intent = new Intent(this, GroupActivity.class);
            startActivity(intent);
        }
    }

    private void displayTable(String tableName) {
        Cursor c = db.rawQuery("SELECT * FROM " + tableName, null);
        StringBuilder sb = new StringBuilder();

        for (String col : c.getColumnNames()) {
            sb.append(col).append("\t");
        }
        sb.append("\n");

        if (c.moveToFirst()) {
            do {
                for (int i = 0; i < c.getColumnCount(); i++) {
                    sb.append(c.getString(i)).append("\t");
                }
                sb.append("\n");
            } while (c.moveToNext());
        }

        c.close();
        tableDisplay.setText(sb.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
