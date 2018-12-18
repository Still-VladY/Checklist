package com.inrusinvest.checklist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class CompanyGet extends AppCompatActivity {

    JSONParser jsonParser = new JSONParser();

    ArrayList<HashMap<String, String>> companyList;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_COMPANY = "company";
    private static final String TAG_PID = "id";
    private static final String TAG_COMPANY_NAME = "company_name";
    private static final String TAG_STATUS = "status_comp";
    private static final String TAG_MESSAGE = "message";
    private static final String url_get_company = "http://46.149.225.24:8081/checklist/get_company.php";
    //private static final String url_get_company = "http://192.168.100.23:8081/checklist/get_company.php";

    JSONArray company = null;
    PDialog pDialog = new PDialog();

    ListView lv;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        companyList = new ArrayList<HashMap<String, String>>();

        lv = findViewById(R.id.parent_list_org);
        new GetCompany().execute();

    }

    @SuppressLint("StaticFieldLeak")
    class GetCompany extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.run(CompanyGet.this);
        }


        protected String doInBackground(String... args) {

            HashMap<String, String> map = new HashMap<String, String>();

            String get_uid = null;

            Bundle arg = getIntent().getExtras();

            if (arg != null) {
                get_uid = arg.getString("uid_user");   // ПОЛУЧИЛ UID, далее в чеклист, далее в вопросы
                map.put("uid", "\'"+get_uid+"\'");
            }

            final String put_uid = get_uid;

            JSONObject json = jsonParser.makeHttpRequest(url_get_company, "GET", map);

            if (get_uid != null) {
                Log.d("uid: ", get_uid);
            }
            Log.d("All Companies: ", json.toString());

            try {
                // Получаем SUCCESS тег для проверки статуса ответа сервера
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // продукт найден
                    // Получаем масив
                    company = json.getJSONArray(TAG_COMPANY);

                    // перебор
                    for (int i = 0; i < company.length(); i++) {
                        JSONObject c = company.getJSONObject(i);
                        String status = c.getString(TAG_STATUS);
                        //System.out.println("Статус - "+status);

                        if (status.equals("1")) {
                            // Сохраняем каждый json элемент в переменную
                            String id = c.getString(TAG_PID);
                            String name = c.getString(TAG_COMPANY_NAME);
                            //System.out.print(name + "\n");

                            HashMap<String, String> map2list = new HashMap<String, String>();

                            // добавляем каждый елемент в HashMap ключ => значение
                            map2list.put(TAG_PID, id);
                            map2list.put(TAG_COMPANY_NAME, name);

                            // добавляем HashList в ArrayList
                            companyList.add(map2list);

                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {

                                    String pid = ((TextView) view.findViewById(R.id.pid)).getText()
                                            .toString();
                                    System.out.println(pid);
                                    Intent intent = new Intent(CompanyGet.this, ChecklistGet.class);
                                    intent.putExtra("company_id", pid);
                                    intent.putExtra("uid_user", put_uid);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);

                                }
                            });

                        } //else //Toast.makeText(getApplicationContext(), "Нет доступных организаций", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    HashMap<String, String> map2list = new HashMap<String, String>();

                    // добавляем каждый елемент в HashMap ключ => значение
                    String name = json.getString(TAG_MESSAGE);
                    map2list.put(TAG_COMPANY_NAME, name);
                    // добавляем HashList в ArrayList
                    companyList.add(map2list);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // закрываем прогресс диалог
            pDialog.end();
            // обновляем UI форму в фоновом потоке
            runOnUiThread(new Runnable() {
                public void run() {

                    ListAdapter adapter = new SimpleAdapter(
                            CompanyGet.this, companyList,
                            R.layout.list_item, new String[]{TAG_PID,
                            TAG_COMPANY_NAME},
                            new int[]{R.id.pid, R.id.name});
                    // обновляем listview
                    lv.setAdapter(adapter);
                }
            });

        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.action_about:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onDestroy() {
        moveTaskToBack(true);

        super.onDestroy();

        System.runFinalizersOnExit(true);
        System.exit(0);
    }
}
