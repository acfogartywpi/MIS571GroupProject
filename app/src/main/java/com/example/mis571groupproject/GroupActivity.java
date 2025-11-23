package com.example.mis571groupproject;

import com.example.mis571groupproject.util.DBOperator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

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
}
