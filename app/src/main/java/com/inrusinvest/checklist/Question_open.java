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
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Question_open extends AppCompatActivity {

    private AlertDialog.Builder alertDialog;
    ImageView imageView;
    ImageView imagePhoto;
    Button btn1;
    Button btn2;
    ProgressBar progressBar;

    static final int REQUEST_CODE_PERMISSION_READ = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    PDialog pDialog = new PDialog();

    JSONParser jsonParser = new JSONParser();

    ArrayList<HashMap<String, String>> questList;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_QUESTION = "question";
    private static final String TAG_QUESTION_TEXT = "question_text";
    private static final String TAG_PHOTO_AVALIBLE = "photo_avalible";
    private static final String TAG_PHOTO = "photo";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_IF_ANSWER = "if_answer";
    private static final String TAG_ANSWER = "answer";
    private static final String url_open_question = "http://46.149.225.24:8081/checklist/question_open.php";
    private static final String url_answer = "http://46.149.225.24:8081/checklist/put_answer.php";


    private TextView tv;

    private String photo;
    private String answer;
    private String get_ch_id;
    private String get_uid;
    private String get_comp_id;
    private String get_qu_id;
    private String if_answer;
    private String get_answer;
    private File photoFile = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_open);

        tv = findViewById(R.id.text_question);
        imagePhoto = findViewById(R.id.image_question);
        imageView = new ImageView(getApplicationContext());
        btn1 = findViewById(R.id.okListBtn);
        btn2 = findViewById(R.id.noListBnt);
        progressBar = findViewById(R.id.top_progress_bar);

        questList = new ArrayList<>();

        new GetQuestionText().execute();
        //new PostPhoto().execute();

        alertDialog = new AlertDialog.Builder(Question_open.this);
        alertDialog.setTitle("Продолжить?");
        alertDialog.setMessage("Для продолжения нажмите \"Далее\", либо, если необходимо сделать фото, нажмите \"Фото\"");
        alertDialog.setPositiveButton("Далее", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Toast.makeText(Question_open.this, "Далее... Записываем ответ, отсылаем фото...", Toast.LENGTH_SHORT).show();
                //new PostPhoto().execute();
                goToQuestions();
            }
        });
        alertDialog.setNegativeButton(" Фото", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int permissionStatus = ContextCompat.checkSelfPermission(Question_open.this, android.Manifest.permission.CAMERA);

                if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                    //saveImage();
                    dispatchTakePictureIntent();
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

    /*private void dispatchTakePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmapImg = (Bitmap) extras.get("data");
            imageView.setImageBitmap(bitmapImg);
            new PostPhoto().execute();
        }
    }*/

    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_"+timeStamp+"_";
        File storegeDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storegeDir
        );

        String mCurrentPhotoPath = "file:" + image.getAbsolutePath();

        return image;
    }

    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            int permissionStatus = ContextCompat.checkSelfPermission(Question_open.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionStatus == PackageManager.PERMISSION_GRANTED) {

                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    Log.d("Ошибка Camera Storage:", "Ошибка в создании файла изображения");
                }

                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".com.inrusinvest.checklist.provider", photoFile);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(takePictureIntent, 1);
                }
            } else {
                ActivityCompat.requestPermissions(Question_open.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_PERMISSION_READ);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        new PostPhoto().execute();
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
                map.put("id", "\'" + get_qu_id + "\'");
                map.put("uid", "\'" + get_uid + "\'");
                map.put("ch_id", "\'" + get_ch_id + "\'");
                map.put("comp_id", "\'" + get_comp_id + "\'");
            }

            // получаем JSON строк с URL

            JSONObject json = jsonParser.makeHttpRequest(url_open_question, "GET", map);


            //Log.d("Все вопросы: ", json.toString());


            try {
                // Получаем SUCCESS тег для проверки статуса ответа сервера
                int success = json.getInt(TAG_SUCCESS);


                if (success == 1) {
                    // Получаем масив
                    JSONArray question = json.getJSONArray(TAG_QUESTION);
                    // перебор
                    for (int i = 0; i < question.length(); i++) {

                        JSONObject c = question.getJSONObject(i);

                        // Сохраняем каждый json элемент в переменную
                        //String id = c.getString(TAG_PID_CH);
                        String name = c.getString(TAG_QUESTION_TEXT);
                        if_answer = c.getString(TAG_IF_ANSWER);
                        get_answer = c.getString(TAG_ANSWER);
                        String photoUrl = c.getString(TAG_PHOTO);
                        if (!photoUrl.equals("null")) {
                            new DownloadImg(imagePhoto).execute(photoUrl);
                        }

                        photo = c.getString(TAG_PHOTO_AVALIBLE);
                        tv.setText(name);
                        Log.d("Текст вопроса: ", name);

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
                                        //new GetAnswer().execute();
                                        //goToQuestions();
                                        break;
                                    case "3":
                                        alertDialog.show();
                                        //new GetAnswer().execute();
                                        //goToQuestions();
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
                                        //new GetAnswer().execute();
                                        //goToQuestions();
                                        break;
                                    case "2":
                                        new GetAnswer().execute();
                                        goToQuestions();
                                        break;
                                    case "3":
                                        alertDialog.show();
                                        //new GetAnswer().execute();
                                        //goToQuestions();
                                        break;
                                }
                            }
                        });

                    }
                } else {

                    // добавляем каждый елемент в HashMap ключ => значение
                    String name = json.getString(TAG_MESSAGE);
                    tv.setText(name);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(String file_url) {
            // закрываем прогресс диалог
            pDialog.end();
            if (if_answer.equals("1")) {
                Toast.makeText(getApplicationContext(), "На данный вопрос уже дан ответ, его можно поменять, " +
                        "только выбрав противоположный ответ", Toast.LENGTH_LONG).show();

                if (if_answer.equals("1")) {
                    switch (get_answer) {
                        case "0":
                            btn2.setEnabled(false);
                            btn1.setEnabled(true);
                            break;
                        case "1":
                            btn1.setEnabled(false);
                            btn2.setEnabled(true);
                            break;
                        default:
                            btn1.setEnabled(true);
                            btn2.setEnabled(true);
                            break;

                    }
                }
            }
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

            map.put("uid", "\'" + get_uid + "\'");
            map.put("id_ch", "\'" + get_ch_id + "\'");
            map.put("comp_id", "\'" + get_comp_id + "\'");
            map.put("id_qu", "\'" + get_qu_id + "\'");
            map.put("answer", answer);

            Log.d("Чеклист Ран ид юзера: ", get_uid);
            Log.d("Чеклист Ран ид комп: ", get_comp_id);
            Log.d("Чеклист Ран ид чек", get_ch_id);


            // получаем JSON строк с URL

            JSONObject json = jsonParser.makeHttpRequest(url_answer, "GET", map);

            try {
                // Получаем SUCCESS тег для проверки статуса ответа сервера
                int success = json.getInt(TAG_SUCCESS);


                if (success == 1) {
                    Log.d("Ответ - ", "Статус ОК");

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


    @SuppressLint("StaticFieldLeak")
    private class DownloadImg extends AsyncTask<String, Void, Bitmap> {

        ImageView imagePhoto;

        private DownloadImg(ImageView imagePhoto) {
            this.imagePhoto = imagePhoto;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mPhoto = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mPhoto = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.d("Error open Photo", e.getMessage());
                e.printStackTrace();
            }
            return mPhoto;
        }

        protected void onPostExecute(Bitmap result) {
            imagePhoto.setImageBitmap(result);
        }
    }

    private void goToQuestions() {
        Intent intent = new Intent(Question_open.this, ListOpen.class);
        intent.putExtra("checklist_id", get_ch_id);
        intent.putExtra("uid_user", get_uid);
        intent.putExtra("company_id", get_comp_id);
        if (progressBar.getProgress() == 100 || progressBar.getProgress() == 0) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else Toast.makeText(getApplicationContext(), "Ошибка отправки фото.", Toast.LENGTH_LONG).show();
    }


    @SuppressLint("StaticFieldLeak")
    class PostPhoto extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //pDialog.run(Question_open.this);
            progressBar.setProgress(30);
        }

        @SuppressLint("SetTextI18n")
        protected String doInBackground(String... args) {
            /*imageView = new ImageView(getApplicationContext());
            imageView.setImageResource(R.drawable.ok);
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();*/

            /*
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

            // Получаем изображение из потока в виде байтов
            byte[] bytes = byteArrayOutputStream.toByteArray();

            String base = Base64.encodeToString(bytes, Base64.DEFAULT);*/

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), options);
            if (bitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ok);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imgBytes = baos.toByteArray();
                String imgBase = Base64.encodeToString(imgBytes, Base64.DEFAULT);
                progressBar.setProgress(50);
                // Создаем новый HashMap
                HashMap<String, String> map = new HashMap<>();
                map.put("img", imgBase);
                map.put("uid", get_uid);
                map.put("id_ch", get_ch_id);
                map.put("comp_id", get_comp_id);
                map.put("id_qu", get_qu_id);
                Log.d("Отправка Изображения ", imgBase);

                // получаем JSON строк с URL

                jsonParser.makeHttpRequest("http://46.149.225.24:8081/checklist/get_img.php", "POST", map);
                progressBar.setProgress(80);

            } else Log.d("Отправка фото", "Фото не было сделано");
            /*try {
                // Получаем SUCCESS тег для проверки статуса ответа сервера
                int success = json.getInt(TAG_SUCCESS);


                if (success == 1) {
                    Log.d("Ответ - ", "Статус ОК");

                } else {
                    Log.d("ОШИБКА", "ОШИБКА, не 1");
                }
            } catch (JSONException e) {
                Log.d("ОШИБКА", "ОШИБКА");
                e.printStackTrace();
            }*/

            return null;
        }


        protected void onPostExecute(String file_url) {
            // закрываем прогресс диалог
            //pDialog.end();
            progressBar.setProgress(100);
        }
    }
}
