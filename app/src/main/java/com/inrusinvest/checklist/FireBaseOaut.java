package com.inrusinvest.checklist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;


public class FireBaseOaut extends BaseActivity implements
        View.OnClickListener {


    JSONParser jsonParser = new JSONParser();
    PDialog pDialog = new PDialog();
    private static final String TAG_SUCCESS = "success";
    private static final String url_get_user = "http://46.149.225.24:8081/checklist/get_user.php";


    private static final String TAG = "EmailPassword";
    private TextView mStatusTextView; //объявляем поля и авторизацию
    private TextView mDetailTextView;
    private EditText mEmailField;
    private EditText mPasswordField;


    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_base_oaut);

        mStatusTextView = findViewById(R.id.status);  //определяем поля по id
        mDetailTextView = findViewById(R.id.detail);
        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);

        findViewById(R.id.emailSignInButton).setOnClickListener(this);  //определяем кнопки, добавляем слушателей
        findViewById(R.id.emailCreateAccountButton).setOnClickListener(this);
        //findViewById(R.id.signOutButton).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();   //инициализируем авторизацию
    }

    @Override
    public void onStart() {
        super.onStart();
        // Проверяем зареган ли юзер
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        hideProgressDialog();
    }

    private void createAccount(String email, String password) { //Проверка на заполненность полей
        Log.d(TAG, "createAccount:" + email);
        if (!validateRegForm()) {
            return;
        }

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)    //создаем нвоого пользователя, если успешно - кидаем на форму с подтверждением входа, не успешно - ошибка
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @SuppressLint("StringFormatMatches")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            /*mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
                                    user.getEmail()));

                            findViewById(R.id.emailPasswordButtons).setVisibility(View.GONE);
                            findViewById(R.id.emailPasswordFields).setVisibility(View.GONE);
                            findViewById(R.id.signedInButtons).setVisibility(View.VISIBLE);*/

                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(FireBaseOaut.this, "Ошибка регистрации",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        hideProgressDialog();
                    }
                });

    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password) //Авторизация по логину и паролю, если успешно, перекидываем на главную активити, не успешно - ошибка
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(FireBaseOaut.this, "Ошибка авторизации",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }


                        if (!task.isSuccessful()) {
                            mStatusTextView.setText(R.string.auth_failed);
                        }
                    }
                });
    }


    private boolean validateForm() {  //проверка на заполненность полей при авторизации
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Введите вашу почту");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Введите пароль");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private boolean validateRegForm() {  //проверка на заполненность полей при регистрации
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Введите вашу почту");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Придумайте пароль");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void updateUI(final FirebaseUser user) { //обновление инфы о зареганом юзере, перекидывание на активити со счетчиками

        if (user != null) {
            showProgressDialog();

            DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
            db.addValueEventListener(new ValueEventListener() {
                @SuppressLint("StringFormatMatches")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    new UserOaut().execute();
                   /* String val = dataSnapshot.child(user.getUid()).child("access").getValue(String.class);
                    if (val != null) {
                        hideProgressDialog();
                        Intent intent = new Intent(getApplicationContext(), CompanyGet.class);
                        intent.putExtra("uid_user", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                        startActivity(intent);
                    } else {
                        hideProgressDialog();
                        Intent intent = new Intent(getApplicationContext(), NewUserActivity.class);
                        startActivity(intent);
                    }*/
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);

            findViewById(R.id.emailPasswordButtons).setVisibility(View.VISIBLE);
            findViewById(R.id.emailPasswordFields).setVisibility(View.VISIBLE);
            findViewById(R.id.signedInButtons).setVisibility(View.GONE);
        }

    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.emailCreateAccountButton) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
            //createAccount(crMailField.getText().toString(), crPasswordField.getText().toString());
        } else if (i == R.id.emailSignInButton) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        }
    }


    @SuppressLint("StaticFieldLeak")
    class UserOaut extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //pDialog.run(FireBaseOaut.this);
        }

        protected String doInBackground(String... args) {

            // Создаем новый HashMap
            HashMap<String, String> map = new HashMap<>();

            map.put("uid", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
            Log.d("uid parsing:", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

            // получаем JSON строк с URL

            JSONObject json = jsonParser.makeHttpRequest(url_get_user, "GET", map);


            Log.d("Есть ли пользователь: ", json.toString());

            try {
                // Получаем SUCCESS тег для проверки статуса ответа сервера
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // продукт найден
                    // Получаем масив
                    Log.d("Status", "Success");
                    hideProgressDialog();
                    Intent intent = new Intent(getApplicationContext(), CompanyGet.class);
                    intent.putExtra("uid_user", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                    startActivity(intent);


                } else {
                    Log.d("Status", "Unsuccess");
                    hideProgressDialog();
                    Intent intent = new Intent(getApplicationContext(), NewUserActivity.class);
                    startActivity(intent);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // закрываем прогресс диалог
            //pDialog.end();
            // обновляем UI форму в фоновом потоке
            /*runOnUiThread(new Runnable() {
                public void run() {

                    ListAdapter adapter = new SimpleAdapter(
                            CompanyGet.this, companyList,
                            R.layout.list_item, new String[]{TAG_PID,
                            TAG_COMPANY_NAME},
                            new int[]{R.id.pid, R.id.name});
                    // обновляем listview
                    lv.setAdapter(adapter);
                }
            });*/
        }
    }

    public void onDestroy() {
        moveTaskToBack(true);

        super.onDestroy();

        System.runFinalizersOnExit(true);
        System.exit(0);
    }
}
