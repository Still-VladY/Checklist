package com.inrusinvest.checklist;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListOpen extends Activity {

    AlertDialog.Builder alertDialog;
    Uri outputFileUri;
    ImageView imageView;
    View page;
    static final int REQUEST_CODE_PERMISSION_READ = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final List<View> pages = new ArrayList<View>();

        addText("Вопросик");
        pages.add(page);
        addText("Еще вопросик");
        pages.add(page);


        QuestionAdapter questionAdapter = new QuestionAdapter(pages);
        ViewPager viewPager = new ViewPager(this);
        viewPager.setAdapter(questionAdapter);
        viewPager.setCurrentItem(0);
        setContentView(viewPager);


        /*TextView tv = findViewById(R.id.text_organization);
        Bundle arg = getIntent().getExtras();
        if (arg != null) {
            String getcomp = Objects.requireNonNull(arg.get("company")).toString();
            tv.setText(getcomp);
        }*/

        alertDialog = new AlertDialog.Builder(ListOpen.this);
        alertDialog.setTitle("Продолжить?");
        alertDialog.setMessage("Для продолжения нажмите \"Далее\", либо, если необходимо сделать фото, нажмите \"Фото\"");
        alertDialog.setPositiveButton("Далее", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(ListOpen.this, "Далее...", Toast.LENGTH_SHORT).show();
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

            }
        });

    }

    private void addText(String s) {
        LayoutInflater inflater = LayoutInflater.from(this);

        page = inflater.inflate(R.layout.fragment_question, null);
        TextView textView = (TextView) page.findViewById(R.id.text_question);
        imageView = (ImageView) page.findViewById(R.id.image_question);
        textView.setText(s);
    }

    public void clickNo(View v) {
        alertDialog.show();

    }

    public void clickYes(View v) {

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
}
