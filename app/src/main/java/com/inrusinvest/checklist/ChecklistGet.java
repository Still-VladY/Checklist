package com.inrusinvest.checklist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class ChecklistGet extends AppCompatActivity {

    PDialog pDialog = new PDialog();

    JSONParser jsonParser = new JSONParser();

    ArrayList<HashMap<String, String>> checkList;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_CHECKLIST = "checklist";
    private static final String TAG_PID = "id";
    private static final String TAG_CHECKLIST_NAME = "checklist_name";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_ID_CHECKLIST = "id_checklist";
    private static final String url_get_checklist = "http://46.149.225.24:8081/checklist/get_checklist.php";
    //private static final String url_get_checklist = "http://192.168.100.23:8081/checklist/get_checklist.php";

    JSONArray checklist = null;

    ListView lv;
    String get_uid = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        checkList = new ArrayList<HashMap<String, String>>();

        lv = findViewById(R.id.parent_list_org);
        new GetChecklist().execute();

    }

    @SuppressLint("StaticFieldLeak")
    class GetChecklist extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.run(ChecklistGet.this);
        }


        protected String doInBackground(String... args) {
            // Создаем новый HashMap
            HashMap<String, String> map = new HashMap<>();

            String get_comp_id = null;
            Bundle arg = getIntent().getExtras();
            if (arg != null) {
                get_comp_id = arg.getString("company_id");
                get_uid = arg.getString("uid_user");
                //System.out.println("Ид выбранной компании - " + get_comp_id);

                map.put("id", get_comp_id);
                map.put("uid", "\'" + get_uid + "\'");
            }

            final String put_uid = get_uid;
            final String put_comp_id = get_comp_id;

            // получаем JSON строк с URL

            JSONObject json = jsonParser.makeHttpRequest(url_get_checklist, "GET", map);


            // Log.d("All Checklists: ", json.toString());

            try {
                // Получаем SUCCESS тег для проверки статуса ответа сервера
                int success = json.getInt(TAG_SUCCESS);


                if (success == 1) {
                    // Получаем масив
                    checklist = json.getJSONArray(TAG_CHECKLIST);
                    // перебор
                    for (int i = 0; i < checklist.length(); i++) {

                        JSONObject c = checklist.getJSONObject(i);
                        //System.out.println("Статус - " + status);

                        // Сохраняем каждый json элемент в переменную
                        String id = c.getString(TAG_ID_CHECKLIST);
                        String name = c.getString(TAG_CHECKLIST_NAME);
                        Log.d("Чеклисты", name);
                        HashMap<String, String> map2list = new HashMap<String, String>();

                        // добавляем каждый елемент в HashMap ключ => значение

                        map2list.put(TAG_CHECKLIST_NAME, name);
                        map2list.put(TAG_PID, id);
                        // добавляем HashList в ArrayList
                        checkList.add(map2list);

                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                String pid = ((TextView) view.findViewById(R.id.pid)).getText()
                                        .toString();
                                Log.d("Выбранный id чеклиста", pid);
                                Intent intent = new Intent(ChecklistGet.this, ListOpen.class);
                                intent.putExtra("checklist_id", pid);
                                intent.putExtra("uid_user", put_uid);
                                intent.putExtra("company_id", put_comp_id);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });

                    }
                } else {
                    HashMap<String, String> map2list = new HashMap<String, String>();

                    // добавляем каждый елемент в HashMap ключ => значение
                    String name = json.getString(TAG_MESSAGE);
                    map2list.put(TAG_CHECKLIST_NAME, name);
                    // добавляем HashList в ArrayList
                    checkList.add(map2list);
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
                            ChecklistGet.this, checkList,
                            R.layout.list_item, new String[]{TAG_PID,
                            TAG_CHECKLIST_NAME},
                            new int[]{R.id.pid, R.id.name});
                    // обновляем listview
                    lv.setAdapter(adapter);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChecklistGet.this, CompanyGet.class);
        intent.putExtra("uid_user", get_uid);
        startActivity(intent);
        finish();
    }
}
