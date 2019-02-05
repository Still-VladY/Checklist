package com.inrusinvest.checklist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
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

    ArrayList<HashMap<String, Object>> questList;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_SUCCESS_RUN_CH = "success_run_ch";
    private static final String TAG_SUCCESS_ALL_ANSWER = "success_all_answer";
    private static final String TAG_QUESTION = "question";
    private static final String TAG_PID = "id";
    private static final String TAG_PID_QUEST = "id_qu";
    private static final String TAG_QUESTION_NAME = "question_name";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_IF_ANSWER = "if_answer";
    private static final String url_get_checklist = "http://46.149.225.24:8081/checklist/get_question.php";
    //private static final String url_get_checklist = "http://192.168.100.23:8081/checklist/get_question.php";
    //private static final String TAG_ID_CHECKLIST = "id_checklist";

    JSONArray question = null;

    ListView lv;

    String get_ch_id;
    String get_uid;
    String get_comp_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arg = getIntent().getExtras();
        if (arg != null) {
            get_ch_id = arg.getString("checklist_id");
            get_uid = arg.getString("uid_user");
            get_comp_id = arg.getString("company_id");
        }

        setContentView(R.layout.activity_main);
        // new RunChecklist().execute();
        new GetQuestion().execute();
        questList = new ArrayList<>();

        lv = findViewById(R.id.parent_list_org);
    }

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
            int ifAnswImg = 0;

            map.put("id", get_ch_id);
            map.put("uid", "\'" + get_uid + "\'");

            map.put("id_ch", "\'" + get_ch_id + "\'");
            map.put("comp_id", "\'" + get_comp_id + "\'");

            Log.d("ИД чеклиста в вопр: ", get_ch_id);
            Log.d("ЮИД юзера в листах: ", get_uid);

            Log.d("Чеклист Ран ид комп: ", get_comp_id);
            Log.d("Чеклист Ран ид чек", get_ch_id);

            // получаем JSON строк с URL

            JSONObject json = jsonParser.makeHttpRequest(url_get_checklist, "GET", map);


            //Log.d("Все вопросы: ", json.toString());


            try {
                // Получаем SUCCESS тег для проверки статуса ответа сервера
                int success = json.getInt(TAG_SUCCESS);
                int success_run_ch = json.getInt(TAG_SUCCESS_RUN_CH);
                int success_all_answer = json.getInt(TAG_SUCCESS_ALL_ANSWER);

                if (success_all_answer == 1) {
                    Log.d("Все ответы: ", "Загружены");
                } else if (success_all_answer == 3) {
                    Log.d("Все ответы: ", "Уже были загружены");
                } else {
                    Log.d("Все ответы: ", "Ошибка загрузки");
                }

                if (success_run_ch == 1) {
                    Log.d("Чеклист ран: ", "Статус - Открыт");
                } else {
                    Log.d("Чеклист ран: ", "Уже был открыт");
                }

                if (success == 1) {
                    // Получаем масив
                    question = json.getJSONArray(TAG_QUESTION);
                    // перебор
                    for (int i = 0; i < question.length(); i++) {

                        JSONObject c = question.getJSONObject(i);
                        //String if_answer = c.getString(TAG_IF_ANSWER);

                        // Сохраняем каждый json элемент в переменную
                        //String id = c.getString(TAG_PID_CH);
                        String id = c.getString(TAG_PID_QUEST);
                        String name = c.getString(TAG_QUESTION_NAME);
                        String img = c.getString(TAG_IF_ANSWER);

                        LayoutInflater inflater = getLayoutInflater();
                        ViewGroup parent = findViewById(R.id.parentList);
                        View rowView = inflater.inflate(R.layout.list_item_img, parent, false);
                        ImageView ifImg = (ImageView) rowView.findViewById(R.id.img_img);

                        if (img.equals("0")) {
                            Log.d("Отвечен? ", "Нет");
                            ifImg.setImageResource(R.drawable.scuare);
                            ifAnswImg = R.drawable.scuare;
                        } else if (img.equals("1")) {
                            Log.d("Отвечен? ", "Да");
                            ifImg.setImageResource(R.drawable.ok);
                            ifAnswImg = R.drawable.ok;
                        }

                        Log.d("Вопросы: ", name);

                        HashMap<String, Object> map2list = new HashMap<String, Object>();

                        // добавляем каждый елемент в HashMap ключ => значение

                        map2list.put(TAG_QUESTION_NAME, name);
                        map2list.put(TAG_PID, id);
                        map2list.put(TAG_IF_ANSWER, ifAnswImg);
                        // добавляем HashList в ArrayList
                        Log.d("МАП: ", map2list.toString());

                        questList.add(map2list);

                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                String pid = ((TextView) view.findViewById(R.id.pid_img)).getText()
                                        .toString();
                                Log.d("Выбранный id вопроса:", pid);
                                Log.d("Выбранный id чеклиста: ", get_ch_id);
                                Intent intent = new Intent(ListOpen.this, Question_open.class);
                                intent.putExtra("id_question", pid);
                                intent.putExtra("checklist_id", get_ch_id);
                                intent.putExtra("user_uid", get_uid);
                                intent.putExtra("company_id", get_comp_id);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });
                    }
                } else {
                    HashMap<String, Object> map2list = new HashMap<String, Object>();

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
                            R.layout.list_item_img, new String[]{TAG_IF_ANSWER, TAG_PID,
                            TAG_QUESTION_NAME},
                            new int[]{R.id.img_img, R.id.pid_img, R.id.name_img});
                    // обновляем listview
                    lv.setAdapter(adapter);

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ListOpen.this, ChecklistGet.class);
        intent.putExtra("company_id", get_comp_id);
        intent.putExtra("uid_user", get_uid);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
