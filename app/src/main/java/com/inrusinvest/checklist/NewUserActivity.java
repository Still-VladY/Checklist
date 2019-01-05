package com.inrusinvest.checklist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

public class NewUserActivity extends AppCompatActivity implements
        View.OnClickListener  {

    private EditText crFamilyField;
    private EditText crNameField;
    private EditText crMiddleField;

    FirebaseUser user;

    PDialog pDialog = new PDialog();
    private static final String TAG_SUCCESS = "success";
    JSONParser jsonParser = new JSONParser();
    private static final String url = "http://46.149.225.24:8081/checklist/put_new_user.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        findViewById(R.id.newUserBnt).setOnClickListener(this);
        findViewById(R.id.signOutButton).setOnClickListener(this);

        crFamilyField = findViewById(R.id.crFamilyField);
        crNameField = findViewById(R.id.crNameField);
        crMiddleField = findViewById(R.id.crMiddleField);

        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if(i == R.id.newUserBnt) {
            if (validateDialog()){
                Toast.makeText(getApplicationContext(), "Успешно", Toast.LENGTH_SHORT).show();
                new UserOaut().execute();
            }
        } else if (i == R.id.signOutButton) {
            signOut();
        }
    }

    private void signOut() {  //метод выхода
        FirebaseAuth.getInstance().signOut();
        user = null;
        Intent intent = new Intent(NewUserActivity.this, FireBaseOaut.class);
        startActivity(intent);
    }

    private boolean validateDialog() {
        boolean valid = true;

        String name = crNameField.getText().toString();
        if (TextUtils.isEmpty(name)) {
            crNameField.setError("Введите ваше имя");
            valid = false;
        }

        String family = crFamilyField.getText().toString();
        if (TextUtils.isEmpty(family)) {
            crFamilyField.setError("Введите вашу фамилию");
            valid = false;
        }

        String middle = crMiddleField.getText().toString();
        if (TextUtils.isEmpty(middle)) {
            crMiddleField.setError("Введите ваше отчество");
            valid = false;
        }

        return valid;
    }


    @SuppressLint("StaticFieldLeak")
    class UserOaut extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.run(NewUserActivity.this);
        }

        @SuppressLint("ShowToast")
        protected String doInBackground(String... args) {

            // Создаем новый HashMap
            HashMap<String, String> map = new HashMap<String, String>();

            map.put("uid", "\'"+Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()+"\'");
            map.put("login_name", "\'"+FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0]+"\'");
            map.put("last_name", "\'"+crFamilyField.getText().toString()+"\'");
            map.put("first_name", "\'"+crNameField.getText().toString()+"\'");
            map.put("middle_name", "\'"+crMiddleField.getText().toString()+"\'");
            Log.d("uid parsing:", "\'"+Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()+"\'");
            Log.d("login parsing: ", Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail()).split("@")[0]);

            // получаем JSON строк с URL

            JSONObject json = jsonParser.makeHttpRequest(url, "GET", map);


            Log.d("Status: ", json.toString());

            try {
                // Получаем SUCCESS тег для проверки статуса ответа сервера
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    Log.d("Status", "Success");
                    /*DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
                    db.setValue(user.getUid()).;
                    db.child(user.getUid()).setValue("")*/
                    Intent intent = new Intent(NewUserActivity.this, CompanyGet.class);
                    intent.putExtra("uid_user", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                    startActivity(intent);
                } else {
                    Log.d("Status", "Unsuccess");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // закрываем прогресс диалог
            pDialog.end();
        }
    }
}
