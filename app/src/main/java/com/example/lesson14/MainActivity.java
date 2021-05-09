package com.example.lesson14;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private  static final int CODE_TAKE_PHOTO=2;
    private  static final int CODE_TAKE_PHOTO_NO_SAVE=1;
    private  static final int CODE_GET_PHOTO=3;


    private ImageView img_anh;
    private Button btn_Takephoto;
    private Button btn_Takephotonosave;
    private Button btn_Getphoto;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // co 2 cach de chup anh cam:
        //1 : chup va co luu anh
        //2 : chup va ko luu anh vao bo nho
        btn_Takephoto = findViewById(R.id.btn_take_photo);
        btn_Takephotonosave = findViewById(R.id.btn_take_photo_no_save);
        btn_Getphoto = findViewById(R.id.btn_get_photo);
        img_anh = findViewById(R.id.img);

        btn_Takephoto.setOnClickListener(this);
        btn_Takephotonosave.setOnClickListener(this);
        btn_Getphoto.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_take_photo:
                takePhoto();
            case R.id.btn_take_photo_no_save:
                takePhotoNoSave();
            case R.id.btn_get_photo:
                getPhoto();
        }

    }


    private void takePhotoNoSave() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try{
            startActivityForResult(intent,CODE_TAKE_PHOTO_NO_SAVE);
        }
        catch (ActivityNotFoundException e){

        }

    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                //URI la duong dan toi file de content provider su dung

                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.lesson14",
                        photoFile);

                //se lay dc URI file anh vua tao

                // truyen them photoURI vao intent , de he thong se luu anh sau khi chup vao file thong qua URI cua file
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CODE_TAKE_PHOTO);
            }
        }

    }

    private void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,CODE_GET_PHOTO);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CODE_TAKE_PHOTO_NO_SAVE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imagebBitmap = (Bitmap) extras.get("data");

            img_anh.setImageBitmap(imagebBitmap);

        }
        if(requestCode == CODE_TAKE_PHOTO && resultCode == RESULT_OK){
            // se kiem tra xem he thong da luu anh thanh cong
            Toast.makeText(this,"Da luu thanh cong", Toast.LENGTH_SHORT).show();
            Glide.with(MainActivity.this).load(currentPhotoPath).into(img_anh);
        }

        if(requestCode == CODE_GET_PHOTO && resultCode == RESULT_OK){
            // se kiem tra xem he thong da luu anh thanh cong
            try{
                Uri imageUri= data.getData();

                Glide.with(MainActivity.this).load(imageUri).into(img_anh);

            }catch (Exception e){

            }
            Toast.makeText(this,"Da lay anh thanh cong", Toast.LENGTH_SHORT).show();
        }

    }

    // tao ra file anh
    private File createImageFile() throws IOException {
        // Create an image file name
        // Cau lenh nay se tao ra String thoi gian hien tai
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        //
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        //lay ra file thu muc chau anh
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /*directory */
    );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}