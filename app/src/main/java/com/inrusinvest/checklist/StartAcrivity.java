package com.inrusinvest.checklist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class StartAcrivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_acrivity);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            Intent intent = new Intent(StartAcrivity.this, CompanyGet.class);
            intent.putExtra("uid_user", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(StartAcrivity.this, FireBaseOaut.class);
            startActivity(intent);
            finish();
        }
    }
}
