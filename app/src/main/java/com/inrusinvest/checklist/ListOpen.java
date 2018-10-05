package com.inrusinvest.checklist;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import java.util.Objects;

public class ListOpen extends AppCompatActivity {

    AlertDialog.Builder alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_open);

        TextView tv = (TextView) findViewById(R.id.text_organization);
        Bundle arg = getIntent().getExtras();
        if (arg != null) {
            String getcomp = Objects.requireNonNull(arg.get("company")).toString();
            tv.setText(getcomp);
        }

        alertDialog = new AlertDialog.Builder(ListOpen.this);
        alertDialog.setTitle("Продолжить?");
        alertDialog.setMessage("Для продолжения нажмите \"Далее\", либо, если необходимо сделать фото, нажмите \"Фото\"");
        alertDialog.setPositiveButton("Далее", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.setNegativeButton(" Фото", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = getIntent();

                intent.setComponent(null);
                intent.setPackage("com.google.android.GoogleCamera");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        alertDialog.setCancelable(true);
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

            }
        });
    }

    public void clickNo (View v) {
        alertDialog.show();
    }

    public void clickYes (View v) {

    }
}
