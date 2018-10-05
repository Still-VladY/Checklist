package com.inrusinvest.checklist;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] mass = new String[] {"Какая-то организация 1", "Какая-то организация 2", "Какая-то организация 3"};


        ListView lv = (ListView) findViewById(R.id.parent_list_org);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.list_item_view, mass);
        lv.setAdapter(arrayAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String xx = parent.getAdapter().getItem((int) id).toString();
                Intent intent = new Intent(MainActivity.this, ListOpen.class);
                intent.putExtra("company", xx);
                startActivity(intent);
            }
        });
    }
}
