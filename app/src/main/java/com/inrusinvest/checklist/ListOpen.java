package com.inrusinvest.checklist;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListOpen extends Activity {

    AlertDialog.Builder alertDialog;
    Uri outputFileUri;
    ImageView imageView;
    View page;
    static final int REQUEST_CODE_PERMISSION_READ = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    List<View> pages = new ArrayList<View>();

    PDialog pDialog = new PDialog();

    JSONParser jsonParser = new JSONParser();

    ArrayList<HashMap<String, String>> questList;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_QUESTION = "question";
    private static final String TAG_PID_CH = "id_ch";
    private static final String TAG_PID_COMP_US = "id_comp_user";
    private static final String TAG_QUESTION_TEXT = "question_text";
    private static final String TAG_STATUS = "status_quest";
    private static final String TAG_PHOTO_AVALIBLE = "photo_avalible";
    private static final String TAG_MESSAGE = "message";
    //private static final String TAG_ID_CHECKLIST = "id_checklist";

    JSONArray question = null;

    ListView lv;
    String photo;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new GetQuestion().execute();

        alertDialog = new AlertDialog.Builder(ListOpen.this);
        alertDialog.setTitle("Продолжить?");
        alertDialog.setMessage("Для продолжения нажмите \"Далее\", либо, если необходимо сделать фото, нажмите \"Фото\"");
        alertDialog.setPositiveButton("Далее", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(ListOpen.this, "Далее... Записываем ответ, отсылаем фото...", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setNegativeButton(" Фото", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int permissionStatus = ContextCompat.checkSelfPermission(ListOpen.this, Manifest.permission.CAMERA);

                if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                    saveImage();
                } else {
                    ActivityCompat.requestPermissions(ListOpen.this, new String[]{Manifest.permission.CAMERA},
                            REQUEST_CODE_PERMISSION_READ);
                }
            }
        });
        alertDialog.setCancelable(true);
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Toast.makeText(ListOpen.this, "Отмена...", Toast.LENGTH_SHORT).show();
            }
        });




        /*
        Button yesBtn = findViewById(R.id.okListBtn);
        Button noBtn = findViewById(R.id.noListBnt);
        if (photo != null) {
            switch (photo) {
                case "0":
                    yesBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getApplicationContext(), "Записываем положительный ответ...", Toast.LENGTH_SHORT).show();
                        }
                    });
                    noBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getApplicationContext(), "Записываем отрицательный ответ", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case "1":
                    yesBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getApplicationContext(), "Записываем положительный ответ...", Toast.LENGTH_SHORT).show();
                        }
                    });
                    noBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.show();
                        }
                    });
                    break;
                case "2":
                    yesBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.show();
                        }
                    });
                    noBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getApplicationContext(), "Записываем отрицательный ответ...", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case "3":
                    yesBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.show();
                        }
                    });
                    noBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.show();
                        }
                    });
                    break;
            }
        } */
    }

    private void addText(String s) {
        LayoutInflater inflater = LayoutInflater.from(this);

        page = inflater.inflate(R.layout.fragment_question, null);
        TextView textView = (TextView) page.findViewById(R.id.text_question);
        imageView = (ImageView) page.findViewById(R.id.image_question);
        textView.setText(s);
    }

    public void noClick(View v) {
        switch (photo) {
            case "0": {
                Toast.makeText(getApplicationContext(), "Записываем отрицательный ответ", Toast.LENGTH_SHORT).show();
                break;
            }
            case  "1": {
                alertDialog.show();
                break;
            }
            case "2": {
                Toast.makeText(getApplicationContext(), "Записываем отрицательный ответ...", Toast.LENGTH_SHORT).show();
                break;
            }

            case "3": {
                alertDialog.show();
                break;
            }
        }

    }

    public void yesClick (View v) {
        switch (photo) {
            case "0": {
                Toast.makeText(getApplicationContext(), "Записываем положительный ответ...", Toast.LENGTH_SHORT).show();
                break;
            }
            case  "1": {
                Toast.makeText(getApplicationContext(), "Записываем положительный ответ...", Toast.LENGTH_SHORT).show();
                break;
            }
            case "2": {
                alertDialog.show();
                break;
            }

            case "3": {
                alertDialog.show();
                break;
            }
        }
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
            int permissionStatus = ContextCompat.checkSelfPermission(ListOpen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(outputFileUri);
                this.sendBroadcast(mediaScanIntent);
            } else {
                ActivityCompat.requestPermissions(ListOpen.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_PERMISSION_READ);
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    class GetQuestion extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.run(ListOpen.this);
        }


        protected String doInBackground(String... args) {
            // Создаем новый HashMap
            HashMap<String, String> map = new HashMap<>();

            String get_ch_id = null;
            Bundle arg = getIntent().getExtras();
            if (arg != null) {
                get_ch_id = arg.getString("checklist_id");
                //System.out.println("Ид checklist- " + get_ch_id);
            }
            map.put("id", get_ch_id);
            // получаем JSON строк с URL
            String url_get_checklist = "http://46.149.225.24:8081/checklist/get_question.php";
            JSONObject json = jsonParser.makeHttpRequest(url_get_checklist, "GET", map);


            Log.d("All Questions: ", json.toString());

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
                        //System.out.println("Статус - " + status);

                        if (status.equals("1")) {
                            // Сохраняем каждый json элемент в переменную
                            //String id = c.getString(TAG_PID_CH);
                            String name = c.getString(TAG_QUESTION_TEXT);
                            photo = c.getString(TAG_PHOTO_AVALIBLE);

                            Log.d("Вопросы - ", name);

                            addText(name);
                            pages.add(page);
                        }
                    }
                } else {
                    HashMap<String, String> map2list = new HashMap<String, String>();

                    // добавляем каждый елемент в HashMap ключ => значение
                    String name = json.getString(TAG_MESSAGE);
                    map2list.put(TAG_QUESTION_TEXT, name);
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
                    QuestionAdapter questionAdapter = new QuestionAdapter(pages);
                    ViewPager viewPager = new ViewPager(ListOpen.this);
                    viewPager.setAdapter(questionAdapter);
                    viewPager.setCurrentItem(0);
                    setContentView(viewPager);
                }
            });
        }
    }
}
