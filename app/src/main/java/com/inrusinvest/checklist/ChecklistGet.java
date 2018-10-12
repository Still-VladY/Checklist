package com.inrusinvest.checklist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ChecklistGet extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist_get);

        String[] mass = new String[] {"Какой-то чек-лист 1", "Какой-то чек-лист 2", "Какой-то чек-лист 3"};


        ListView lv = findViewById(R.id.parent_list_check);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.list_item_view, mass);
        lv.setAdapter(arrayAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String xx = parent.getAdapter().getItem((int) id).toString();
                Intent intent = new Intent(ChecklistGet.this, ListOpen.class);
                startActivity(intent);
            }
        });
    }
}
