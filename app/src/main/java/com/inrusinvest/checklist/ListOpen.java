package com.inrusinvest.checklist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class ListOpen extends Activity {

    PDialog pDialog = new PDialog();

    JSONParser jsonParser = new JSONParser();

    ArrayList<HashMap<String, String>> questList;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_QUESTION = "question";
    private static final String TAG_PID = "id";
    private static final String TAG_PID_QUEST = "id_qu";
    private static final String TAG_QUESTION_NAME = "question_name";
    private static final String TAG_STATUS = "status_quest";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_IFANSWER = "if_answer";
    private static final String url_get_checklist = "http://46.149.225.24:8081/checklist/get_question.php";
    private static final String url_open_checklist = "http://46.149.225.24:8081/checklist/run_ch.php";
    //private static final String url_get_checklist = "http://192.168.100.23:8081/checklist/get_question.php";
    //private static final String TAG_ID_CHECKLIST = "id_checklist";

    JSONArray question = null;

    ListView lv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new RunChecklist().execute();
        questList = new ArrayList<>();
        new GetQuestion().execute();

        lv = findViewById(R.id.parent_list_org);
    }


    String get_ch_id;
    String get_uid;
    String get_comp_id;

    @SuppressLint("StaticFieldLeak")
    class GetQuestion extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.run(ListOpen.this);

        }

        @SuppressLint("SetTextI18n")
        protected String doInBackground(String... args) {
            // Создаем новый HashMap
            HashMap<String, String> map = new HashMap<>();



            Bundle arg = getIntent().getExtras();
            if (arg != null) {
                get_ch_id = arg.getString("checklist_id");
                get_uid = arg.getString("uid_user");
                get_comp_id = arg.getString("company_id");


                map.put("id", get_ch_id);
                map.put("uid", "\'" + get_uid + "\'");
                Log.d("ИД чеклиста в вопр: ", get_ch_id);
                Log.d("ЮИД юзера в листах: ", get_uid);
            }

            // получаем JSON строк с URL

            JSONObject json = jsonParser.makeHttpRequest(url_get_checklist, "GET", map);


            //Log.d("Все вопросы: ", json.toString());


            try {
                // Получаем SUCCESS тег для проверки статуса ответа сервера
                int success = json.getInt(TAG_SUCCESS);


                if (success == 1) {
                    // Получаем масив
                    question = json.getJSONArray(TAG_QUESTION);
                    // перебор
                    for (int i = 0; i < question.length(); i++) {

                        JSONObject c = question.getJSONObject(i);
                        String status = c.getString(TAG_STATUS);
                        String if_answer = c.getString(TAG_IFANSWER);

                        if (status.equals("1")) {
                            // Сохраняем каждый json элемент в переменную
                            //String id = c.getString(TAG_PID_CH);
                            String id = c.getString(TAG_PID_QUEST);
                            String name = c.getString(TAG_QUESTION_NAME);

                            Log.d("Вопросы - ", name);

                            HashMap<String, String> map2list = new HashMap<String, String>();

                            // добавляем каждый елемент в HashMap ключ => значение

                            map2list.put(TAG_QUESTION_NAME, name);
                            map2list.put(TAG_PID, id);
                            // добавляем HashList в ArrayList
                            Log.d("МАП: ", map2list.toString());

                            questList.add(map2list);

                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {
                                    String pid = ((TextView) view.findViewById(R.id.pid)).getText()
                                            .toString();
                                    Log.d("Выбранный id вопроса:", pid);
                                    Log.d("Выбранный id чеклиста: ", get_ch_id);
                                    Intent intent = new Intent(ListOpen.this, Question_open.class);
                                    intent.putExtra("id_question", pid);
                                    intent.putExtra("checklist_id", get_ch_id);
                                    intent.putExtra("user_uid", get_uid);
                                    intent.putExtra("company_id", get_comp_id);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                } else {
                    HashMap<String, String> map2list = new HashMap<String, String>();

                    // добавляем каждый елемент в HashMap ключ => значение
                    String name = json.getString(TAG_MESSAGE);
                    map2list.put(TAG_QUESTION_NAME, name);
                    // добавляем HashList в ArrayList
                    questList.add(map2list);
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
                            ListOpen.this, questList,
                            R.layout.list_item, new String[]{TAG_PID,
                            TAG_QUESTION_NAME},
                            new int[]{R.id.pid, R.id.name});
                    // обновляем listview
                    lv.setAdapter(adapter);

                }
            });
        }
    }


    @SuppressLint("StaticFieldLeak")
    class RunChecklist extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.run(ListOpen.this);
        }

        @SuppressLint("SetTextI18n")
        protected String doInBackground(String... args) {
            // Создаем новый HashMap
            HashMap<String, String> map = new HashMap<>();

                map.put("uid", "\'"+get_uid+"\'");
                map.put("id_ch", "\'"+get_ch_id+"\'");
                map.put("comp_id", "\'"+get_comp_id+"\'");

                Log.d("Чеклист Ран ид юзера: ", get_uid);
                Log.d("Чеклист Ран ид комп: ", get_comp_id);
                Log.d("Чеклист Ран ид чек", get_ch_id);


            // получаем JSON строк с URL

            JSONObject json = jsonParser.makeHttpRequest(url_open_checklist, "GET", map);


            //Log.d("Все вопросы: ", json.toString());


            try {
                // Получаем SUCCESS тег для проверки статуса ответа сервера
                int success = json.getInt(TAG_SUCCESS);


                if (success == 1) {
                    // Получаем масив
                    question = json.getJSONArray(TAG_QUESTION);
                    // перебор
                    for (int i = 0; i < question.length(); i++) {

                        JSONObject c = question.getJSONObject(i);
                        String status = c.getString(TAG_STATUS);

                        if (status.equals("1")) {
                            // Сохраняем каждый json элемент в переменную
                            //String id = c.getString(TAG_PID_CH);
                        }
                    }
                } else {
                    Log.d("ОШИБКА", "ОШИБКА, не 1");
                }
            } catch (JSONException e) {
                Log.d("ОШИБКА", "ОШИБКА");
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
