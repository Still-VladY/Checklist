package com.inrusinvest.checklist;

import android.app.ProgressDialog;
import android.content.Context;

public class PDialog {

    private ProgressDialog progressDialog;

    public void run (Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Загрузка. Подождите...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void end() {
        progressDialog.dismiss();
    }
}
