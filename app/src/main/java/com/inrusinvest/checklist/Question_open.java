package com.inrusinvest.checklist;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Question_open extends AppCompatActivity {

    AlertDialog.Builder alertDialog;
    Uri outputFileUri;
    ImageView imageView;
    ImageView imagePhoto;

    static final int REQUEST_CODE_PERMISSION_READ = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    PDialog pDialog = new PDialog();

    JSONParser jsonParser = new JSONParser();

    ArrayList<HashMap<String, String>> questList;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_QUESTION = "question";
    private static final String TAG_PID = "id";
    private static final String TAG_QUESTION_TEXT = "question_text";
    private static final String TAG_STATUS = "status_quest";
    private static final String TAG_PHOTO_AVALIBLE = "photo_avalible";
    private static final String TAG_PHOTO = "photo";
    private static final String TAG_MESSAGE = "message";
    private static final String url_get_checklist = "http://46.149.225.24:8081/checklist/question_open.php";
    private static final String url_answer = "http://46.149.225.24:8081/checklist/put_answer.php";


    private JSONArray question = null;

    private TextView tv;

    private String photo;
    private String answer;
    private String get_ch_id;
    private String get_uid;
    private String get_comp_id;
    private String get_qu_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_open);

        tv = findViewById(R.id.text_question);
        imagePhoto = findViewById(R.id.image_question);
        questList =  new ArrayList<>();

        new GetQuestionText().execute();


        alertDialog = new AlertDialog.Builder(Question_open.this);
        alertDialog.setTitle("Продолжить?");
        alertDialog.setMessage("Для продолжения нажмите \"Далее\", либо, если необходимо сделать фото, нажмите \"Фото\"");
        alertDialog.setPositiveButton("Далее", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(Question_open.this, "Далее... Записываем ответ, отсылаем фото...", Toast.LENGTH_SHORT).show();

                goToQuestions();
            }
        });
        alertDialog.setNegativeButton(" Фото", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int permissionStatus = ContextCompat.checkSelfPermission(Question_open.this, android.Manifest.permission.CAMERA);

                if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                    saveImage();
                } else {
                    ActivityCompat.requestPermissions(Question_open.this, new String[]{Manifest.permission.CAMERA},
                            REQUEST_CODE_PERMISSION_READ);
                }
            }
        });
        alertDialog.setCancelable(true);
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Toast.makeText(Question_open.this, "Отмена...", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void saveImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(),
                "test.jpg");
        outputFileUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        try {
            Bitmap img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), outputFileUri);
            imageView.setImageBitmap(img);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            int permissionStatus = ContextCompat.checkSelfPermission(Question_open.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(outputFileUri);
                this.sendBroadcast(mediaScanIntent);
            } else {
                ActivityCompat.requestPermissions(Question_open.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_PERMISSION_READ);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    class GetQuestionText extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.run(Question_open.this);
        }

        @SuppressLint("SetTextI18n")
        protected String doInBackground(String... args) {
            // Создаем новый HashMap
            HashMap<String, String> map = new HashMap<>();

            Bundle arg = getIntent().getExtras();
            if (arg != null) {
                get_qu_id = arg.getString("id_question");
                get_uid = arg.getString("user_uid");
                get_ch_id = arg.getString("checklist_id");
                get_comp_id = arg.getString("company_id");
                map.put("id", get_qu_id);
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

                        if (status.equals("1")) {
                            // Сохраняем каждый json элемент в переменную
                            //String id = c.getString(TAG_PID_CH);
                            String name = c.getString(TAG_QUESTION_TEXT);
                            String photoUrl = c.getString(TAG_PHOTO);
                            if (!photoUrl.equals("null")) {
                                URL url = null;
                                try {
                                    url = new URL(photoUrl);
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }
                                assert url != null;
                                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                imagePhoto.setImageBitmap(bmp);
                            }

                            photo = c.getString(TAG_PHOTO_AVALIBLE);
                            tv.setText(name);
                            Log.d("Текст вопроса: ", name);
                            Button btn1 = findViewById(R.id.okListBtn);
                            Button btn2 = findViewById(R.id.noListBnt);
                            btn1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {  //если 0 - не нужно, 1 - нет - нужно, да - не нужно, 2 - нет - не нужно, да - нужно, 3 - нет - нужно, да - нужно
                                    answer = "1";
                                    switch (photo) {
                                        case "0":
                                            new GetAnswer().execute();
                                            goToQuestions();
                                            break;
                                        case "1":
                                            new GetAnswer().execute();
                                            goToQuestions();
                                            break;
                                        case "2":
                                            alertDialog.show();
                                            break;
                                        case "3":
                                            alertDialog.show();
                                            break;
                                    }
                                }
                            });

                            btn2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    answer = "0";
                                    switch (photo) {
                                        case "0":
                                            new GetAnswer().execute();
                                            goToQuestions();
                                            break;
                                        case "1":
                                            alertDialog.show();
                                            break;
                                        case "2":
                                            new GetAnswer().execute();
                                            goToQuestions();
                                            break;
                                        case "3":
                                            alertDialog.show();
                                            break;
                                    }
                                }
                            });
                        }
                    }
                } else {

                    // добавляем каждый елемент в HashMap ключ => значение
                    String name = json.getString(TAG_MESSAGE);
                    tv.setText(name);
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(String file_url) {
            // закрываем прогресс диалог
            pDialog.end();
        }
    }


    @SuppressLint("StaticFieldLeak")
    class GetAnswer extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.run(Question_open.this);
        }

        @SuppressLint("SetTextI18n")
        protected String doInBackground(String... args) {
            // Создаем новый HashMap
            HashMap<String, String> map = new HashMap<>();

            map.put("uid", "\'"+get_uid+"\'");
            map.put("id_ch", "\'"+get_ch_id+"\'");
            map.put("comp_id", "\'"+get_comp_id+"\'");
            map.put("id_qu", "\'"+get_qu_id+"\'");
            map.put("answer", answer);

            Log.d("Чеклист Ран ид юзера: ", get_uid);
            Log.d("Чеклист Ран ид комп: ", get_comp_id);
            Log.d("Чеклист Ран ид чек", get_ch_id);


            // получаем JSON строк с URL

            JSONObject json = jsonParser.makeHttpRequest(url_answer, "GET", map);

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

    private void goToQuestions () {
        Intent intent = new Intent(Question_open.this, ListOpen.class);
        intent.putExtra("checklist_id", get_ch_id);
        intent.putExtra("uid_user", get_uid);
        intent.putExtra("company_id", get_comp_id);
        startActivity(intent);
    }
 }
