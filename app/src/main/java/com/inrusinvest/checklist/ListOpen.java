package com.inrusinvest.checklist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ListOpen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_open);

        TextView tv = (TextView) findViewById(R.id.text_organization);
        Bundle arg = getIntent().getExtras();
        if (arg != null) {
            String getcomp = arg.get("company").toString();
            tv.setText(getcomp);
        }
    }
}
