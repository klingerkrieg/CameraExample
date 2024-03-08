package com.example.cameraexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TAG = "Camera";
    private ImageView imageView;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //recupero o local onde irei exibir a imagem
        this.imageView = (ImageView)this.findViewById(R.id.imageView);

        //solicito as permissões necessárias
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)   == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 100);
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)   == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)   == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }

    }

    public void abrirCamera(View v){
        //Chamo o aplicativo responsável pela camera
        //passando para ele o arquivo onde eu quero que a foto seja salva

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i(TAG, "IOException");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                Uri photoURI = null;
                try {
                    photoURI = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", createImageFile());
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, e.getMessage());
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        // Crio o arquivo onde quero que a imagem seja salva
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Quando o aplicativo de foto fechar, exibir a foto no local desejado
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                Bitmap mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                imageView.setImageBitmap(mImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, e.getMessage());
            }
        }
    }
}
