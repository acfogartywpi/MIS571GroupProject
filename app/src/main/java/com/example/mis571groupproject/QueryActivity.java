package com.example.mis571groupproject;
import com.example.mis571groupproject.constant.SQLCommand;
import com.example.mis571groupproject.util.DBOperator;
import com.example.mis571groupproject.view.TableView;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;
import android.util.Log;
public class QueryActivity extends Activity implements
        OnClickListener {
    Button backBtn,resultBtn;
    Spinner querySpinner;
    ScrollView scrollView;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query);
//copy database file
        try{
            DBOperator.copyDB(getBaseContext());
        }catch(Exception e){
            e.printStackTrace();
        }
        backBtn=(Button)this.findViewById(R.id.goback_btn);
        backBtn.setOnClickListener(this);
        resultBtn=(Button)this.findViewById(R.id.showresult_btn);
        resultBtn.setOnClickListener(this);
        querySpinner=(Spinner)this.findViewById(R.id.querylist_spinner);
        scrollView=(ScrollView)this.findViewById(R.id.scrollview_queryresults);
    }
    public void onClick(View v)
    {
        String sql="";
        int id=v.getId();
        if (id==R.id.showresult_btn){
            try { // <-- START of the try-catch block
                int pos = querySpinner.getSelectedItemPosition();
                if (pos == Spinner.INVALID_POSITION) {
                    Toast.makeText(this.getBaseContext(), "Please choose a query!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Log which query is being executed
                Log.d("QueryActivity", "Executing query for position: " + pos);

                scrollView.removeAllViews();

                if (pos == 0) {
                    sql = SQLCommand.QUERY_1;
                } else if (pos == 1) {
                    sql = SQLCommand.QUERY_2;
                } else if (pos == 2) {
                    sql = SQLCommand.QUERY_3;
                } else if (pos == 3) {
                    sql = SQLCommand.QUERY_4;
                } else if (pos == 4) {
                    sql = SQLCommand.QUERY_5;
                } else if (pos == 5) {
                    sql = SQLCommand.QUERY_6;
                } else if (pos == 6) {
                    sql = SQLCommand.QUERY_7;
                }

                // Log the actual SQL command
                Log.d("QueryActivity", "SQL to execute: " + sql);

                Cursor cursor = DBOperator.getInstance().execQuery(sql);

                // Check if the cursor is null, which DBOperator might return on error
                if (cursor == null) {
                    Log.e("QueryActivity", "The cursor is null. The query likely failed in DBOperator.");
                    Toast.makeText(this, "Error executing query.", Toast.LENGTH_LONG).show();
                    return;
                }

                scrollView.addView(new TableView(this.getBaseContext(), cursor));

            } catch (Exception e) { // <-- CATCH the exception
                // Log the full error stack trace to Logcat
                Log.e("QueryActivity", "An error occurred in showresult_btn onClick", e);

                // Show a user-friendly error message
                Toast.makeText(this, "Failed to display results. Check logs for details.", Toast.LENGTH_LONG).show();
            }
        }else if (id==R.id.goback_btn){
//go back to main screen
            Intent intent = new Intent(this, GroupActivity.class);
            this.startActivity(intent);
        }
    }
}
