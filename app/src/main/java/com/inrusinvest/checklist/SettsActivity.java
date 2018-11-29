package com.inrusinvest.checklist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setts);
        TextView tv = (TextView) findViewById(R.id.textAboutUser);


        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mAuth = FirebaseAuth.getInstance();
            tv.setText(getString(R.string.passwordless_status_fmt, user.getEmail()));
        }

    }

    public void onClick(View v) {

        mAuth.signOut();
        user = null;
        Intent intent = new Intent(this, FireBaseOaut.class);
        startActivity(intent);
    }


}